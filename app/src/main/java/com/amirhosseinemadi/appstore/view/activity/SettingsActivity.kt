package com.amirhosseinemadi.appstore.view.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivitySettingsBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.bottomsheet.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsBinding:ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = DataBindingUtil.setContentView(this,R.layout.activity_settings)
        initView()
    }


    private fun initView()
    {
        settingsBinding.language.setOnClickListener(this::langClick)
        settingsBinding.theme.setOnClickListener(this::themeClick)
        settingsBinding.bug.setOnClickListener(this::bugClick)
        settingsBinding.imgBack.setOnClickListener(this::backClick)

        settingsBinding.switchS.isChecked = PrefManager.getUpdate()
        settingsBinding.switchS.setOnCheckedChangeListener(this::switchClick)

        when(PrefManager.getLang())
        {
            "fa" -> { settingsBinding.txtLanguage.text = "فارسی" }

            "en" -> { settingsBinding.txtLanguage.text = "English" }
        }

        when(PrefManager.getTheme())
        {
            "dark" -> { settingsBinding.txtDayNight.text = getString(R.string.dark) }

            "light" -> { settingsBinding.txtDayNight.text = getString(R.string.light) }
        }
    }


    private fun langClick(view:View)
    {
        SettingsFragment("lang",null).show(supportFragmentManager, null)
    }


    private fun themeClick(view:View)
    {
        SettingsFragment("dayNight",null).show(supportFragmentManager, null)
    }


    private fun bugClick(view:View)
    {
        val intent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:amirhossein79.e@gmail.com"))
        startActivity(intent)
    }


    private fun switchClick(button:CompoundButton, isChecked:Boolean)
    {
        PrefManager.setUpdate(isChecked)
    }


    private fun backClick(view:View)
    {
        startActivity(Intent(this,MainActivity::class.java)/*.also { it.putExtra("key","settings") }*/)
        finish()
    }


    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java)/*.also { it.putExtra("key","settings") }*/)
        finish()
    }

}