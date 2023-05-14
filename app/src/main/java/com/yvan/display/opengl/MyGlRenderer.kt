package com.yvan.display

import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Surface
import java.util.Locale.filter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// OpenGL的渲染器
class MyGlRenderer(private val surfaceView: MyGLSurfaceView) : GLSurfaceView.Renderer, OnFrameAvailableListener {

    private var surfaceTexName = 0 // OpenGL纹理对象名称(例如通过glGenTextures生成)
    private var surfaceTexture: SurfaceTexture? = null // 定义成员 SurfaceTexture 用于显示的支持
    private var screenFilter: ScreenFilter? = null // 在OpenGL中 对图像纹理图层 的 过滤器
    var mtx = FloatArray(16) // OpenGL更新纹理的数量，是固定化 16
    private var mediaProjection: MediaProjection? = null // 录制屏幕的核心
    private var virtualDisplay: VirtualDisplay? = null // 虚拟显示屏幕
    private var surface: Surface? = null // Surface为了服务于SurfaceTexture
    private var width = INVALID_SIZE // 宽度
    private var height = INVALID_SIZE // 高度

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // 创建一个OpenGL 纹理（图片）
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        surfaceTexName = textures[0]
        surfaceTexture = SurfaceTexture(surfaceTexName)

        // 注册 图像有效 回调
        surfaceTexture!!.setOnFrameAvailableListener(this)
        screenFilter = ScreenFilter(surfaceView.context)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        this.width = width
        this.height = height
        createVirtualDisplay()
        screenFilter!!.setSize(width, height)
    }

    // 创建虚拟的屏幕
    private fun createVirtualDisplay() {
        if (mediaProjection == null || width == INVALID_SIZE || height == INVALID_SIZE) {
            return
        }
        surfaceTexture!!.setDefaultBufferSize(width, height)
        surface = Surface(surfaceTexture)
        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "ScreenRecorder-display0", // 虚拟的屏幕名称 虚拟显示的名称，必须非空
            width, height, 1, // 宽度，高度，虚拟显示在dpi中的密度 必须大于0。
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, // 同学们，此标记的含义是 虚拟显示标志:创建一个公共显示
            surface, // 虚拟内容所显示的表面，说白了就是我们 最终要显示到 【Surface为了服务于SurfaceTexture】 中去
            null, null // 用不到，也没有用过哦
        )
    }

    /**
     * 图像的处理与绘制
     * @param gl
     */
    override fun onDrawFrame(gl: GL10) {
        // todo 更新纹理
        surfaceTexture!!.updateTexImage()
        surfaceTexture!!.getTransformMatrix(mtx)
        screenFilter!!.setTransformMatrix(mtx)
        screenFilter!!.onDraw(surfaceTexName)
    }

    // 被 自定义的MyGLSurfaceView，位了销毁 【渲染器】 哪里调用 的 对外开发API方法
    fun onSurfaceDestroyed() {
        surface!!.release()
        virtualDisplay!!.release()
        mediaProjection!!.stop()
        width = INVALID_SIZE
        height = INVALID_SIZE
    }

    // 此帧可以用时-回调此方法
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        // 请求执行一次 onDrawFrame
        surfaceView.requestRender()
    }

    // 在 ScreenActivity调用 设置 录制屏幕的核心 到 自定义的MyGLSurfaceView中去 的 对外开发API方法
    fun setMediaProjection(mediaProjection: MediaProjection?) {
        this.mediaProjection = mediaProjection
        createVirtualDisplay()
    }

    // 通用常量区域
    companion object {
        private const val INVALID_SIZE = -1  // 宽度 与 高度 的 默认值
    }
}