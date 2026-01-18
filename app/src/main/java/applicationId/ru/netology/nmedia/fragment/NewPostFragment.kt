package applicationId.ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import applicationId.ru.netology.nmedia.databinding.FragmentNewPostBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.viewModel.PostViewModel


class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by lazy {
        ViewModelProvider(requireActivity())[PostViewModel::class.java]
    }

    private val args: NewPostFragmentArgs by navArgs()
    private var editingPost: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем пост из аргументов (может быть null)
        editingPost = args.post

        setupPostData()
        setupClickListeners()
    }

    private fun setupPostData() {
        val post = editingPost
        if (post != null) {
            // Режим редактирования
            binding.editingTitle.visibility = View.VISIBLE
            binding.buttonCancel.visibility = View.VISIBLE
            binding.content.setText(post.content)
            binding.content.setSelection(binding.content.text.length)
        } else {
            // Режим создания нового поста
            binding.editingTitle.visibility = View.GONE
            binding.buttonCancel.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.save.setOnClickListener {
            saveOrUpdatePost()
        }
    }

    private fun saveOrUpdatePost() {
        val content = binding.content.text.toString().trim()
        if (content.isNotEmpty()) {
            val post = editingPost
            if (post != null) {
                // Редактирование существующего поста
                viewModel.edit(post.id, content)
            } else {
                // Создание нового поста
                viewModel.save(content)
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}