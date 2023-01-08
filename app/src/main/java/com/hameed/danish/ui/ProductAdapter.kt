package com.hameed.danish.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hameed.danish.databinding.ListItemBinding
import com.hameed.danish.domain.Product

class ProductAdapter(private val onItemClicked:(Product)->Unit):ListAdapter<Product,ProductAdapter.ProductViewHolder>(DiffCallback) {
    class ProductViewHolder(private var binding:ListItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product){
            binding.product = product
            binding.executePendingBindings()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context)))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }


    }
}