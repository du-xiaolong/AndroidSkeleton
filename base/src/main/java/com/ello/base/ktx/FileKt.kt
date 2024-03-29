package com.ello.base.ktx

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.ello.base.utils.AppUtils
import okhttp3.internal.closeQuietly
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @author dxl
 */


/**
 * 保存文件到公共目录
 *
 * @param context
 */
fun File.saveToMedia(context: Context) {
    val mimeType = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(absolutePath)) ?: "*/*"
    inputStream().saveToMedia(context, name, mimeType)
}

/**
 * IO流文件保存到公共目录
 */
fun InputStream.saveToMedia(context: Context, fileName: String, mimeType: String? = null) {
    val _mimeType = mimeType ?: MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(fileName.substringAfterLast(".")) ?: ""

    fun isVideo(mimeType: String) = mimeType.contains("video")
    fun isImage(mimeType: String) = mimeType.contains("image")
    fun isAudio(mimeType: String) = mimeType.contains("audio")
    val directory =
        when {
            isVideo(_mimeType) -> Environment.DIRECTORY_MOVIES
            isImage(_mimeType) -> Environment.DIRECTORY_DCIM
            isAudio(_mimeType) -> Environment.DIRECTORY_MUSIC
            else -> throw IllegalArgumentException("不支持的类型")
        }
    val contentValues = ContentValues().apply {
        val date = System.currentTimeMillis() / 1000
        put(MediaStore.Images.Media.DATE_ADDED, date)
        put(MediaStore.Images.Media.DATE_MODIFIED, date)

        put(MediaStore.Images.Media.MIME_TYPE, mimeType)

    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        with(contentValues) {
            // 高版本不用查重直接插入，会自动重命名
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, directory)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    } else {
        val picturePath =
            Environment.getExternalStoragePublicDirectory(directory)
        if (!picturePath.exists() && !picturePath.mkdirs()) {
            throw IllegalArgumentException("保存失败！getExternalStoragePublicDirectory不存在")
        }
        // 文件路径查重，重复的话在文件名后拼接数字
        var imageFile = File(picturePath, fileName)
        var queryUri = context.contentResolver.queryMediaImage28(imageFile.absolutePath)
        var suffix = 1
        val fileNameWithoutExtension = imageFile.nameWithoutExtension
        val fileExtension = imageFile.extension
        while (queryUri != null) {
            val newName = fileNameWithoutExtension + "(${suffix++})." + fileExtension
            imageFile = File(picturePath, newName)
            queryUri = context.contentResolver.queryMediaImage28(imageFile.absolutePath)
        }
        with(contentValues) {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
        }
    }

    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (isVideo(_mimeType)) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else if (isImage(_mimeType)) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }
    } else {
        if (isVideo(_mimeType)) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if (isImage(_mimeType)) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    }

    val insertUri = context.contentResolver.insert(uri, contentValues)

    require(insertUri != null) { "insertUri = null !!" }
    val openOutputStream = context.contentResolver.openOutputStream(insertUri)
    require(openOutputStream != null) { "openOutputStream = null!!" }
    val length = copyTo(openOutputStream)
    if (length > 0) {
        //修改为可见
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(insertUri, contentValues, null, null)
        } else {
            contentValues.put(MediaStore.Images.Media.SIZE, length)
            context.contentResolver.update(insertUri, contentValues, null, null)
            // 通知媒体库更新
            val intent =
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, insertUri)
            context.sendBroadcast(intent)
        }
    } else {
        throw IllegalArgumentException("保存失败！length = 0")
    }
}

/**
 * 根据URI获取真实路径
 */
