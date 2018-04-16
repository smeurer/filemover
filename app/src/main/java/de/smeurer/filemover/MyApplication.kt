package de.smeurer.filemover

import android.app.Application
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer
import de.smeurer.filemover.data.MoveConfig
import io.paperdb.Paper
import org.joda.time.DateTime

/**
 * Created by Simon Meurer on 16.04.18.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
        Paper.addSerializer(DateTime::class.java, JodaDateTimeSerializer())
        if (!Paper.book().contains("configs")) {
            Paper.book().write("configs", emptySet<MoveConfig>())
        }
    }
}