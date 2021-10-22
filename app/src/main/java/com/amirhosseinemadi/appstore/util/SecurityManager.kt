package com.amirhosseinemadi.appstore.util

import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Base64DataException
import com.amirhosseinemadi.appstore.BuildConfig
import com.amirhosseinemadi.appstore.common.Application
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.*
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
            var iv:ByteArray = ByteArray(16)
            secureRandom.nextBytes(iv)
            val ivSpec:IvParameterSpec = IvParameterSpec(iv)

            aesCipher.init(Cipher.ENCRYPT_MODE,aesKey,ivSpec)

            val rsaCipher:Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

            val x509Spec:X509EncodedKeySpec = X509EncodedKeySpec(Base64.decode(BuildConfig.PUBLIC_KEY,Base64.NO_WRAP))
            val publicKey:PublicKey = KeyFactory.getInstance("RSA").generatePublic(x509Spec);

            rsaCipher.init(Cipher.ENCRYPT_MODE,publicKey)

            val encKey:String = Base64.encodeToString(rsaCipher.doFinal(aesKey.encoded),Base64.NO_WRAP)
            val encIv:String = Base64.encodeToString(rsaCipher.doFinal(iv),Base64.NO_WRAP)
            val encData:String = Base64.encodeToString(aesCipher.doFinal(inputData.toByteArray()),Base64.NO_WRAP)

            val outputData:String = "$encKey@$encIv@$encData"

            val map:MutableMap<String,Any> = HashMap()
            map.put("key",aesKey.encoded)
            map.put("iv",iv)
            map.put("output",outputData)

            return map
        }


        public fun decrypt(inputData:String, aesKey:ByteArray, iv:ByteArray?) : String
        {
            val aesCipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val key:SecretKey = SecretKeySpec(aesKey,"AES")

            val ivSpec:IvParameterSpec = IvParameterSpec(iv)

            aesCipher.init(Cipher.DECRYPT_MODE,key,ivSpec)

            var outputData = ""

            try
            {
                outputData = String(aesCipher.doFinal(Base64.decode(inputData,Base64.NO_WRAP)),StandardCharsets.UTF_8)
            }catch (exception:IllegalBlockSizeException)
            {
                outputData = String(Base64.decode(inputData,Base64.NO_WRAP),StandardCharsets.UTF_8)
            }catch (exception:BadPaddingException)
            {
                outputData = inputData
            }catch (exception:Base64DataException)
            {
                outputData = inputData
            }

            return outputData
        }


        public fun decryptRaw(inputData:String, aesKey:ByteArray, iv:ByteArray?) : ByteArray
        {
            val aesCipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val key:Key = SecretKeySpec(aesKey,"AES")

            val ivSpec:IvParameterSpec = IvParameterSpec(iv)

            aesCipher.init(Cipher.DECRYPT_MODE,key,ivSpec)

            val outputData:ByteArray = aesCipher.doFinal(Base64.decode(inputData,Base64.NO_WRAP))

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

            }else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    val entry:KeyStore.SecretKeyEntry = keyStore.getEntry(alias,null) as KeyStore.SecretKeyEntry
                    key = entry.secretKey
                }else
                {
                    val privateKey:PrivateKey = keyStore.getKey(alias,null) as PrivateKey
                    val publicKey:PublicKey = keyStore.getCertificate(alias).publicKey
                    key = KeyPair(publicKey,privateKey)
                }
            }

            return key
        }


        public fun storeDataEncrypt(inputData: String,alias: String) : String
        {
            var finalString:String = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                val cipher:Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

                val key:SecretKey = storeDataKey(alias) as SecretKey

                cipher.init(Cipher.ENCRYPT_MODE,key)

                val encrypted:String = Base64.encodeToString(cipher.doFinal(inputData.toByteArray()),Base64.NO_WRAP)

                finalString = Base64.encodeToString(cipher.iv,Base64.NO_WRAP)+encrypted

            }else
            {
                val cipher:Cipher = Cipher.getInstance("RSA")

                val key:KeyPair = storeDataKey(alias) as KeyPair

                cipher.init(Cipher.ENCRYPT_MODE,key.public)

                val encrypted:String = Base64.encodeToString(cipher.doFinal(inputData.toByteArray()),Base64.NO_WRAP)

                finalString = encrypted
            }

            return finalString
        }


        public fun storeDataDecrypt(inputData: String,alias: String) : String
        {
            var finalString:String = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                val cipher:Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

                val key:SecretKey = storeDataKey(alias) as SecretKey

                val iv:ByteArray? = Base64.decode(inputData.substring(0,24),Base64.NO_WRAP)
                val ivSpec:IvParameterSpec = IvParameterSpec(iv)

                cipher.init(Cipher.DECRYPT_MODE,key,ivSpec)

                val decrypted:String = String(cipher.doFinal(Base64.decode(inputData.substring(24),Base64.NO_WRAP)))

                finalString = decrypted

            }else
            {
                val cipher:Cipher = Cipher.getInstance("RSA")

                val key:KeyPair = storeDataKey(alias) as KeyPair

                cipher.init(Cipher.ENCRYPT_MODE,key.public)

                val decrypted:String = String(cipher.doFinal(Base64.decode(inputData,Base64.NO_WRAP)))

                finalString = decrypted
            }

            return finalString
        }


        public fun getDigest(inputData:String) : String
        {
            val digest:MessageDigest = MessageDigest.getInstance("SHA1")

            digest.update(inputData.toByteArray())
            val outputData:String = Base64.encodeToString(digest.digest(),Base64.NO_WRAP)

            return outputData
        }

    }

}