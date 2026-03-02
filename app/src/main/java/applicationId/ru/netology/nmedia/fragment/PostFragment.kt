package applicationId.ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.FragmentPostBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.viewModel.PostViewModel

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()
    private val args: PostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = args.postId

        if (viewModel.data.value.isNullOrEmpty()) {
            viewModel.loadPosts()
        }

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId }
            if (post == null) {
                findNavController().navigateUp()
                return@observe
            }
            setupPost(post)
        }

        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)
            ?.supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupPost(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = post.likes.toString()
            shareCount.text = post.shares.toString()
            viewsCount.text = post.views.toString()

            like.setImageResource(
                if (post.likedByMe) R.drawable.love_like_heart_icon_196980
                else R.drawable.like_selector
            )

            like.setOnClickListener {
                viewModel.likeById(post.id)
            }

            share.setOnClickListener {
                sharePost(post.content)
            }

            menu.setOnClickListener {
                showMenu(post, it)
            }

            if (post.video.isNullOrEmpty()) {
                videoGroup.visibility = View.GONE
            } else {
                videoGroup.visibility = View.VISIBLE
                playButton.setOnClickListener { playVideo(post.video) }
                videoGroup.setOnClickListener { playVideo(post.video) }
            }
        }
    }

    private fun showMenu(post: Post, view: View) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.menu_post)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        val action = PostFragmentDirections.actionPostFragmentToNewPostFragment(post)
                        findNavController().navigate(action)
                        true
                    }
                    R.id.remove -> {
                        viewModel.removeById(post.id)
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }.show()
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