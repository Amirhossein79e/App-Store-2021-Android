package com.amirhosseinemadi.appstore.util

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class CustomInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response
    {
        val postParam: FormBody = chain.request().body() as FormBody

        val encryptedMap: Map<String, Any> = SecurityManager.encrypt(postParam.value(1))

        chain.request().newBuilder()
            .post(
                FormBody.Builder()
                    .add(postParam.name(0), postParam.value(0))
                    .add(postParam.name(1), encryptedMap.get("output") as String)
                    .build()
            )

        val decrypted: String = SecurityManager.decrypt(
            chain.proceed(chain.request()).body()?.string()
                .toString(),
            encryptedMap.get("key") as ByteArray,
            encryptedMap.get("iv") as ByteArray
        )

        val response: Response = chain.proceed(chain.request()).newBuilder()
            .body(
                ResponseBody.create(
                    chain.proceed(chain.request()).body()?.contentType(),
                    decrypted
                )
            )
            .build()

        return response
    }

}