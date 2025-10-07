package com.example.edgeview

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
  private lateinit var textureView: TextureView
  private lateinit var camera2Source: Camera2Source

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    textureView = TextureView(this)
    setContentView(textureView)

    val perm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    if (perm != PackageManager.PERMISSION_GRANTED) {
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera()
      }.launch(Manifest.permission.CAMERA)
    } else startCamera()
  }

  private fun startCamera() {
    camera2Source = Camera2Source(this) { surfaceTexture ->
      // attach surfaceTexture to renderer, omitted here for brevity
    }
    camera2Source.start()
  }

  override fun onDestroy() {
    super.onDestroy()
    camera2Source.stop()
  }
}
