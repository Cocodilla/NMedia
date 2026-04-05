package applicationId.ru.netology.nmedia.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.observeInLifecycle(
    owner: LifecycleOwner,
    block: (T) -> Unit,
) {
    owner.lifecycleScope.launch {
        collect { block(it) }
    }
}