package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import applicationId.ru.netology.nmedia.dto.Point
import applicationId.ru.netology.nmedia.repository.PointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PointViewModel @Inject constructor(
    private val repository: PointRepository,
) : ViewModel() {

    val data: StateFlow<List<Point>> = repository.data()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _selectedPoint = MutableSharedFlow<Point>(extraBufferCapacity = 1)
    val selectedPoint = _selectedPoint.asSharedFlow()

    fun selectPoint(point: Point) {
        _selectedPoint.tryEmit(point)
    }

    fun save(
        id: Long = 0,
        title: String,
        description: String,
        lat: Double,
        lon: Double,
    ) = viewModelScope.launch {
        repository.save(
            Point(
                id = id,
                title = title,
                description = description,
                lat = lat,
                lon = lon,
            )
        )
    }

    fun removeById(id: Long) = viewModelScope.launch {
        repository.removeById(id)
    }
}