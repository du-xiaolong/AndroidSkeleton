package com.ello.androidskeleton.launcher

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.launch
import com.ello.androidskeleton.R
import com.ello.androidskeleton.databinding.ActivityLauncherBinding
import com.ello.base.ktx.*

class LauncherActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityLauncherBinding

    //启动另一个activity，回传结果
    private val startLauncher2ActivityLauncher = startActivityForResultLauncher {
        if (it.resultCode == RESULT_OK) {
            val result = it.data?.getIntExtra("params", 0) ?: 0
            Toast.makeText(this, "回调结果：$result", Toast.LENGTH_SHORT).show()
        }
    }

    //拍照
    private val takePictureLauncher = takePicturePreviewLauncher{
        viewBinding.ivPic.setImageBitmap(it)
    }

    //请求相机权限
    private val cameraPermissionLauncher = requestPermissionLauncher(Manifest.permission.CAMERA) {
        //takePicturePreviewLauncher正常不需要拍照权限，但是如果在manifest中声明了camera权限，就必须申请
        takePictureLauncher.launch()
    }


    private val openDocumentLauncher = openDocumentLauncher(arrayOf("*/*")) {

    }

    private val openDocumentsLauncher = openMultipleDocumentsLauncher(arrayOf("*/*")) {

    }

    private val getContentLauncher = getContentLauncher("*/*") {

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding =
            ActivityLauncherBinding.inflate(layoutInflater).also { setContentView(it.root) }

        supportActionBar?.title = this.javaClass.simpleName

        with(viewBinding) {
            button.setOnClickListener {
                startLauncher2ActivityLauncher.launch(Intent(this@LauncherActivity, Launcher2Activity::class.java))
            }

            btnCamera.setOnClickListener {
                cameraPermissionLauncher.launch()
            }

            btnGetContent.setOnClickListener {
                getContentLauncher.launch()
            }

            btnOpenDocument.setOnClickListener {
                openDocumentLauncher.launch()
            }

            btnOpenDocuments.setOnClickListener {
                openDocumentsLauncher.launch()
            }



        }



    }
}