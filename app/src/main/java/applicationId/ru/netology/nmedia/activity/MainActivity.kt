package applicationId.ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.adapter.PostAdapter
import applicationId.ru.netology.nmedia.databinding.ActivityMainBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepositoryMemory
import applicationId.ru.netology.nmedia.viewModel.PostViewModel
import applicationId.ru.netology.nmedia.viewModel.PostViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepositoryMemory())
    }
    private lateinit var adapter: PostAdapter

    private val newPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val content = data.getStringExtra(NewPostActivity.EXTRA_CONTENT)
                val postId = data.getLongExtra(NewPostActivity.EXTRA_POST_ID, 0L)

                if (!content.isNullOrEmpty()) {
                    if (postId > 0L) {
                        // Редактирование существующего поста
                        viewModel.edit(postId, content)
                    } else {
                        // Создание нового поста
                        viewModel.save(content)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "onCreate")
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
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
                openEditPostScreen(post)
            }

            override fun onVideoPlay(post: Post) {
                playVideo(post.video)
            }
        })

        binding.list.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }

    private fun setupClickListeners() {
        binding.add.setOnClickListener {
            openNewPostScreen()
        }
    }

    private fun openNewPostScreen() {
        val intent = Intent(this, NewPostActivity::class.java)
        newPostLauncher.launch(intent)
    }

    private fun openEditPostScreen(post: Post) {
        val intent = Intent(this, NewPostActivity::class.java).apply {
            putExtra(NewPostActivity.EXTRA_POST, post)
        }
        newPostLauncher.launch(intent)
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val chooser = Intent.createChooser(intent, getString(R.string.chooser_play_video))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            }
        }
    }

    // Методы жизненного цикла...
    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }
}