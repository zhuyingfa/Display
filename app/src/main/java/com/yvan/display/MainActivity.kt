package com.yvan.display

import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

// 这是一个主要的Activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 此MainActivity引入布局文件
    }

    /**
     * 点击事件
     */
    fun startShared(view: View?) {
        val options = Bundle() // 实例化Bundle
        val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager // 获取显示管理器 并 强转类型为 显示管理器
        val displays = displayManager.displays // 从显示管理器中 获取 当前系统所有的显示屏幕
        val intent = Intent(this, ScreenActivity::class.java) // 准备一个待 激活的Activity 也就是显示第二块屏幕的 目标Activity
        if (displays != null && displays.size > 1) { // 判断屏幕大于1，意味着 有 副屏的存在，才能进入if
            options.putInt("android.activity.launchDisplayId", displays[1].displayId) // 激活Activity前期的设置，设置为第二块屏幕的ID
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // 这种第二块屏幕的显示，标准Flags的设置
            startActivity(intent, options) // 激活 显示第二块屏幕的 目标Activity
        }
    }
}