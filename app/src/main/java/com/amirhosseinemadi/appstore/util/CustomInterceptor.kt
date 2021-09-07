package com.amirhosseinemadi.appstore.util

import android.util.Log
import okhttp3.*
import kotlin.math.log

class CustomInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response
    {
        val request:Request = chain.request()
        var modifiedRequest:Request? = null

        val postParam: FormBody = request.body() as FormBody
        val strSend = postParam.name(0)+" : "+postParam.value(0) +" -- "+postParam.name(1)+" : "+postParam.value(1)

        Log.i("Request to Server : ","( $strSend )")

        val encryptedMap: Map<String, Any> = SecurityManager.encrypt(postParam.value(1))

        modifiedRequest = request.newBuilder()
            .post(
                FormBody.Builder()
                    .add(postParam.name(0), postParam.value(0))
                    .add(postParam.name(1), encryptedMap.get("output") as String)
                    .build()
            ).build()

        val strSendEnc = (modifiedRequest.body() as FormBody).name(0)+" : "+ (modifiedRequest.body() as FormBody).value(0) +" -- "+ (modifiedRequest.body() as FormBody).name(1)+" : "+ (modifiedRequest.body() as FormBody).value(1)
        Log.i("Request to Server : ","( $strSendEnc )")

        val response:Response = chain.proceed(modifiedRequest)
        var modifiedResponse:Response? = null
        val responseBody:ResponseBody? = response.body()

        if (responseBody != null)
        {
            val strRecEnc = response.body()?.string()
            Log.i("Response from Server : ","( $strRecEnc )")

            val decrypted: String = SecurityManager.decrypt(
                strRecEnc!!,
                encryptedMap.get("key") as ByteArray,
                encryptedMap.get("iv") as ByteArray
            )

            Log.i("Response from Server : ","( $decrypted )")

            modifiedResponse = response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(),decrypted))
                .build()

            return modifiedResponse
        }

        return response
    }

}