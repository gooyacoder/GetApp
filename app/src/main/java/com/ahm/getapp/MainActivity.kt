package com.ahm.getapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    lateinit var url: EditText
    lateinit var progressBar: ProgressBar


    private val REQUEST_CODE_PERMISSIONS = 2

    private val REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        url = findViewById(R.id.editTextUrl)
        progressBar = findViewById(R.id.progressBar)
        progressBar.max = 100

        if (allPermissionsGranted()) {
            // Permissions granted, proceed with the app
            // startApp();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }
    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
    fun getFileName(url: String) : String {
        val urlArray = url.split('/')
        val urlArraySize = urlArray.size
        var fileName = urlArray[urlArraySize - 1]
        if(fileName.contains('?')){
            fileName = fileName.substring(0, fileName.indexOf('?'))
        }
        return fileName
    }
    fun onDownloadButtonClicked(view: View) {
        val url: String = url.text.toString()
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            var progress: Int = 0
            val connection = URL(url).openConnection()
            connection.connect()
            val fileLength = connection.contentLength
            val input = BufferedInputStream(URL(url).openStream())
            val fileName = getFileName(url)
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            val output = FileOutputStream(file)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                if (fileLength > 0) {
                    progress = (total * 100 / fileLength).toInt()
                    handler.post {
                        progressBar.progress = progress
                    }

                }
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            handler.post {
                progressBar.progress = progress
            }
        }
    }
}