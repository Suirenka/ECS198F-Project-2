package com.ecs198f.foodtrucks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ecs198f.foodtrucks.databinding.FoodItemBinding

class FoodItemListRecyclerViewAdapter(private var items: List<FoodItem>):
    RecyclerView.Adapter<FoodItemListRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: FoodItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FoodItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let {
            holder.binding.apply {
                foodItemName.text = it.name
                foodItemPrice.text = "\$${it.price} (${if (it.taxIncluded) "tax included" else "+ tax"})"
                foodItemDescription.text = it.description
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(items: List<FoodItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}