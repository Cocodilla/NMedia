package applicationId.ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import applicationId.ru.netology.nmedia.databinding.ActivityNewPostBinding
import applicationId.ru.netology.nmedia.dto.Post

class NewPostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POST = "post"
        const val EXTRA_CONTENT = "content"
        const val EXTRA_VIDEO = "video"
        const val EXTRA_POST_ID = "post_id"
    }

    private lateinit var binding: ActivityNewPostBinding
    private var currentPost: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPostData()
        setupClickListeners()
    }

    private fun setupPostData() {
        currentPost = intent.getParcelableExtra(EXTRA_POST)

        currentPost?.let { post ->
            // Режим редактирования
            binding.editingTitle.visibility = View.VISIBLE
            binding.buttonCancel.visibility = View.VISIBLE
            binding.content.setText(post.content)
            binding.content.setSelection(binding.content.text.length)
        } ?: run {
            // Режим создания нового поста
            binding.editingTitle.visibility = View.GONE
            binding.buttonCancel.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.save.setOnClickListener {
            saveOrUpdatePost()
        }
    }

    private fun saveOrUpdatePost() {
        val content = binding.content.text.toString().trim()
        if (content.isNotEmpty()) {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_CONTENT, content)
                currentPost?.let { post ->
                    putExtra(EXTRA_POST_ID, post.id)
                }
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}