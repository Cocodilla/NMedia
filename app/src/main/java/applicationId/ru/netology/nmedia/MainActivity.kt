package applicationId.ru.netology.nmedia

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import applicationId.ru.netology.nmedia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val post = Post (
            id = 1,
            published = "Нетология. Университет интернет-профессий будущего",
            content = "Привет! Это новая Нетология! Когда-то Нетология начичиналась с интенсивов поонлайн-маркетингу.Затем плявились курсы по дизайну,разработке,аналитике,управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остается с нами: мы верим , что в каждом уже есть сила, которая заставляет хотеть больше,целиться выше, бежать быстрее. Наша миссия- помочь встать на путь роста и начать цепочку перемен.",
            author =  "21 мая в 19.00",
            likes = 999999999,
            likedByMe = false,
            shares = 999,
            views = 999
        )

        fun formatCount(count: Int): String {
            return when {
                count < 1000 -> count.toString()
                count < 10_000 -> {
                    val thousands = count / 1000
                    val hundreds = (count % 1000) / 100
                    if (hundreds == 0) "${thousands}K" else "${thousands}.${hundreds}K"
                }
                count < 1_000_000 -> "${count / 1000}K"
                else -> {
                    val millions = count / 1_000_000
                    val hundredThousands = (count % 1_000_000) / 100_000
                    if (hundredThousands == 0) "${millions}M" else "${millions}.${hundredThousands}M"
                }
            }
        }

        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            shares.text = formatCount(post.shares)
            views.text = formatCount(post.views)
            likes.text = formatCount(post.likes)
            like.setImageResource(R.drawable.heart)

            like.setOnClickListener { view ->
                post.likedByMe = !post.likedByMe
                post.likes = if (post.likedByMe) post.likes + 1 else post.likes - 1
                like.setImageResource(
                    if (post.likedByMe) R.drawable.love_like_heart_icon_196980
                    else R.drawable.heart
                )
                likes.text = formatCount(post.likes)
            }

            share.setOnClickListener { view ->
                post.shares += 1
                shares.text = formatCount(post.shares)
            }
            root.setOnClickListener {
                Log.d("MainActivity", "Root container clicked")
            }

            avatar.setOnClickListener {
                Log.d("MainActivity", "Avatar clicked")
            }
            }
            }
        }





