package com.yvan.display

import android.content.Context
import android.opengl.GLES20
import java.io.*

object OpenGLUtils {
    private const val TAG = "OpenGLUtils"

    val VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    val TEXURE = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    fun glGenTextures(textures: IntArray) {
        GLES20.glGenTextures(textures.size, textures, 0)

        for (i in textures.indices) {
            // 绑定纹理，后续配置纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i])
            /**
             * 必须：设置纹理过滤参数设置
             */
            /*设置纹理缩放过滤*/
            // GL_NEAREST: 使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            // GL_LINEAR:  使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            // 后者速度较慢，但视觉效果好
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            ) // 放大过滤

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            ) //缩小过滤

            /**
             * 可选：设置纹理环绕方向
             */
            //纹理坐标的范围是0-1。超出这一范围的坐标将被OpenGL根据GL_TEXTURE_WRAP参数的值进行处理
            // GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_T 分别为x，y方向。
            // GL_REPEAT:平铺
            // GL_MIRRORED_REPEAT: 纹理坐标是奇数时使用镜像平铺
            // GL_CLAMP_TO_EDGE: 坐标超出部分被截取成0、1，边缘拉伸
            // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,  GLES20.GL_CLAMP_TO_EDGE);
            // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

    fun readAssetTextFile(context: Context, filename: String?): String {
        var br: BufferedReader? = null
        var line: String?
        val sb = StringBuilder()
        try {
            val `is` = context.assets.open(filename!!)
            br = BufferedReader(InputStreamReader(`is`))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return sb.toString()
    }

    fun loadProgram(vSource: String?, fSource: String?): Int {
        /**
         * 顶点着色器
         */
        val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        // 加载着色器代码
        GLES20.glShaderSource(vShader, vSource)
        // 编译（配置）
        GLES20.glCompileShader(vShader)

        // 查看配置 是否成功
        val status = IntArray(1)
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) {
            // 失败
            "load vertex shader:" + GLES20.glGetShaderInfoLog(vShader)
        }

        /**
         * 片元着色器
         * 流程和上面一样
         */
        val fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        // 加载着色器代码
        GLES20.glShaderSource(fShader, fSource)
        // 编译（配置）
        GLES20.glCompileShader(fShader)

        // 查看配置 是否成功
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            // 失败
            throw IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog(vShader))
        }

        /**
         * 创建着色器程序
         */
        val program = GLES20.glCreateProgram()
        // 绑定顶点和片元
        GLES20.glAttachShader(program, vShader)
        GLES20.glAttachShader(program, fShader)
        // 链接着色器程序
        GLES20.glLinkProgram(program)

        // 获得状态
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw IllegalStateException("link program:" + GLES20.glGetProgramInfoLog(program))
        }
        GLES20.glDeleteShader(vShader)
        GLES20.glDeleteShader(fShader)
        return program
    }

    fun copyAssets2SdCard(context: Context, src: String?, dst: String?) {
        try {
            val file = File(dst)
            if (!file.exists()) {
                val `is` = context.assets.open(src!!)
                val fos = FileOutputStream(file)
                var len: Int
                val buffer = ByteArray(2048)
                while (`is`.read(buffer).also { len = it } != -1) {
                    fos.write(buffer, 0, len)
                }
                `is`.close()
                fos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}