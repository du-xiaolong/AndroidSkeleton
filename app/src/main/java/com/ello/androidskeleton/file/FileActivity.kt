package com.ello.androidskeleton.file

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ello.androidskeleton.databinding.ActivityFileBinding
import com.ello.base.ktx.saveToMedia
import okhttp3.internal.closeQuietly

class FileActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFileBinding.inflate(layoutInflater).also { setContentView(it.root) }

        viewBinding.btnSaveImageToPublic.setOnClickListener {
            saveFile("test_image.jpg")
        }
        viewBinding.btnSaveVideoToPublic.setOnClickListener {
            saveFile("test_video.mp4")
        }
        viewBinding.btnSaveAudioToPublic.setOnClickListener {
            saveFile("test_audio.mp3")
        }
        viewBinding.btnQueryImage.setOnClickListener {
            //todo
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            val cursor = contentResolver.query(
                collection,
                arrayOf(MediaStore.Images.Media._ID),
                "${MediaStore.Images.Media.DISPLAY_NAME} = ?",
                arrayOf("test_image.jpg"),
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?:return@setOnClickListener
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                Log.d("查询", "onCreate: id = $id")
            }
            cursor.closeQuietly()
        }
    }

    private fun saveFile(assetsName: String) {
        kotlin.runCatching {
            assets.open("test_image.jpg").saveToMedia(this, "test_image.jpg")
        }.onFailure {
            Toast.makeText(this, "保存失败：$it", Toast.LENGTH_SHORT).show()
        }.onSuccess {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
        }
    }
}