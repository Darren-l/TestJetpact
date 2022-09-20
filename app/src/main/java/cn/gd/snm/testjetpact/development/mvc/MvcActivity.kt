package cn.gd.snm.testjetpact.development.mvc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.gd.snm.testjetpact.R

/**
 * MVC模式下，数据请求、数据到Ui刷新的驱动由controller层控制。
 *
 */
class MvcActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvc)
    }
}