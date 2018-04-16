package de.smeurer.filemover.ui

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.markodevcic.peko.Peko
import de.smeurer.filemover.data.Mode
import de.smeurer.filemover.data.MoveConfig
import io.paperdb.Paper
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/**
 * Created by Simon Meurer on 16.04.18.
 */
abstract class AbstractBaseActivity : AppCompatActivity() {

    fun saveMoveConfig(moveConfig: MoveConfig) {
        val configs = getMoveConfigsSet()
        Paper.book().write("configs", configs.plus(moveConfig))
    }

    fun deleteMoveConfig(moveConfig: MoveConfig?) {
        val configs = getMoveConfigsSet()
        Paper.book().write("configs", configs.minus(moveConfig))
    }

    fun getMoveConfigs(): List<MoveConfig> {
        return getMoveConfigsSet().toList()
    }

    private fun getMoveConfigsSet(): Set<MoveConfig> {
        return Paper.book().read("configs")
    }


    fun runMoveConfig(moveConfig: MoveConfig) {
        launch(UI) {
            val permissionResultDeferred =
                    Peko.requestPermissionsAsync(this@AbstractBaseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val (grantedPermissions) = permissionResultDeferred.await()

            if (Manifest.permission.WRITE_EXTERNAL_STORAGE in grantedPermissions) {
                startMoveConfig(moveConfig)
            } else {
                toast("Sorry we need access to storage!")
            }
        }


    }

    private fun startMoveConfig(moveConfig: MoveConfig) {
        val directory = File(moveConfig.fromPath)
        val files = directory.listFiles()
        Log.d("Files", "All files: " + files.size)

        var i = 0

        files.toObservable()
                .filter { f -> moveConfig.extensions.contains(f.extension) }
                .doOnEach { file ->
                    run {
                        if (file.value != null) {
                            copyFile(file.value!!.absolutePath, moveConfig.toPath)
                            if (moveConfig.mode == Mode.move) {
                                if (file.value!!.delete()) {
                                    i++
                                }
                            } else {
                                i++
                            }
                        }

                    }
                }
                .subscribeBy(onComplete = {
                    when (moveConfig.mode) {
                        Mode.move -> toast("Moved $i files")
                        Mode.copy -> toast("Copied $i files")
                    }
                    moveConfig.lastRunAt = DateTime.now()
                    saveMoveConfig(moveConfig)
                })
    }


    private fun copyFile(srcFile: String, dstDir: String) {
        Log.d("COPY", "From $srcFile to $dstDir");
        try {
            val src = File(srcFile)
            val dst = File(dstDir, src.name)

            copyFile(src, dst)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File) {
        if (!destFile.parentFile.exists())
            destFile.parentFile.mkdirs()

        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null

        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination!!.transferFrom(source, 0, source!!.size())
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }
}