package applicationId.ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import applicationId.ru.netology.nmedia.App
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.adapter.PostAdapter
import applicationId.ru.netology.nmedia.databinding.FragmentFeedBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepositoryImpl
import applicationId.ru.netology.nmedia.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    // чтобы snackbar не показывался бесконечно на одно и то же состояние (например, после поворота)
    private var lastErrorMessage: String? = null

    private val viewModel: PostViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = requireActivity().application as App
                val dao = app.db.postDao()
                val repo = PostRepositoryImpl(dao)
                return PostViewModel(repo) as T
            }
        }
    }

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
        setupObservers()
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
            viewModel.loadPosts()
        }
    }

    private fun setupObservers() {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.loading

            val err = state.error ?: return@observe
            val msg = err.message ?: getString(R.string.error_unknown)

            // не показываем один и тот же snackbar повторно
            if (lastErrorMessage == msg) return@observe
            lastErrorMessage = msg

            Snackbar.make(binding.root, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { viewModel.retry() }
                .show()
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
