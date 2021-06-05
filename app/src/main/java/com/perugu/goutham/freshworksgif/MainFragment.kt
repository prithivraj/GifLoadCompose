package com.perugu.goutham.freshworksgif

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.perugu.goutham.freshworksgif.common.ListingViewPagerAdapter
import com.perugu.goutham.freshworksgif.favourites.FavoritesFragment
import com.perugu.goutham.freshworksgif.trending.TrendingFragment

class MainFragment : Fragment(R.layout.fragment_main_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = view.findViewById<ViewPager2>(R.id.pager)

        val listingViewPagerAdapter = ListingViewPagerAdapter(this)
        listingViewPagerAdapter.addFragment(TrendingFragment())
        listingViewPagerAdapter.addFragment(FavoritesFragment())

        viewPager.adapter = listingViewPagerAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            when(position){
                0 -> tab.text = "Trending"
                1 -> tab.text = "Favorites"
            }
        }.attach()

    }
}