package com.amirhosseinemadi.appstore.view.bottomsheet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentSettingsBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.activity.SplashActivity
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment(private val mode:String, private val callback:Callback?) : BottomSheetDialogFragment() {

    private lateinit var settingBinding:FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        settingBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false)
        initView()

        return settingBinding.root
    }


    private fun initView()
    {
        settingBinding.txtEn.setOnClickListener(this::langClick)
        settingBinding.txtFa.setOnClickListener(this::langClick)
        settingBinding.txtDark.setOnClickListener(this::themeClick)
        settingBinding.txtLight.setOnClickListener(this::themeClick)

        if (PrefManager.getLang().equals("en"))
        {
            settingBinding.txtFa.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
            settingBinding.txtEn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,ContextCompat.getDrawable(requireContext(),R.drawable.ic_check),null)
        }else if(PrefManager.getLang().equals("fa"))
        {
            settingBinding.txtEn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
            settingBinding.txtFa.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,ContextCompat.getDrawable(requireContext(),R.drawable.ic_check),null)
        }

        if (PrefManager.getTheme().equals("dark"))
        {
            settingBinding.txtLight.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
            settingBinding.txtDark.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,ContextCompat.getDrawable(requireContext(),R.drawable.ic_check),null)
        }else if(PrefManager.getTheme().equals("light"))
        {
            settingBinding.txtDark.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
            settingBinding.txtLight.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,ContextCompat.getDrawable(requireContext(),R.drawable.ic_check),null)
        }

        when(mode)
        {
            "lang" ->
            {
                settingBinding.dayNight.visibility = View.GONE
                settingBinding.language.visibility = View.VISIBLE
            }

            "dayNight" ->
            {
                settingBinding.language.visibility = View.GONE
                settingBinding.dayNight.visibility = View.VISIBLE
            }
        }
    }


    override fun getTheme(): Int {
        return R.style.bottom_sheet_theme
    }


    private fun langClick(view:View)
    {
        when(view.id)
        {
            R.id.txt_en ->
            {
                if (!PrefManager.getLang().equals("en"))
                {
                    PrefManager.setLang("en")
                    dismiss()
                    startActivity(Intent(requireContext(),SplashActivity::class.java))
                    requireActivity().finishAffinity()
                }
            }

            R.id.txt_fa ->
            {
                if (!PrefManager.getLang().equals("fa"))
                {
                    PrefManager.setLang("fa")
                    dismiss()
                    startActivity(Intent(requireContext(),SplashActivity::class.java))
                    requireActivity().finishAffinity()
                }
            }
        }

    }


    private fun themeClick(view:View)
    {
        when(view.id)
        {
            R.id.txt_dark ->
            {
                dismiss()
                PrefManager.setTheme("dark")
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    {
                        requireActivity().finishAffinity()
                        startActivity(Intent(requireContext(),SplashActivity::class.java))
                    }
                }
            }

            R.id.txt_light ->
            {
                dismiss()
                PrefManager.setTheme("light")
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    {
                        requireActivity().finishAffinity()
                        startActivity(Intent(requireContext(),SplashActivity::class.java))
                    }
                }
            }
        }

    }

}