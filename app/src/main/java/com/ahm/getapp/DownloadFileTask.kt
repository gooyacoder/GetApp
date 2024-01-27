package com.ahm.getapp

import android.os.AsyncTask
import java.io.FileOutputStream
import java.io.BufferedInputStream
import java.net.URL

class DownloadFileTask(private val url: String, private val outputFile: String,
    private val onProgressUpdate:(Int)-> Unit, private val onDownloadComplete: () -> Unit) :
    AsyncTask<Void, Int, Void>()
{


    override fun doInBackground(vararg p0: Void?): Void? {
        val connection = URL(url).openConnection()
        connection.connect()
        val fileLength = connection.contentLength
        val input = BufferedInputStream(URL(url).openStream())
        val output = FileOutputStream(outputFile)
        val data = ByteArray(1024)
        var total: Long = 0
        var count: Int

        while (input.read(data).also { count = it } != -1) {
            total += count.toLong()
            if (fileLength > 0) {
                publishProgress((total * 100 / fileLength).toInt())
            }
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()

        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        onProgressUpdate(values[0] ?: 0)
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        onDownloadComplete()
    }
}