package applicationId.ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.databinding.FragmentPostBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.entity.PostEntity
import applicationId.ru.netology.nmedia.viewModel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by activityViewModels()
    private val args: PostFragmentArgs by navArgs()

    @Inject
    lateinit var postDao: PostDao

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

        // Кнопка "назад" в toolbar
        (requireActivity() as? AppCompatActivity)
            ?.supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)

        // Подписываемся на конкретный пост из Room
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postDao.observeById(postId)
                    .map { entity -> entity?.toDto() }
                    .collectLatest { post ->
                        // Если пост исчез (удалили/нет в базе) — уходим назад
                        if (post == null) {
                            findNavController().navigateUp()
                            return@collectLatest
                        }
                        setupPost(post)
                    }
            }
        }
    }

    private fun setupPost(post: Post) = with(binding) {
        // Если у тебя в layout нет прогресса — эту строку можно удалить
        // progress.isVisible = false

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

        like.setOnClickListener { viewModel.likeById(post.id) }
        share.setOnClickListener { sharePost(post.content) }
        menu.setOnClickListener { showMenu(post, it) }

        videoGroup.isVisible = !post.video.isNullOrEmpty()
        if (!post.video.isNullOrEmpty()) {
            playButton.setOnClickListener { playVideo(post.video) }
            videoGroup.setOnClickListener { playVideo(post.video) }
        }
    }

    private fun showMenu(post: Post, view: View) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.menu_post)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        val action = PostFragmentDirections
                            .actionPostFragmentToNewPostFragment(post)
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