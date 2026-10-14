package com.example.quotexassistant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ScreenCaptureActivity : AppCompatActivity() {

    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        
        // Request screen capture permission
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Pass the projection result to FloatingService
            val serviceIntent = Intent(this, FloatingService::class.java).apply {
                putExtra("code", resultCode)
                putExtra("data", data)
            }
            startService(serviceIntent)
        }
        finish()
    }
}
