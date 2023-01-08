package com.hameed.danish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hameed.danish.databinding.UserItemBinding
import com.hameed.danish.domain.User

class UserAdapter(private val onItemClicked: (User) -> Unit):ListAdapter<User, UserAdapter.UserViewHolder>(
    DiffCallback
) {
    class UserViewHolder(private var binding:UserItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User){
            binding.user = user
            binding.executePendingBindings()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
       return UserViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)

        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }

        holder.bind(current)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }


    }
}