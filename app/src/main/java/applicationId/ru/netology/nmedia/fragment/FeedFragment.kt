package applicationId.ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.adapter.PostAdapter
import applicationId.ru.netology.nmedia.databinding.FragmentFeedBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.viewModel.PostViewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    // ✅ Вот так правильно получаем ViewModel без Factory
    private val viewModel: PostViewModel by lazy {
        ViewModelProvider(requireActivity())[PostViewModel::class.java]
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

        Log.d("FeedFragment", "onViewCreated")

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(object : PostAdapter.OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onShare(post: Post) {
                sharePost(post.content)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragmentToNewPostFragment(post)
                findNavController().navigate(action)
            }

            override fun onVideoPlay(post: Post) {
                playVideo(post.video)
            }

            override fun onPostClick(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragmentToPostFragment(post.id)
                findNavController().navigate(action)
            }
        })

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupClickListeners() {
        binding.add.setOnClickListener {
            openNewPostScreen()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            viewModel.loadPosts()
        }
    }

    private fun openNewPostScreen() {
        findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
    }

    private fun sharePost(content: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        val chooser = Intent.createChooser(intent, getString(R.string.chooser_share_post))
        startActivity(chooser)
    }

    private fun playVideo(videoUrl: String?) {
        videoUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            val chooser = Intent.createChooser(intent, getString(R.string.chooser_play_video))
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(chooser)
            } else {
                Log.e("FeedFragment", "No app found to handle video URL: $url")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
