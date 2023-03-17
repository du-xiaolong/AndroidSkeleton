package com.ello.base.view

import androidx.core.view.forEachIndexed
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * bottomNavigationView和ViewPager2关联
 * @author dxl
 */
class NavigationViewPagerMediator(
    val bottomNavigationView: BottomNavigationView,
    val viewpager: ViewPager2,
    private val config: ((BottomNavigationView, ViewPager2) -> Unit)? = null
) {


    fun attach() {
        config?.invoke(bottomNavigationView, viewpager)
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
        bottomNavigationView.setOnItemSelectedListener {
            bottomNavigationView.menu.forEachIndexed { index, item ->
                if (it.itemId == item.itemId) {
                    viewpager.setCurrentItem(index, false)
                }
            }
            true
        }

    }

}