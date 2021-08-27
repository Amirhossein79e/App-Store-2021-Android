package com.amirhosseinemadi.appstore.view
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import com.amirhosseinemadi.appstore.BuildConfig
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.SecurityManager
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SecurityManager.encrypt("a")



    }
}