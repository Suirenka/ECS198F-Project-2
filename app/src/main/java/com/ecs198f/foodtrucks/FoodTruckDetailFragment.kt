package com.ecs198f.foodtrucks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodTruckDetailFragment : Fragment() {
    private val args: FoodTruckDetailFragmentArgs by navArgs()
    private lateinit var tabStateAdapter: TabStateAdapter
    private lateinit var  DisconnectAdapter: DisConeectedAdap
    private lateinit var viewPager: ViewPager2
    private val viewModel: TruckIdViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFoodTruckDetailBinding.inflate(inflater, container, false)

        args.foodTruck.let {

            binding.apply {
                Glide.with(root).load(it.imageUrl).into(foodTruckDetailImage)
                foodTruckDetailPriceLevel.text = "$".repeat(it.priceLevel)
                foodTruckDetailLocation.text = it.location
                foodTruckDetailTime.text = it.formattedTimeInterval
            }

            (requireActivity() as MainActivity).apply {
                title = it.name
            }
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = runBlocking {
        val viewPage = view.findViewById<ViewPager2>(R.id.pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        var list1: List<FoodItem> = emptyList()
        var list2: List<FoodReview> = emptyList()

        args.foodTruck.let {
            (requireActivity() as MainActivity).apply {

                foodTruckService.listFoodItems(it.id).enqueue(object : Callback<List<FoodItem>> {
                    override fun onResponse(
                        call: Call<List<FoodItem>>,
                        response: Response<List<FoodItem>>
                    ) {

                        list1 = response.body()!!
                        lifecycleScope.launch {
                            var items = db2.fooditemDao().listAllFoodItems(it.id)
                            if (items.isEmpty()) {
                                db2.fooditemDao().addItems(list1)
                            }
                        }
                        foodTruckService.listFoodReviews(it.id).enqueue(object : Callback<List<FoodReview>> {
                            override fun onResponse(
                                call: Call<List<FoodReview>>,
                                response: Response<List<FoodReview>>
                            ) {

                                list2 = response.body()!!
                                tabStateAdapter = TabStateAdapter(this@FoodTruckDetailFragment, list1, list2)
                                viewPager = view.findViewById(R.id.pager)
                                viewPager.adapter = tabStateAdapter

                                TabLayoutMediator(tabLayout, viewPage ){tab, position ->
                                    when(position)
                                    {
                                        0->{
                                            tab.text = "MENU"
                                        }
                                        1->{
                                            tab.text = "REVIEWS"
                                        }
                                    }
                                }.attach()
                            }

                            override fun onFailure(call: Call<List<FoodReview>>, t: Throwable) {
                                lifecycleScope.launch {
                                    list1 = db2.fooditemDao().listAllFoodItems(it.id)
                                    DisconnectAdapter = DisConeectedAdap(this@FoodTruckDetailFragment, list1)
                                    viewPager = view.findViewById(R.id.pager)
                                    viewPager.adapter =  DisconnectAdapter

                                    TabLayoutMediator(tabLayout, viewPage ){tab, position ->
                                        when(position)
                                        {
                                            0->{
                                                tab.text = "MENU"
                                            }
                                            1->{
                                                tab.text = "REVIEWS"
                                            }
                                        }
                                    }.attach()
                                }
                            }
                        })

                    }

                    override fun onFailure(call: Call<List<FoodItem>>, t: Throwable) {
                        throw t
                    }
                })
            }
        }

        viewModel.set(args.foodTruck.id)
        viewModel.updated.observe(viewLifecycleOwner, Observer { item ->
            // Perform an action with the latest item data
            if (item) {
                lifecycleScope.launch { updateReviews(list1, tabLayout) }
            }
        })
    }

    suspend fun updateReviews(list1:List<FoodItem>, tabLayout:TabLayout) {
        delay(1000L)
        var list2:List<FoodReview>
        args.foodTruck.let {
            (requireActivity() as MainActivity).apply {
                foodTruckService.listFoodReviews(it.id).enqueue(object : Callback<List<FoodReview>> {
                    override fun onResponse(
                        call: Call<List<FoodReview>>,
                        response: Response<List<FoodReview>>
                    ) {

                        list2 = response.body()!!
                        Log.d("New review", list2.last().content)
                        tabStateAdapter = TabStateAdapter(this@FoodTruckDetailFragment, list1, list2)
                        viewPager = view!!.findViewById(R.id.pager)
                        viewPager.adapter = tabStateAdapter
                        viewModel.set(false)
                    }

                    override fun onFailure(call: Call<List<FoodReview>>, t: Throwable) {
                    }
                })
                tabLayout.setScrollPosition(0,0f,true);
                viewPager.setCurrentItem(0)

            }
        }
    }


}