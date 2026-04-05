package applicationId.ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import applicationId.ru.netology.nmedia.adapter.PointAdapter
import applicationId.ru.netology.nmedia.databinding.FragmentPointsListBinding
import applicationId.ru.netology.nmedia.util.observeInLifecycle
import applicationId.ru.netology.nmedia.viewModel.PointViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PointsListFragment : Fragment() {

    private var _binding: FragmentPointsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PointViewModel by activityViewModels()

    private lateinit var adapter: PointAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPointsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PointAdapter { point ->
            viewModel.selectPoint(point)
            parentFragmentManager.popBackStack()
        }

        binding.list.adapter = adapter

        viewModel.data.observeInLifecycle(viewLifecycleOwner) { points ->
            adapter.submitList(points)
            binding.empty.visibility = if (points.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}