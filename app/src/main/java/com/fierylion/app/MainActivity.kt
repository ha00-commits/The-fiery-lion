package com.fierylion.app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var counter: TextView
    private lateinit var status: TextView
    private lateinit var btn: TextView
    private var running = false
    private var startTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            val d = System.currentTimeMillis() - startTime
            val h = TimeUnit.MILLISECONDS.toHours(d)
            val m = TimeUnit.MILLISECONDS.toMinutes(d) % 60
            val s = TimeUnit.MILLISECONDS.toSeconds(d) % 60
            counter.text = String.format("%02d:%02d:%02d", h, m, s)
            handler.postDelayed(this, 1000)
        }
    }

    private fun circle(color: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(color))
            setStroke(8, Color.parseColor("#33FFFFFF"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.parseColor("#0A0A0A"))
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            setPadding(0, 120, 0, 40)
        }

        val title = TextView(this).apply {
            text = "THE FIERY LION"
            setTextColor(Color.WHITE)
            textSize = 26f
            letterSpacing = 0.15f
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "غير مفعل"
            setTextColor(Color.parseColor("#888888"))
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 60)
        }

        btn = TextView(this).apply {
            text = "تفعيل"
            textSize = 28f
            setTextColor(Color.parseColor("#0A0A0A"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            background = circle("#FF6D00")
            layoutParams = LinearLayout.LayoutParams(620, 620)
            setOnClickListener { toggle() }
        }

        counter = TextView(this).apply {
            text = "00:00:00"
            setTextColor(Color.parseColor("#FF6D00"))
            textSize = 40f
            typeface = android.graphics.Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, 70, 0, 6)
        }

        val hint = TextView(this).apply {
            text = "مدة التفعيل"
            setTextColor(Color.parseColor("#666666"))
            textSize = 13f
            gravity = Gravity.CENTER
        }

        val rights = TextView(this).apply {
            text = "أبوجبل ابن الكوت • @P71GP\n© 2026 — جميع الحقوق محفوظة"
            setTextColor(Color.parseColor("#555555"))
            textSize = 11f
            gravity = Gravity.CENTER
            setPadding(0, 60, 0, 0)
        }

        root.addView(title)
        root.addView(status)
        root.addView(btn)
        root.addView(counter)
        root.addView(hint)
        root.addView(rights)
        setContentView(root)
    }

    private fun toggle() {
        if (running) {
            running = false
            handler.removeCallbacks(tick)
            btn.text = "تفعيل"
            btn.background = circle("#FF6D00")
            status.text = "غير مفعل"
            counter.text = "00:00:00"
        } else {
            running = true
            startTime = System.currentTimeMillis()
            btn.text = "إيقاف"
            btn.background = circle("#D50000")
            status.text = "مفعل ✅"
            handler.post(tick)
            clearCache()
            killBackground()
            openWireless()
        }
    }

    private fun clearCache() {
        try {
            cacheDir.listFiles()?.forEach { it.deleteRecursively() }
            externalCacheDir?.listFiles()?.forEach { it.deleteRecursively() }
        } catch (_: Exception) {}
    }

    private fun killBackground() {
        try {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(packageName)
        } catch (_: Exception) {}
    }

    private fun openWireless() {
        try {
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tick)
    }
}
