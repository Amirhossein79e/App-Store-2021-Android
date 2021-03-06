package com.amirhosseinemadi.appstore.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityMainBinding
import com.amirhosseinemadi.appstore.util.DownloadManager
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.fragment.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding:ActivityMainBinding
    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initView()
        registerNetworkListener()
    }


    private fun initView()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            Utilities.underApiStatusBarHandle(this)
        }

        mainBinding.bottomNav.setOnItemSelectedListener(this::itemListener)
        if (intent.extras != null && intent.extras!!.getString("key") != null)
        {
            when(intent.extras!!.getString("key"))
            {
                "settings" -> { mainBinding.bottomNav.selectedItemId = R.id.item_account }

                "update" -> { mainBinding.bottomNav.selectedItemId = R.id.item_account }

                "download" ->
                {
                    if (DownloadManager.downloadQueue?.get(0)?.packageName != null)
                    {
                        supportFragmentManager.beginTransaction().add(R.id.frame,AppFragment(DownloadManager.downloadQueue!!.get(0).packageName!!),"appFragment").commit()
                    }else
                    {
                        mainBinding.bottomNav.selectedItemId = R.id.item_home
                    }

                }
            }
        }else
        {
            mainBinding.bottomNav.selectedItemId = R.id.item_home
        }
    }


    private fun itemListener(item:MenuItem) : Boolean
    {
        when(item.itemId)
        {
            R.id.item_home -> supportFragmentManager.beginTransaction().replace(mainBinding.frame.id,HomeFragment(),"homeInit").addToBackStack("homeInit").commit()

            R.id.item_search -> supportFragmentManager.beginTransaction().replace(mainBinding.frame.id,SearchFragment(),"searchInit").addToBackStack("searchInit").commit()

            R.id.item_category -> supportFragmentManager.beginTransaction().replace(mainBinding.frame.id,CategoryFragment(),"categoryInit").addToBackStack("categoryInit").commit()

            R.id.item_account -> supportFragmentManager.beginTransaction().replace(mainBinding.frame.id,AccountFragment(),"accountInit").addToBackStack("accountInit").commit()
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


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0)
        {
            super.onBackPressed()
        }
    }

}