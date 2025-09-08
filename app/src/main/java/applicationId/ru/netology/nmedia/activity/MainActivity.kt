package applicationId.ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import applicationId.ru.netology.nmedia.adapter.PostAdapter
import applicationId.ru.netology.nmedia.databinding.ActivityMainBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.viewModel.PostViewModel



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "onCreate")
        Log.d("MainActivity", "MainActivity hashCode ${this.hashCode()}")
        Log.d("MainActivity", "ViewModel hashCode ${viewModel.hashCode()}")

        // Инициализация адаптера
        adapter = PostAdapter(
            object : PostAdapter.OnLikeListener {
                override fun onLike(post: Post) {
                    viewModel.like(post.id)
                }
            },
            object : PostAdapter.OnShareListener {
                override fun onShare(post: Post) {
                    viewModel.share(post.id)
                }
            }
        )

        // Настройка RecyclerView
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.postsRecyclerView.adapter = adapter

        // Подписка на изменения данных
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }


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