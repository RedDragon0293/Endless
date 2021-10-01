package cn.asone.endless.utils.io

import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.security.SecurityUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object FileUtils {
    @JvmStatic
    fun createBackupFile(file: File) {
        val backupFile = File(file.parentFile, "${file.name}_bak")
        if (backupFile.exists()) {
            if (SecurityUtils.getSha1String(backupFile).equals(SecurityUtils.getSha1String(file), true)
                && SecurityUtils.getMd5String(backupFile).equals(SecurityUtils.getMd5String(file), true)
            ) {
                ClientUtils.logger.info("文件 ${file.name} 与其备份文件相同. 跳过备份.")
            }
            ClientUtils.logger.warn("${file.name} 的备份文件已存在. 覆盖现有文件.")
            backupFile.delete()
        }
        file.renameTo(backupFile)
    }

    @JvmStatic
    fun extractZip(file: File, outputFolder: File = file.parentFile) {
        if (!file.exists() || file.isDirectory) {
            throw IllegalArgumentException("文件 ${file.name} 不存在!")
        }
        runCatching {
            if (!outputFolder.exists())
                outputFolder.mkdirs()
            val inputStream = ZipInputStream(FileInputStream(file))
            var entry = inputStream.nextEntry
            while (entry != null) {
                val currentFile = File(outputFolder, File.separator + entry.name)
                currentFile.parentFile.mkdirs()
                if (currentFile.exists()) {
                    ClientUtils.logger.warn("压缩文件 $file 的子文件 ${currentFile.name} 已存在. 覆盖现有文件.")
                    currentFile.delete()
                }
                val outputStream = FileOutputStream(currentFile)
                /*
                var length: Int = inputStream.read(buffer)
                while (length > 0) {
                    outputStream.write(buffer, 0, length)
                    length = inputStream.read(buffer)
                }
                */
                inputStream.copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
                entry = inputStream.nextEntry
            }
            inputStream.closeEntry()
            inputStream.close()
        }.onFailure {
            it.printStackTrace()
        }
    }
}