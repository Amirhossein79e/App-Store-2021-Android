package com.amirhosseinemadi.appstore.view.activity

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityMainBinding
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.fragment.AccountFragment
import com.amirhosseinemadi.appstore.view.fragment.CategoryFragment
import com.amirhosseinemadi.appstore.view.fragment.HomeFragment
import com.amirhosseinemadi.appstore.view.fragment.SearchFragment
import com.amirhosseinemadi.appstore.viewmodel.MainVm
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel:MainVm
    private lateinit var mainBinding:ActivityMainBinding
    private lateinit var dialog:Dialog
    private lateinit var frame:FrameLayout
    private lateinit var bottomNav:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainVm()
        mainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main).also { it.viewModel = viewModel }
        initView()
        registerNetworkListener()
    }


    private fun initView()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            Utilities.underApiStatusBarHandle(this)
        }

        bottomNav = mainBinding.bottomNav
        frame = mainBinding.frame

        mainBinding.cardBottomNav.setBackgroundResource(R.drawable.bottom_nav_card_background)
        mainBinding.cardBottomNav.clipChildren = false
        bottomNav.setOnItemSelectedListener(this::itemListener)
    }


    private fun itemListener(item:MenuItem) : Boolean
    {
        when(item.itemId)
        {
            R.id.item_home -> supportFragmentManager.beginTransaction().replace(frame.id,HomeFragment()).commit()

            R.id.item_search -> supportFragmentManager.beginTransaction().replace(frame.id,SearchFragment()).commit()

            R.id.item_category -> supportFragmentManager.beginTransaction().replace(frame.id,CategoryFragment()).commit()

            R.id.item_account -> supportFragmentManager.beginTransaction().replace(frame.id,AccountFragment()).commit()
        }

        item.isChecked = true

        return false
    }


    private fun registerNetworkListener()
    {
        val connectivityManager:ConnectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        dialog = Utilities.dialogIcon(this,R.drawable.ic_network_error, R.string.network_error,R.string.network_pos,null,true,false,
            {
                val intent:Intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            },null)

        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),object : ConnectivityManager.NetworkCallback()
        {
            override fun onAvailable(network: Network)
            {
                super.onAvailable(network)
                if (dialog.isShowing)
                {
                    runOnUiThread{ dialog.dismiss() }
                }
            }

            override fun onLost(network: Network)
            {
                super.onLost(network)
                if (!dialog.isShowing)
                {
                    runOnUiThread{ dialog.show() }
                }
            }

            override fun onUnavailable()
            {
                super.onUnavailable()
                if (!dialog.isShowing)
                {
                    runOnUiThread{ dialog.show()}
                }
            }
        })
    }

}