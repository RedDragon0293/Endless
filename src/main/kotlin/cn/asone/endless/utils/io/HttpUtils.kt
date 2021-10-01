package cn.asone.endless.utils.io

import cn.asone.endless.utils.ClientUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object HttpUtils {
    private const val DEFAULT_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.60"

    init {
        HttpURLConnection.setFollowRedirects(true)
    }

    @JvmStatic
    private fun make(url: String, method: String, agent: String = DEFAULT_AGENT): HttpURLConnection {
        val httpConnection = URL(url).openConnection() as HttpURLConnection

        httpConnection.requestMethod = method
        httpConnection.connectTimeout = 2000 // 2 seconds until connect timeouts
        httpConnection.readTimeout = 10000 // 10 seconds until read timeouts

        httpConnection.setRequestProperty("User-Agent", agent)

        httpConnection.instanceFollowRedirects = true
        httpConnection.doOutput = true

        return httpConnection
    }

    @JvmStatic
    fun request(url: String, method: String, agent: String = DEFAULT_AGENT): String {
        val connection = make(url, method, agent)

        return connection.inputStream.reader().readText()
    }

    @JvmStatic
    fun get(url: String) = request(url, "GET")
/*
    @JvmStatic
    fun download(url: String, file: File) = FileOutputStream(file).use { make(url, "GET").inputStream.copyTo(it) }
*/

    @Throws(IOException::class)
    @JvmStatic
    fun download(url: String, file: File) {
        if (file.isDirectory)
            throw IllegalArgumentException("目录无法作为保存文件!")
        if (file.exists()) {
            ClientUtils.logger.warn("文件 ${file.name} 已存在. 将对现有文件备份.")
            FileUtils.createBackupFile(file)
        }
        val connection = make(url, "GET")
        val inputStream = connection.inputStream
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        outputStream.flush()
        outputStream.close()
    }
}