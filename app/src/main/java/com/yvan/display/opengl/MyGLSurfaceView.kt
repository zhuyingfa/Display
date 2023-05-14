package com.yvan.display

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder

class MyGLSurfaceView (context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    val renderer: MyGlRenderer // 定义成员变量：渲染器

    // 布局一定会调用 两个参数的构造函数
    init {
        // 设置EGL版本
        setEGLContextClientVersion(2)
        // 设置渲染器
        renderer = MyGlRenderer(this)
        setRenderer(renderer)
        // RENDERMODE_WHEN_DIRTY 按需渲染，有帧数据的时候，才会去渲染（ 效率高，麻烦，后面需要手动调用一次才行）
        // RENDERMODE_CONTINUOUSLY 每隔16毫秒，读取更新一次，（如果没有显示上一帧）
        renderMode = RENDERMODE_WHEN_DIRTY

        // requestRender();
    }

    // 自定义的MyGLSurfaceView，为了销毁 【渲染器】
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.onSurfaceDestroyed()
    }
}