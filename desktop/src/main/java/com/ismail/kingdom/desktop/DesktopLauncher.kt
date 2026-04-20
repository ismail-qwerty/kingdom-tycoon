// PATH: desktop/src/main/java/com/ismail/kingdom/desktop/DesktopLauncher.kt
package com.ismail.kingdom.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.ismail.kingdom.KingdomGame

// Desktop launcher for running Kingdom Tycoon on PC
object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            setTitle("Kingdom Tycoon")
            setWindowedMode(1280, 720)
            setForegroundFPS(60)
            useVsync(true)
            setWindowIcon("ui/icon.png")
        }
        Lwjgl3Application(KingdomGame(null), config)
    }
}
