package com.example.edgeview

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size

class Camera2Source(private val ctx: Context, private val onTextureReady: (SurfaceTexture)->Unit) {
  private var cameraDevice: CameraDevice? = null
  private var session: CameraCaptureSession? = null
  private var thread: HandlerThread? = null
  private var handler: Handler? = null

  fun start() {
    thread = HandlerThread("cam").also { it.start(); handler = Handler(it.looper) }
    val manager = ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val id = manager.cameraIdList[0]
    manager.openCamera(id, object: CameraDevice.StateCallback() {
      override fun onOpened(camera: CameraDevice) {
        cameraDevice = camera
        val tex = SurfaceTexture(10)
        tex.setDefaultBufferSize(640,480)
        onTextureReady(tex)
        val surface = Surface(tex)
        val req = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        req.addTarget(surface)
        camera.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback() {
          override fun onConfigured(s: CameraCaptureSession) {
            session = s
            s.setRepeatingRequest(req.build(), null, handler)
          }
          override fun onConfigureFailed(s: CameraCaptureSession) {}
        }, handler)
      }
      override fun onDisconnected(camera: CameraDevice) { camera.close() }
      override fun onError(camera: CameraDevice, err: Int) { camera.close() }
    }, handler)
  }

  fun stop() {
    session?.close(); cameraDevice?.close(); thread?.quitSafely()
  }
}
