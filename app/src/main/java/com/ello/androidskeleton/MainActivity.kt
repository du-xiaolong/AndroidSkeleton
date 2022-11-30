package com.ello.androidskeleton

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.ello.androidskeleton.abc.AActivity
import com.ello.androidskeleton.activityParams.ParamsActivity
import com.ello.androidskeleton.databinding.ActivityMainBinding
import com.ello.androidskeleton.displayCuts.DisplayCuts2Activity
import com.ello.androidskeleton.displayCuts.DisplayCutsActivity
import com.ello.androidskeleton.file.FileActivity
import com.ello.androidskeleton.fragmentParams.ParamFragmentActivity
import com.ello.androidskeleton.launcher.LauncherActivity
import com.ello.androidskeleton.swipeRecyclerView.SwipeRecyclerViewActivity
import com.ello.base.ktx.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val paramLauncher = registerForActivityResult(ParamsActivity.ParamActivityLauncher()) {
        Toast.makeText(this, "回传参数：$it", Toast.LENGTH_SHORT).show()
    }

    private val fragmentParamLauncher =
        registerForActivityResult(ParamFragmentActivity.ParamsFragmentLauncher()) {
            Toast.makeText(this, "回传参数：$it", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportActionBar?.title = this.javaClass.simpleName

        viewBinding.btnActivityParams.setOnClickListener(this::activityParamClick)
        viewBinding.btnActivityParamsWithResult.setOnClickListener(this::activityParamWithResultClick)
        viewBinding.btnFragmentParams.setOnClickListener(this::fragmentParamClick)
        viewBinding.btnFragmentParamsWithResult.setOnClickListener(this::fragmentParamsWithResultClick)
        viewBinding.btnLauncher.setOnClickListener { startActivity<LauncherActivity>() }
        viewBinding.btnSwipeRecyclerView.setOnClickListener { startActivity<SwipeRecyclerViewActivity>() }
        viewBinding.btnFile.setOnClickListener { startActivity<FileActivity>() }

        //刘海屏适配
        viewBinding.btnDisplayCuts.setOnClickListener { startActivity<DisplayCutsActivity>() }
        viewBinding.btnDisplayCuts2.setOnClickListener { startActivity<DisplayCuts2Activity>() }

        viewBinding.btnParamActivities.setOnClickListener { startActivity<AActivity>() }
    }


    /**
     * 跳转activity传参
     */
    private fun activityParamClick(v: View) {
        startActivity<ParamsActivity>(
            "intParam1" to 1,
            "intParam2" to 2,
            "intParam3" to 3,
            "intParam4" to 4
        )
    }

    private fun activityParamWithResultClick(v: View) {
        paramLauncher.launch(
            bundleOf(
                "intParam1" to 5,
                "intParam2" to 6,
                "intParam3" to 7,
                "intParam4" to 8
            )
        )
    }


    private fun fragmentParamClick(v: View) {
        startActivity<ParamFragmentActivity>(
            "param1" to 9,
            "param2" to 10,
            "param3" to 11,
            "param4" to 12
        )
    }

    private fun fragmentParamsWithResultClick(v: View) {
        fragmentParamLauncher.launch(
            bundleOf(
                "param1" to 13,
                "param2" to 14,
                "param3" to 15,
                "param4" to 16
            )
        )
    }


}

