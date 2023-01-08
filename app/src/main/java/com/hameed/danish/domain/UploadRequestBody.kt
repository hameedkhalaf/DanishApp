package com.hameed.danish.domain

import android.os.Looper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBody(
    private val file:File,
    private val contentType:String,
    private val callback: UploadCallback
):RequestBody() {
    override fun contentType() = "$contentType/*".toMediaTypeOrNull()
    override fun contentLength() = file.length()
    override fun writeTo(sink: BufferedSink) {
        file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileIputStream = FileInputStream(file)
        var uploaded = 0L
        fileIputStream.use { inputStream ->
            var read: Int
            android.os.Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also {
                    read = it
                } != -1) {
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }

    }


    interface UploadCallback {
    }

    companion object{
        private const val DEFAULT_BUFFER_SIZE=2048
    }
}