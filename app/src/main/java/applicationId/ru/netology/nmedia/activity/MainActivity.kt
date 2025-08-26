package applicationId.ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.ActivityMainBinding
import applicationId.ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.util.NumberFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("OnCreate", "OnCreate")
        Log.d("MainActivity", "MainActivity hashCode ${this.hashCode()}")



        val viewModel: PostViewModel by viewModels()
        Log.d("viewModel", "viewModel hashCode ${this.hashCode()}")

        viewModel.data.observe(this) { post ->
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                shares.text = NumberFormatter.formatCount(post.shares)
                views.text = NumberFormatter.formatCount(post.views)
                likes.text = NumberFormatter.formatCount(post.likes)

                if (post.likedByMe) {
                    like.setImageResource(R.drawable.love_like_heart_icon_196980)
                } else {
                    like.setImageResource(R.drawable.heart)
                }
            }
        }

        binding.like.setOnClickListener {
            viewModel.like()
        }

        binding.share.setOnClickListener {
            viewModel.share()
        }

        binding.root.setOnClickListener {
            Log.d("MainActivity", "Root container clicked")
        }

        binding.avatar.setOnClickListener {
            Log.d("MainActivity", "Avatar clicked")
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