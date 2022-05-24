package com.ecs198f.foodtrucks

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter



class DisConeectedAdap(fragment: Fragment, list1: List<FoodItem>): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    val list1 = list1

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment

        when(position){
            0 ->{
                fragment = FoodTruckMenuFragment(list1)
            }
            1 ->{
                fragment = FoodTruckReviewsNoConnectionFragment()
            }
            else ->{
                throw Exception("exception")
            }
        }

        return fragment
    }


}