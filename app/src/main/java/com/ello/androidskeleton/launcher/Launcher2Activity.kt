package com.ello.androidskeleton.launcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ello.androidskeleton.R
import com.ello.androidskeleton.databinding.ActivityLauncher2Binding
import com.ello.base.ktx.finishWithResult

class Launcher2Activity : AppCompatActivity() {

    private lateinit var viewBinding:ActivityLauncher2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLauncher2Binding.inflate(layoutInflater).also { setContentView(it.root) }

        supportActionBar?.title = this.javaClass.simpleName

        viewBinding.button2.setOnClickListener {
            finishWithResult("params" to 8888)
        }
    }

}