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

    private var lastErrorMessage: String? = null
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupPagingCollectors()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(object : PostAdapter.OnInteractionListener {
            override fun onLike(post: Post) = viewModel.likeById(post.id)
            override fun onShare(post: Post) = sharePost(post.content)
            override fun onRemove(post: Post) = viewModel.removeById(post.id)
            override fun onEdit(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragmentToNewPostFragment(post)
                findNavController().navigate(action)
            }
            override fun onVideoPlay(post: Post) = playVideo(post.video)
            override fun onPostClick(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragmentToPostFragment(post.id)
                findNavController().navigate(action)
            }
        })

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@FeedFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupPagingCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {


                launch {
                    viewModel.data.collectLatest { pagingData ->
                        adapter.submitData(pagingData)
                    }
                }

                //  Состояния загрузки (прогресс/ошибка)
                launch {
                    adapter.loadStateFlow.collectLatest { state ->
                        // Показываем "крутилку" только когда идет refresh
                        binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading

                        // Ошибка может быть в refresh или append
                        val errorState = when {
                            state.refresh is LoadState.Error -> state.refresh as LoadState.Error
                            state.append is LoadState.Error -> state.append as LoadState.Error
                            state.prepend is LoadState.Error -> state.prepend as LoadState.Error
                            else -> null
                        } ?: return@collectLatest

                        val msg = errorState.error.message ?: getString(R.string.error_unknown)
                        if (lastErrorMessage == msg) return@collectLatest
                        lastErrorMessage = msg

                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry) { adapter.retry() }
                            .show()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }

    private fun sharePost(content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_share_post)))
    }

    private fun playVideo(videoUrl: String?) {
        videoUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            val chooser = Intent.createChooser(intent, getString(R.string.chooser_play_video))
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