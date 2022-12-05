package com.ello.basequickadapter4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ello.basequickadapter4.multi.MultiListActivity
import com.ello.basequickadapter4.normal.NormalListActivity
import com.ello.basequickadapter4.normalWithDataBinding.NormalListDataBindingActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn1).setOnClickListener { startActivity(Intent(this, NormalListActivity::class.java)) }
        findViewById<View>(R.id.btn2).setOnClickListener { startActivity(Intent(this, NormalListDataBindingActivity::class.java)) }
        findViewById<View>(R.id.btn3).setOnClickListener { startActivity(Intent(this, MultiListActivity::class.java)) }
    }
}