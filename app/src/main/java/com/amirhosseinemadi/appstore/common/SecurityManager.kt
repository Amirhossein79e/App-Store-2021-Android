package com.amirhosseinemadi.appstore.common

import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.amirhosseinemadi.appstore.BuildConfig
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal
import kotlin.collections.HashMap

class SecurityManager {

    companion object
    {

        public fun encrypt(inputData:String) : Map<String,Any>
        {
            val aesCipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val keyGenerator:KeyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128)
            val aesKey:Key = keyGenerator.generateKey()

            val secureRandom:SecureRandom = SecureRandom()
            var iv:ByteArray? = null
            secureRandom.nextBytes(iv)
            val ivSpec:IvParameterSpec = IvParameterSpec(iv)

            aesCipher.init(Cipher.ENCRYPT_MODE,aesKey,ivSpec)

            val rsaCipher:Cipher = Cipher.getInstance("RSA")

            val x509Spec:X509EncodedKeySpec = X509EncodedKeySpec(Base64.decode(BuildConfig.PUBLIC_KEY,Base64.DEFAULT))
            val publicKey:PublicKey = KeyFactory.getInstance("RSA").generatePublic(x509Spec);

            rsaCipher.init(Cipher.ENCRYPT_MODE,publicKey)

            val encKey:String = Base64.encodeToString(rsaCipher.doFinal(aesKey.encoded),Base64.DEFAULT)
            val encIv:String = Base64.encodeToString(rsaCipher.doFinal(iv),Base64.DEFAULT)
            val encData:String = Base64.encodeToString(aesCipher.doFinal(inputData.toByteArray()),Base64.DEFAULT)

            val outputData:String = "$encKey@$encIv@$encData"

            val map:MutableMap<String,Any> = HashMap()
            map.put("key",aesKey)
            map.put("iv",ivSpec)
            map.put("output",outputData)

            return map
        }


        public fun decrypt(inputData:String, aesKey:ByteArray, iv:ByteArray?) : String
        {
            val aesCipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val keySpec:SecretKeySpec = SecretKeySpec(aesKey,"AES")
            val key:Key = SecretKeyFactory.getInstance("AES").generateSecret(keySpec)

            val ivSpec:IvParameterSpec = IvParameterSpec(iv)

            aesCipher.init(Cipher.DECRYPT_MODE,key,ivSpec)

            val outputData:String = String(aesCipher.doFinal(Base64.decode(inputData,Base64.DEFAULT)),StandardCharsets.UTF_8)

            return outputData
        }


        public fun decryptRaw(inputData:String, aesKey:ByteArray, iv:ByteArray?) : ByteArray
        {
            val aesCipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val keySpec:SecretKeySpec = SecretKeySpec(aesKey,"AES")
            val key:Key = SecretKeyFactory.getInstance("AES").generateSecret(keySpec)

            val ivSpec:IvParameterSpec = IvParameterSpec(iv)

            aesCipher.init(Cipher.DECRYPT_MODE,key,ivSpec)

            val outputData:ByteArray = aesCipher.doFinal(Base64.decode(inputData,Base64.DEFAULT))

            return outputData
        }


        public fun storeDataKey(alias:String) : Any?
        {
            val keyStore:KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            var key:Any? = null

            if (!keyStore.containsAlias(alias))
            {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    val keyGen: KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

                    val keyGenSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setKeySize(128)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build()
                    keyGen.init(keyGenSpec)
                    key = keyGen.generateKey()

                } else
                {
                    val keyGen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")

                    val calendar: Calendar = Calendar.getInstance()
                    val endCalendar: Calendar = Calendar.getInstance()
                    endCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR) + 2)

                    val keyGenSpec: KeyPairGeneratorSpec =
                        KeyPairGeneratorSpec.Builder(Application.component.context())
                            .setAlias(alias)
                            .setKeySize(2048)
                            .setSubject(X500Principal("CN = $alias"))
                            .setSerialNumber(BigInteger("3455335345345"))
                            .setStartDate(calendar.time)
                            .setEndDate(endCalendar.time)
                            .build()
                        keyGen.initialize(keyGenSpec)
                        key = keyGen.generateKeyPair()

                }

            }

            return key
        }


        public fun getDigest(inputData:String) : String
        {
            val digest:MessageDigest = MessageDigest.getInstance("SHA1")

            digest.update(inputData.toByteArray())
            val outputData:String = Base64.encodeToString(digest.digest(),Base64.DEFAULT)

            return outputData
        }

    }

}