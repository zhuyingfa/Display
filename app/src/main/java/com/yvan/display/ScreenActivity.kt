package com.yvan.display

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
// 显示第二块屏幕的 目标ScreenActivity， 继承Activity()
class ScreenActivity : Activity() {

    private var mSurfaceView: MyGLSurfaceView? = null // 自定义的MyGLSurfaceView的成员定义
    private var screenServiceIntent: Intent? = null // 后台服务的Intent
    private var mMediaProjectionManager: MediaProjectionManager? = null // 需要捕获屏幕内容的应用程序管理

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_secondary) // 同学们注意：副屏显示的画面，都在自定义的MyGLSurfaceView中显示
        mSurfaceView = findViewById(R.id.surfaceview) // 自定义的MyGLSurfaceView的成员
        screenServiceIntent = Intent(this, ScreenService::class.java) // 后台服务的Intent构建
        startService(screenServiceIntent) // 启动后台服务的
        requestMediaProjection() // 调用此方法的目的，是为了，开启屏幕录制
    }

    // 开启屏幕录制 屏幕捕获
    private fun requestMediaProjection() {
        mMediaProjectionManager = applicationContext.getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager // 捕获屏幕内容的应用程序管理
        val captureIntent = mMediaProjectionManager!!.createScreenCaptureIntent() // 得到 捕获屏幕的 系统Intent
        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION) // 开始调用系统捕获屏幕
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) { // 开始调用系统捕获屏幕 的 回馈 判断
            val mediaProjection = mMediaProjectionManager!!.getMediaProjection(resultCode, data) // 实例化得到 录制屏幕的核心
            mSurfaceView!!.renderer.setMediaProjection(mediaProjection) // 设置 录制屏幕的核心 到 自定义的MyGLSurfaceView中去
        }
    }

    // 通用常量区域
    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 100 // onActivityResult 反馈后的判断值
    }

    // 销毁时，停止后台服务
    override fun onDestroy() {
        super.onDestroy()
        stopService(screenServiceIntent)
    }
}