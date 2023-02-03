package com.ello.androidskeleton.screenRecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ello.androidskeleton.databinding.ActivityScreenRecordBinding

/**
 * @author dxl
 * @date 2023-02-03  周五
 */
class ScreenRecorderActivity : AppCompatActivity() {

    private lateinit var vb:ActivityScreenRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityScreenRecordBinding.inflate(layoutInflater)
        setContentView(vb.root)
    }



}