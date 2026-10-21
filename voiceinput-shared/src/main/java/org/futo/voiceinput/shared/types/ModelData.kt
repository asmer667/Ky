package org.futo.voiceinput.shared.types

import android.content.Context
import androidx.annotation.StringRes
import org.futo.voiceinput.shared.ggml.WhisperGGML
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

@Throws(IOException::class)
private fun loadMappedFile(context: Context, filePath: String): MappedByteBuffer {
    context.assets.openFd(filePath).use { fileDescriptor ->
        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }
}

interface ModelLoader {
    @get:StringRes
    val name: Int
    fun exists(context: Context): Boolean
    fun getRequiredDownloadList(context: Context): List<String>
    fun loadGGML(context: Context): WhisperGGML
    fun key(context: Context): Any
}

internal class ModelBuiltInAsset(
    override val name: Int,
    val ggmlFile: String
) : ModelLoader {
    override fun exists(context: Context): Boolean {
        return true
    }
    override fun getRequiredDownloadList(context: Context): List<String> {
        return listOf()
    }
    override fun loadGGML(context: Context): WhisperGGML {
        val file = loadMappedFile(context, ggmlFile)
        return WhisperGGML(file)
    }
    override fun key(context: Context): Any {
        return "BuiltIn$ggmlFile"
    }
}

@Throws(IOException::class)
private fun tryOpenDownloadedModel(file: File): MappedByteBuffer {
    val fis = file.inputStream()
    val channel = fis.channel
    return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).load()
}

@Throws(IOException::class)
private fun Context.tryOpenDownloadedModel(pathStr: String): MappedByteBuffer {
    return tryOpenDownloadedModel(File(filesDir, pathStr))
}

internal class ModelDownloadable(
    override val name: Int,
    val ggmlFile: String,
    val checksum: String
) : ModelLoader {
    override fun exists(context: Context): Boolean {
        // البحث في assets أولاً
        return try {
            context.assets.open(ggmlFile).close()
            true
        } catch (e: Exception) {
            File(context.filesDir, ggmlFile).exists()
        }
    }
    override fun getRequiredDownloadList(context: Context): List<String> {
        // إذا كان موجوداً في assets، لا حاجة للتنزيل
        return try {
            context.assets.open(ggmlFile).close()
            listOf()
        } catch (e: Exception) {
            listOf(ggmlFile).filter {
                !File(context.filesDir, it).exists()
            }
        }
    }
    override fun loadGGML(context: Context): WhisperGGML {
        // محاولة القراءة من assets أولاً، ثم من filesDir
        return try {
            val file = loadMappedFile(context, ggmlFile)
            WhisperGGML(file)
        } catch (e: Exception) {
            val file = context.tryOpenDownloadedModel(ggmlFile)
            WhisperGGML(file)
        }
    }
    override fun key(context: Context): Any {
        return "Downloadable$ggmlFile$checksum"
    }
}

public class ModelFileFile(
    override val name: Int,
    val file: File,
) : ModelLoader {
    override fun exists(context: Context): Boolean {
        return file.exists()
    }
    override fun getRequiredDownloadList(context: Context): List<String> {
        return listOf()
    }
    override fun loadGGML(context: Context): WhisperGGML {
        val file = tryOpenDownloadedModel(file)
        return WhisperGGML(file)
    }
    override fun key(context: Context): Any {
        return "File${file.absolutePath}"
    }
}
