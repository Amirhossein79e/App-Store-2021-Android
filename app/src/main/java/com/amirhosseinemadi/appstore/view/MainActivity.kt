package com.amirhosseinemadi.appstore.view
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.amirhosseinemadi.appstore.BuildConfig
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.PrefManager
import com.amirhosseinemadi.appstore.common.SecurityManager
import java.security.*
import java.security.cert.CertificateFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //PrefManager.setUser("Amirhossein")
        val shared = getSharedPreferences("main", MODE_PRIVATE)
        println(shared.getString("user",null))
        println(PrefManager.getUser()+"!!!!!!!!!!!!!!")

    }
}