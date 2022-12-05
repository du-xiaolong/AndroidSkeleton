package com.ello.basequickadapter4.multi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ello.basequickadapter4.R
import com.ello.basequickadapter4.normal.NormalListAdapter
import com.ello.basequickadapter4.normal.NormalModel

class MultiListActivity : AppCompatActivity() {

    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv)
    }

    private val adapter by lazy {
        MultiListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_list)


        recyclerView.adapter = adapter

        adapter.submitList(listOf(
            NormalModel(0, "测试0"),
            NormalModel(1, "测试1"),
            NormalModel(2, "测试2"),
            NormalModel(3, "测试3"),
            NormalModel(4, "测试4"),
            NormalModel(3, "测试5"),
            NormalModel(2, "测试6"),
            NormalModel(1, "测试7"),
            NormalModel(0, "测试8"),
            NormalModel(1, "测试9"),
            NormalModel(2, "测试10"),
        ))
    }

}