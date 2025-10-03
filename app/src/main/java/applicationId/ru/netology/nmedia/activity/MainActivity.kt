package applicationId.ru.netology.nmedia.activity

import applicationId.ru.netology.nmedia.viewModel.PostViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.adapter.PostAdapter
import applicationId.ru.netology.nmedia.databinding.ActivityMainBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepositoryMemory
import applicationId.ru.netology.nmedia.viewModel.PostViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepositoryMemory())
    }
    private lateinit var adapter: PostAdapter
    private var selectedPost: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "onCreate")

        // Инициализация адаптера
        adapter = PostAdapter(object : PostAdapter.OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.share(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                // Если удаляем пост, который редактируется, выходим из режима редактирования
                if (viewModel.editablePost.value?.id == post.id) {
                    viewModel.cancelEditing()
                }
            }

            override fun onEdit(post: Post) {
                viewModel.setPostForEditing(post)
            }
        })

        // Настройка RecyclerView
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter

        // Подписка на изменения данных
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        // Подписка на режим редактирования
        viewModel.editablePost.observe(this) { post ->
            if (post != null) {
                // Показываем элементы редактирования (заголовок и крестик)
                binding.editingModeGroup.visibility = View.VISIBLE
                binding.content.setText(post.content)
                // Перемещаем курсор в конец текста
                binding.content.setSelection(binding.content.text.length)
                // Меняем текст кнопки на "Обновить" в режиме редактирования
                binding.save.text = getString(R.string.update_button_text)
            } else {
                // Скрываем элементы редактирования
                binding.editingModeGroup.visibility = View.GONE
                binding.content.text.clear()
                // Возвращаем текст кнопки на "Сохранить" в режиме создания
                binding.save.text = getString(R.string.save_button_text)
            }
        }

        // Обработчик кнопки отмены редактирования (крестик)
        binding.buttonCancel.setOnClickListener {
            viewModel.cancelEditing()
        }

        // Регистрируем контекстное меню для RecyclerView
        registerForContextMenu(binding.list)

        // Обработчик кнопки Save/Update для добавления нового поста или обновления существующего
        binding.save.setOnClickListener {
            val content = binding.content.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.save(content)
                binding.content.text.clear()
            }
        }
    }

    // Создаем контекстное меню
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.list) {
            menuInflater.inflate(R.menu.menu_post, menu)
        }
    }

    // Обрабатываем выбор пункта меню
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                selectedPost?.let { post ->
                    viewModel.setPostForEditing(post)
                }
                true
            }
            R.id.remove -> {
                selectedPost?.let { post ->
                    viewModel.removeById(post.id)
                    // Если удаляем пост, который редактируется, выходим из режима редактирования
                    if (viewModel.editablePost.value?.id == post.id) {
                        viewModel.cancelEditing()
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
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