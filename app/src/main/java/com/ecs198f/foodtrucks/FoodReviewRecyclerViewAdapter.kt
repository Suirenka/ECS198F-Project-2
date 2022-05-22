package com.ecs198f.foodtrucks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecs198f.foodtrucks.databinding.FoodItemBinding
import com.ecs198f.foodtrucks.databinding.FoodReviewBinding

class FoodReviewRecyclerViewAdapter(private var items: List<FoodReview>):
    RecyclerView.Adapter<FoodReviewRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(val binding: FoodReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FoodReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodReviewRecyclerViewAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let {
            holder.binding.apply {
                foodReviewId.text = it.authorName
                foodReviewContent.text = it.content
                Glide.with(root).load(it.authorAvatarUrl).into(userIcon)
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(items: List<FoodReview>) {
        this.items = items
        notifyDataSetChanged()
    }

}
