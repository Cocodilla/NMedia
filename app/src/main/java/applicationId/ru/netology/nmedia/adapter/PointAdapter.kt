package applicationId.ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.databinding.ItemPointBinding
import applicationId.ru.netology.nmedia.dto.Point

class PointAdapter(
    private val onClick: (Point) -> Unit,
) : ListAdapter<Point, PointAdapter.PointViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val binding = ItemPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PointViewHolder(
        private val binding: ItemPointBinding,
        private val onClick: (Point) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(point: Point) {
            binding.title.text = point.title
            binding.description.text = point.description
            binding.coordinates.text = "${point.lat}, ${point.lon}"

            binding.root.setOnClickListener {
                onClick(point)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Point>() {
        override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem == newItem
        }
    }
}