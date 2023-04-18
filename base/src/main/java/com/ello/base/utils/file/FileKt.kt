package com.ello.base.utils.file

import android.content.ContentResolver.MimeTypeInfo
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.blankj.utilcode.util.ConvertUtils
import com.ello.base.utils.AppUtils
import com.ello.base.utils.requireNotNull
import java.io.File
import java.io.InputStream

/**
 *
 * @author dxl
 * @date 2023/4/18
 */

//文件扩展名
val File.extension: String
    get() = MimeTypeMap.getFileExtensionFromUrl(absolutePath)

//获取文件的mineType
val File.mimeType: String
    get() = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"

//保存文件到公共目录
fun File.saveToPublic() {
    inputStream().saveToPublic(name)
}


/**
 * 保存文件到公共目录
 * 注意：Android Q以下的版本需要申请写入文件权限，否则无法保存成功，Android Q及以上版本无需申请
 */
fun InputStream.saveToPublic(fileName: String) {
    val realMimeType = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(fileName.substringAfterLast(".")) ?: "*/*"
    val directory = when {
        fileName.isVideo -> Environment.DIRECTORY_MOVIES
        fileName.isAudio -> Environment.DIRECTORY_MUSIC
        fileName.isImage -> Environment.DIRECTORY_PICTURES
        else -> Environment.DIRECTORY_DOWNLOADS
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            val current = System.currentTimeMillis()
            put(MediaStore.Images.Media.DATE_ADDED, current)
            put(MediaStore.Images.Media.DATE_MODIFIED, current)
            put(MediaStore.Images.Media.MIME_TYPE, realMimeType)
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, directory)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val uri = when {
            fileName.isVideo -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            fileName.isAudio -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            fileName.isImage -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }
        val contentResolver = AppUtils.appContext.contentResolver
        val insert =
            contentResolver.insert(uri, contentValues).requireNotNull { "插入失败，insertUri = null" }
        contentResolver.openOutputStream(insert).use { outputStream ->
            val length = copyTo(outputStream.requireNotNull { "保存失败！outputStream = null" })
            require(length > 0) { "保存失败！length = 0" }
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            contentResolver.update(insert, contentValues, null, null)
        }
    } else {
        val publicDirectory =
            Environment.getExternalStoragePublicDirectory(directory)
        var targetFile = File(publicDirectory, fileName)
        var currentIndex = 0
        //如果已经有这个文件名了，就在文件名后边添加（1），依次类推
        while (targetFile.exists()) {
            currentIndex++
            targetFile =
                File(
                    publicDirectory,
                    "${fileName.substringBeforeLast(".")}($currentIndex).${
                        fileName.substringAfterLast(".")
                    }"
                )
        }

        targetFile.outputStream().use {
            val length = copyTo(it)
            require(length > 0) { "保存失败！length = 0" }
            MediaScannerConnection.scanFile(
                AppUtils.appContext,
                arrayOf(targetFile.absolutePath),
                null,
                null
            )
        }
    }

}

/**
 * 文件保存目录（全局）
 */
fun fileDir(type: String): File {
    val dir =
        AppUtils.appContext.getExternalFilesDir(type) ?: File(AppUtils.appContext.filesDir, type)
    if (!dir.exists()) dir.mkdirs()
    return dir
}

/**
 * 缓存目录（全局）
 */
fun cacheDir(type: String): File {
    val cacheDir = AppUtils.appContext.externalCacheDir ?: AppUtils.appContext.cacheDir
    val dir = File(cacheDir, type)
    if (!dir.exists()) dir.mkdirs()
    return dir
}

/**
 * 文件大小格式化显示，比如3.5MB
 */
val File?.formatSize: String
    get() = (this?.length() ?: 0L).formatSize

/**
 * 文件大小格式化显示，比如3.5MB
 */
val Long.formatSize: String
    get() = ConvertUtils.byte2FitMemorySize(this, 1)

/**
 * 文件是否为视频
 */
val File?.isVideo: Boolean
    get() = this?.name.isVideo

/**
 * 文件是否为图片
 */
val File?.isImage: Boolean
    get() = this?.name.isImage

/**
 * 文件是否为音频
 */
val File?.isAudio: Boolean
    get() = this?.name.isAudio

/**
 * 根据文件名、路径、url判断是否是声音
 */
val String?.isAudio: Boolean
    get() {
        if (isNullOrBlank()) return false
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(substringAfterLast("."))
            ?.contains("audio") == true
    }

/**
 * 根据文件名、路径、url判断是否是图片
 */
val String?.isImage: Boolean
    get() {
        if (isNullOrBlank()) return false
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(substringAfterLast("."))
            ?.contains("image") == true
    }

/**
 * 根据文件名、路径、url判断是否是视频
 */
val String?.isVideo: Boolean
    get() {
        if (isNullOrBlank()) return false
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(substringAfterLast("."))
            ?.contains("video") == true
    }

/**
 * 根据uri获取文件名，如果获取失败返回 ""
 */
val Uri?.fileName: String
    get() {
        this ?: return ""
        return AppUtils.appContext.contentResolver.query(
            this,
            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
            null,
            null,
            null
        ).use { cursor ->
            cursor?.getStringOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
        } ?: ""
    }

/**
 * 根据uri获取文件大小，失败返回0
 */
val Uri?.fileSize: Long
    get() {
        this ?: return 0L
        return AppUtils.appContext.contentResolver.query(
            this,
            arrayOf(MediaStore.MediaColumns.SIZE),
            null,
            null,
            null
        ).use { cursor ->
            cursor?.getLongOrNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
        } ?: 0L
    }




