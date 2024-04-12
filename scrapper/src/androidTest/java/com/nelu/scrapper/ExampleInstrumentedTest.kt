package com.nelu.scrapper

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun checkFacebookAPI() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Scrapper.init(context)
        runBlocking {
            val video = Scrapper.facebook.getVideo("https://www.facebook.com/share/v/uAjY17QzG1keiwpw/?mibextid=jmPrMh")
            println(video.toString())
            assertNotNull(video)
        }
    }
}