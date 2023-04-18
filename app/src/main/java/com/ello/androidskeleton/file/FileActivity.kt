package com.ello.androidskeleton.file

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ello.androidskeleton.databinding.ActivityFileBinding
import com.ello.base.ktx.saveToMedia
import com.ello.base.utils.click
import com.ello.base.utils.file.saveToPublic
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import okhttp3.internal.closeQuietly
import java.io.File

class FileActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFileBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val files = listOf(
            "test_image.jpg",
            "test_video.mp4",
            "test_audio.mp3",
            "test_file.zip"
        )
        listOf(
            viewBinding.btnSaveImage,
            viewBinding.btnSaveVideo,
            viewBinding.btnSaveAudio,
            viewBinding.btnSaveFile
        ).forEachIndexed { index, materialButton ->
            materialButton.click {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request { allGranted, grantedList, deniedList ->
                            if (allGranted) {
                                assets.open(files[index]).saveToPublic(files[index])
                            }
                        }
                }else {
                    assets.open(files[index]).saveToPublic(files[index])
                }
            }
        }

    }
}