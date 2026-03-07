package applicationId.ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.adapter.PostAdapter
import applicationId.ru.netology.nmedia.adapter.PostsLoadStateAdapter
import applicationId.ru.netology.nmedia.databinding.FragmentFeedBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PostAdapter

    // не показывать одинаковую ошибку бесконечно
    private var lastErrorMessage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupSwipeRefresh()
        observePaging()
        setupClicks()
    }

    private fun setupRecyclerView() {

        adapter = PostAdapter(object : PostAdapter.OnInteractionListener {

            override fun onLike(post: Post) =
                viewModel.likeById(post.id)

            override fun onShare(post: Post) =
                sharePost(post.content)

            override fun onRemove(post: Post) =
                viewModel.removeById(post.id)

            override fun onEdit(post: Post) {
                val action =
                    FeedFragmentDirections
                        .actionFeedFragmentToNewPostFragment(post)
                findNavController().navigate(action)
            }

            override fun onVideoPlay(post: Post) =
                playVideo(post.video)

            override fun onPostClick(post: Post) {
                val action =
                    FeedFragmentDirections
                        .actionFeedFragmentToPostFragment(post.id)
                findNavController().navigate(action)
            }
        })

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())

            adapter = this@FeedFragment.adapter.withLoadStateFooter(
                footer = PostsLoadStateAdapter {
                    this@FeedFragment.adapter.retry()
                }
            )
        }
    }

    private fun setupSwipeRefresh() {

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun observePaging() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                    viewModel.data.collectLatest {
                        adapter.submitData(it)
                    }
                }

                launch {

                    adapter.loadStateFlow.collectLatest { state ->

                        binding.swipeRefresh.isRefreshing =
                            state.refresh is LoadState.Loading

                        val errorState = when {
                            state.refresh is LoadState.Error ->
                                state.refresh as LoadState.Error

                            state.append is LoadState.Error ->
                                state.append as LoadState.Error

                            else -> null
                        }

                        errorState?.let {

                            val message =
                                it.error.message
                                    ?: getString(R.string.error_unknown)

                            // предотвращаем повторный показ одной и той же ошибки
                            if (message == lastErrorMessage) return@let
                            lastErrorMessage = message

                            Snackbar.make(
                                binding.root,
                                message,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.retry) {
                                adapter.retry()
                            }.show()
                        }
                    }
                }
            }
        }
    }

    private fun setupClicks() {

        binding.add.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment
            )
        }
    }

    private fun sharePost(content: String) {

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }

        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.chooser_share_post)
            )
        )
    }

    private fun playVideo(videoUrl: String?) {

        videoUrl?.let { url ->

            val intent = Intent(Intent.ACTION_VIEW, url.toUri())

            val chooser = Intent.createChooser(
                intent,
                getString(R.string.chooser_play_video)
            )

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(chooser)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}