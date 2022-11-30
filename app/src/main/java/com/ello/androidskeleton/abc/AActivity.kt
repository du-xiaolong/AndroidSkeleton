package com.ello.androidskeleton.abc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.ello.androidskeleton.R
import com.ello.base.ktx.get
import com.ello.base.ktx.startActivityForResultLauncher

class AActivity : AppCompatActivity() {

    private val bLauncher = startActivityForResultLauncher{
        if (it.resultCode == RESULT_OK) {
            findViewById<TextView>(R.id.tvResult).text = it.data.get("result", "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)

        findViewById<View>(R.id.btnToB).setOnClickListener {
            bLauncher.launch(Intent(this, BActivity::class.java))
        }

    }
}