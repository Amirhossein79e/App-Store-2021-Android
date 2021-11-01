package com.amirhosseinemadi.appstore.view.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivitySettingsBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.bottomsheet.SettingsFragment
import com.amirhosseinemadi.appstore.view.callback.Callback

class SettingsActivity : AppCompatActivity(),Callback {

    private lateinit var settingsBinding:ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = DataBindingUtil.setContentView(this,R.layout.activity_settings)
        initView()
    }


    private fun initView()
    {
        settingsBinding.language.setOnClickListener { SettingsFragment("lang",this).show(supportFragmentManager, null) }

        settingsBinding.theme.setOnClickListener { SettingsFragment("dayNight",this).show(supportFragmentManager, null) }

        settingsBinding.switchS.isChecked = PrefManager.getUpdate()

        settingsBinding.switchS.setOnCheckedChangeListener { buttonView, isChecked ->

            PrefManager.setUpdate(isChecked)

        }

        settingsBinding.bug.setOnClickListener {
            val intent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:amirhossein79.e@gmail.com"))
            startActivity(intent)
        }

        settingsBinding.imgBack.setOnClickListener{
            val intent:Intent = Intent(this,MainActivity::class.java)
            intent.putExtra("key","settings")
            startActivity(intent)
            finish()
        }

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


    override fun onBackPressed() {
        val intent:Intent = Intent(this,MainActivity::class.java)
        intent.putExtra("key","settings")
        startActivity(intent)
        finish()
    }


    override fun notify(vararg obj: Any?) {
        TODO("Not yet implemented")
    }


}