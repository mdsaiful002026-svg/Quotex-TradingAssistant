package com.example.quotexassistant

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()
        setContentView(R.layout.activity_main)

        // Check for System Overlay Permission
        checkOverlayPermission()

        val startBtn = findViewById<Button>(R.id.startOverlayBtn)
        startBtn.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val intent = Intent(this, FloatingService::class.java)
                startService(intent)
            } else {
                checkOverlayPermission()
            }
        }
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 1234)
        }
    }
}
