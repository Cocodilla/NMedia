package applicationId.ru.netology.nmedia.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.FragmentMapBinding
import applicationId.ru.netology.nmedia.dto.Point
import applicationId.ru.netology.nmedia.viewModel.PointViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PointViewModel by activityViewModels()

    private var googleMap: GoogleMap? = null
    private val markerById = mutableMapOf<Long, Marker>()
    private var currentPoints: List<Point> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapContainer)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.openPointsList.setOnClickListener {
            findNavController().navigate(R.id.pointsListFragment)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.data.collect { points ->
                        currentPoints = points
                        renderPoints(points)
                    }
                }

                launch {
                    viewModel.selectedPoint.collect { point ->
                        moveToPoint(point)
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val defaultLocation = LatLng(55.751244, 37.618423)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))

        map.setOnMapClickListener { latLng ->
            showPointDialog(
                point = null,
                lat = latLng.latitude,
                lon = latLng.longitude,
            )
        }

        map.setOnMarkerClickListener { marker ->
            val point = marker.tag as? Point ?: return@setOnMarkerClickListener false
            showMarkerActions(point)
            true
        }

        renderPoints(currentPoints)
    }

    private fun renderPoints(points: List<Point>) {
        val map = googleMap ?: return

        markerById.clear()
        map.clear()

        points.forEach { point ->
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(point.lat, point.lon))
                    .title(point.title)
                    .snippet(point.description)
            )
            marker?.tag = point
            if (marker != null) {
                markerById[point.id] = marker
            }
        }
    }

    private fun moveToPoint(point: Point) {
        val map = googleMap ?: return
        val latLng = LatLng(point.lat, point.lon)

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        markerById[point.id]?.showInfoWindow()
    }

    private fun showMarkerActions(point: Point) {
        AlertDialog.Builder(requireContext())
            .setTitle(point.title)
            .setMessage(point.description)
            .setPositiveButton(R.string.edit) { _, _ ->
                showPointDialog(
                    point = point,
                    lat = point.lat,
                    lon = point.lon,
                )
            }
            .setNeutralButton(R.string.delete) { _, _ ->
                viewModel.removeById(point.id)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPointDialog(
        point: Point?,
        lat: Double,
        lon: Double,
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_point, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.titleInput)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.descriptionInput)

        if (point != null) {
            titleInput.setText(point.title)
            descriptionInput.setText(point.description)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (point == null) R.string.create_point else R.string.edit_point)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val title = titleInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()

                if (title.isBlank()) return@setPositiveButton

                viewModel.save(
                    id = point?.id ?: 0,
                    title = title,
                    description = description,
                    lat = lat,
                    lon = lon,
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}