fun getPathByUri(context: Context, uri: Uri?): String? {
    uri ?: return null
    // 以 file:// 开头的使用第三方应用打开 (open with third-party applications starting with file://)
    if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true))
        return getDataColumn(context, uri)

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // LocalStorageProvider
        if (isLocalStorageDocument(uri)) {
            // The path is the id
            return DocumentsContract.getDocumentId(uri);
        }
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + File.separator + split[1]
                } else {
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + split[1]
                }
            } else if ("home".equals(type, ignoreCase = true)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + File.separator + "documents" + File.separator + split[1]
                } else {
                    @Suppress("DEPRECATION")
                    return Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "documents" + File.separator + split[1]
                }
            } else {
                @Suppress("DEPRECATION")
                val sdcardPath =
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "documents" + File.separator + split[1]
                return if (sdcardPath.startsWith("file://")) {
                    sdcardPath.replace("file://", "")
                } else {
                    sdcardPath
                }
            }
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            if (id != null && id.startsWith("raw:")) {
                return id.substring(4)
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads",
                    "content://downloads/all_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri =
                        ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                    try {
                        val path = getDataColumn(context, contentUri)
                        if (!path.isNullOrBlank()) return path
                    } catch (e: Exception) {
                        Log.e("fileKt", e.toString())
                    }
                }
            } else {
                //testPath(uri)
                return getDataColumn(context, uri)
            }
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val contentUri: Uri? = when (split[0]) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                "download" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI
                } else null
                else -> null
            }
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, "_id=?", selectionArgs)
        }

        //GoogleDriveProvider
        else if (isGoogleDriveUri(uri)) {
            return getGoogleDriveFilePath(uri, context)
        }
    }
    // MediaStore (and general)
    else if ("content".equals(uri.scheme, ignoreCase = true)) {
        // Return the remote address
        if (isGooglePhotosUri(uri)) {
            return uri.lastPathSegment
        }
        // Google drive legacy provider
        else if (isGoogleDriveUri(uri)) {
            return getGoogleDriveFilePath(uri, context)
        }
        // Huawei
        else if (isHuaWeiUri(uri)) {
            val uriPath = getDataColumn(context, uri) ?: uri.toString()
            //content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
            if (uriPath.startsWith("/root")) {
                return uriPath.replace("/root".toRegex(), "")
            }
        }
        return getDataColumn(context, uri)
    }
    return getDataColumn(context, uri)
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
 * 保存uri到指定的目录
 */
@SuppressLint("Range")
fun copyUriToDirectory(context: Context, uri: Uri?, descDir: File?): Boolean {
    uri ?: return false
    descDir ?: return false
    if (descDir.exists()) {
        descDir.mkdirs()
    }
    var file = File(uri.toString())
    if (file.exists()) {
        return FileUtils.move(file, File(descDir, file.name))
    }
    if (uri.scheme == ContentResolver.SCHEME_FILE) {
        file = uri.path?.let { File(it) } ?: return false
        return FileUtils.move(file, File(descDir, file.name))
    }
    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        return kotlin.runCatching {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            var result = false
            if (cursor != null && cursor.moveToFirst()) {
                val displayName =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                result = FileIOUtils.writeFileFromIS(
                    File(descDir, displayName),
                    contentResolver.openInputStream(uri)
                )
                cursor.closeQuietly()
            }
            result
        }.getOrDefault(false)
    }
    return false

}


/**
 * 根据uri读取文件名
 */
@SuppressLint("Range")
fun getFileNameByUri(uri: Uri?): String {
    if (uri == null) {
        return ""
    }
    return kotlin.runCatching {
        val cursor = AppUtils.appContext.contentResolver.query(uri, null, null, null, null)
        val displayName = if (cursor != null && cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        } else ""
        cursor?.close()
        displayName
    }.getOrElse { "" }
}

/**
 * 获取uri的文件大小
 */
@SuppressLint("Range")
fun getFileSizeByUri(uri: Uri?): Long {
    if (uri == null) {
        return 0L
    }
    return runCatching {
        val cursor = AppUtils.appContext.contentResolver.query(uri, null, null, null, null)
        val size = if (cursor != null && cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
        } else 0L
        cursor?.close()
        size
    }.getOrElse { 0L }
}

