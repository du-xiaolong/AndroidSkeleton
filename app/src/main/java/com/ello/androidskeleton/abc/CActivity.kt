package com.ello.androidskeleton.abc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.ello.androidskeleton.R
import com.ello.base.ktx.finishWithResult

class CActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finishWithResult("result" to findViewById<EditText>(R.id.etValue).text.toString())
        }
    }
}