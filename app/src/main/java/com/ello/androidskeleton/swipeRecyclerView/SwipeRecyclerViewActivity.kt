package com.ello.androidskeleton.swipeRecyclerView

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ello.androidskeleton.R
import com.ello.androidskeleton.databinding.ActivitySwipeRecyclerViewBinding
import com.ello.base.view.SwipeLayout

class SwipeRecyclerViewActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySwipeRecyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySwipeRecyclerViewBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        val data = mutableListOf<Int>()
        repeat(100) { data.add(it) }

        viewBinding.recyclerView.adapter =
            object :
                BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_swipe_recycler_view, data) {
                init {
                    addChildClickViewIds(R.id.left_menu, R.id.right_menu)
                }

                override fun convert(holder: BaseViewHolder, item: Int) {
                    holder.setText(R.id.tv_main, item.toString())
                    //滑动事件监听
                    (holder.itemView as SwipeLayout).addListener(object : SwipeLayout.Listener {
                        override fun onSwipe(menuView: View, swipeOffset: Float) {

                        }

                        override fun onSwipeStateChanged(menuView: View, newState: Int) {

                        }

                        override fun onMenuOpened(menuView: View) {

                        }

                        override fun onMenuClosed(menuView: View) {

                        }
                    })
                }

            }.apply {
                setOnItemChildClickListener { _, view, position ->
                    val item = getItem(position)
                    if (view.id == R.id.left_menu) {
                        Toast.makeText(
                            this@SwipeRecyclerViewActivity,
                            "第${item}个：左菜单点击",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (view.id == R.id.right_menu) {
                        Toast.makeText(
                            this@SwipeRecyclerViewActivity,
                            "第${item}个：右菜单点击",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


    }
}