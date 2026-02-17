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

        editingPost = args.post // nullable

        editingPost?.let { post ->
            binding.editingTitle.visibility = View.VISIBLE
            binding.buttonCancel.visibility = View.VISIBLE
            binding.content.setText(post.content)
            binding.content.setSelection(binding.content.text.length)
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.save.setOnClickListener {
            val content = binding.content.text.toString().trim()
            if (content.isEmpty()) return@setOnClickListener

            val post = editingPost
            if (post == null) viewModel.save(content)
            else viewModel.edit(post.id, content)

            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
