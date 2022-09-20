package cn.gd.snm.testjetpact.development

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.gd.snm.testjetpact.R
import cn.gd.snm.testjetpact.development.mvc.MvcActivity
import cn.gd.snm.testjetpact.development.mvi.MviActivity
import cn.gd.snm.testjetpact.development.mvp.MvpActivity
import cn.gd.snm.testjetpact.development.mvvm.MvvmActivity
import kotlinx.android.synthetic.main.activity_development_main.*

class DevelopmentMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_development_main)
        setOnclickLis()
    }

    /**
     * 跳转测试页面
     *
     */
    private fun setOnclickLis() {
        dev_mvc_bt.setOnClickListener {
            val intent = Intent(DevelopmentMainActivity@this,MvcActivity::class.java)
            DevelopmentMainActivity@this.startActivity(intent)
        }

        dev_mvp_bt.setOnClickListener {
            val intent = Intent(DevelopmentMainActivity@this,MvpActivity::class.java)
            DevelopmentMainActivity@this.startActivity(intent)
        }

        dev_mvvm_bt.setOnClickListener {
            val intent = Intent(DevelopmentMainActivity@this, MvvmActivity::class.java)
            DevelopmentMainActivity@this.startActivity(intent)
        }

        dev_mvi_bt.setOnClickListener {
            val intent = Intent(DevelopmentMainActivity@this, MviActivity::class.java)
            DevelopmentMainActivity@this.startActivity(intent)
        }
    }
}