/**
 * 保存bitmap
 *
 * @param file
 */
fun Bitmap.saveToFile(file: File) {
    kotlin.runCatching {
        val bos = BufferedOutputStream(FileOutputStream(file))
        compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
    }
}



/******************************************************* private **************************************************/


/**
 * Android Q以下版本，查询媒体库中当前路径是否存在
 * @return Uri 返回null时说明不存在，可以进行图片插入逻辑
 */
private fun ContentResolver.queryMediaImage28(imagePath: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null
    val imageFile = File(imagePath)
    if (imageFile.canRead() && imageFile.exists()) {
        // 文件已存在，返回一个file://xxx的uri
        return Uri.fromFile(imageFile)
    }
    // 保存的位置
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // 查询是否已经存在相同图片
    val query = this.query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA),
        "${MediaStore.Images.Media.DATA} == ?",
        arrayOf(imagePath), null
    )
    query?.use {
        while (it.moveToNext()) {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = it.getLong(idColumn)
            return ContentUris.withAppendedId(collection, id)
        }
    }
    return null
}


/**
 * BUG : 部分机型进入"文件管理器" 执行到  cursor.getColumnIndexOrThrow(column);出现
 *       Caused by: java.lang.IllegalArgumentException: column '_data' does not exist. Available columns: []
 *
 * Fixed :
 *      https://stackoverflow.com/questions/42508383/illegalargumentexception-column-data-does-not-exist
 *
 */
private fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String? = null,
    selectionArgs: Array<String>? = null
): String? {
    @Suppress("DEPRECATION")
    val column = MediaStore.Files.FileColumns.DATA
    val projection = arrayOf(column)
    try {
        context.contentResolver.query(
            uri ?: return null,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { c: Cursor ->
            if (c.moveToFirst()) {
                val columnIndex = c.getColumnIndex(column)
                return c.getString(columnIndex)
            }
        }
    } catch (e: Throwable) {
        Log.e("error", "getDataColumn -> ${e.message}")
    }
    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is local.
 */
private fun isLocalStorageDocument(uri: Uri?): Boolean {
    return ".andoFileProvider".equals(uri?.authority, true)
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
private fun isExternalStorageDocument(uri: Uri?): Boolean {
    return "com.android.externalstorage.documents".equals(uri?.authority, true)
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
private fun isDownloadsDocument(uri: Uri?): Boolean {
    return "com.android.providers.downloads.documents".equals(uri?.authority, true)
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
private fun isMediaDocument(uri: Uri?): Boolean {
    return "com.android.providers.media.documents".equals(uri?.authority, true)
}

private fun isGoogleDriveUri(uri: Uri?): Boolean {
    return "com.google.android.apps.docs.storage.legacy" == uri?.authority || "com.google.android.apps.docs.storage" == uri?.authority
}

private fun getGoogleDriveFilePath(uri: Uri, context: Context): String? {
    context.contentResolver.query(uri, null, null, null, null)?.use { c: Cursor ->
        /*
         Get the column indexes of the data in the Cursor,
         move to the first row in the Cursor, get the data, and display it.
         */
        val nameIndex: Int = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        //val sizeIndex: Int = c.getColumnIndex(OpenableColumns.SIZE)
        if (!c.moveToFirst()) {
            return uri.toString()
        }
        val name: String = c.getString(nameIndex)
        //val size = c.getLong(sizeIndex).toString()
        val file = File(context.cacheDir, name)

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers)?.also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return file.path
    }
    return uri.toString()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
private fun isGooglePhotosUri(uri: Uri?): Boolean {
    return "com.google.android.apps.photos.content".equals(uri?.authority, true)
}

/**
 * content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
 *
 * @param uri
 * @return
 */
private fun isHuaWeiUri(uri: Uri?): Boolean {
    return "com.huawei.hidisk.fileprovider".equals(uri?.authority, true)
}


