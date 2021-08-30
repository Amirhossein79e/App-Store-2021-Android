package com.amirhosseinemadi.appstore.view
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.amirhosseinemadi.appstore.BuildConfig
import com.amirhosseinemadi.appstore.R
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

        //SecurityManager.encrypt("a")
//        val keyStore:KeyStore = KeyStore.getInstance("AndroidKeyStore")
//        keyStore.load(null)
//        if (!keyStore.containsAlias("test2")){
//        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,Security.getProvider("AndroidKeyStore"))
//        keyGenerator.init(KeyGenParameterSpec.Builder("test2",KeyProperties.PURPOSE_ENCRYPT or  KeyProperties.PURPOSE_DECRYPT)
//            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//            .setRandomizedEncryptionRequired(false)
//            .build())
//        keyGenerator.generateKey()}else
//        {
//            val keyStore:KeyStore = KeyStore.getInstance("AndroidKeyStore")
//            keyStore.load(null)
//            println(keyStore.aliases().nextElement())
//            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//            val key:KeyStore.Entry = keyStore.getEntry("test",null)
//        }
        SecurityManager.storeDataKey("SSS")
        val keys = KeyStore.getInstance("AndroidKeyStore")
        keys.load(null)
        for (alias in keys.aliases())
        {
            println(alias)
            print("!!!!!!!!!!!!!!!!")
        }
        val entry:SecretKey = keys.getKey("SSS",null) as SecretKey
        print(entry.encoded)






    }
}