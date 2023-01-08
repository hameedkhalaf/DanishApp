package com.hameed.danish
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hameed.danish.databinding.OrderItemBinding
import com.hameed.danish.domain.Order

class OrderAdapter:ListAdapter<Order, OrderAdapter.OrderViewHolder>(DiffCallback) {
    class OrderViewHolder(private var binding: OrderItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order){
            binding.order = order
            binding.executePendingBindings()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }


    }
}