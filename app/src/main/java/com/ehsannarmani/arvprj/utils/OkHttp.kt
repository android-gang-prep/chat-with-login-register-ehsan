package com.ehsannarmani.arvprj.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

val client = OkHttpClient.Builder()
    .callTimeout(20, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(20, TimeUnit.SECONDS)
    .connectTimeout(20, TimeUnit.SECONDS)
    .build()

fun getRequest(
    url: String,
    headers: Map<String, String> = mapOf(),
    onError: (String) -> Unit,
    onResponse: (String) -> Unit,
) {
    println("makeing request to $url")
    runCatching {
        var request = Request.Builder()
            .url(url)
            .get()
        headers.forEach { (key, value) ->
            request = request.addHeader(key, value)
        }
        client.newCall(
            request.build()
        )
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        onResponse(response.body?.string().toString())
                    } else {
                        onError(response.body?.string().toString())
                    }
                }
            })
    }.onFailure {
        onError(it.message.toString())
    }
}

fun postRequest(
    url: String,
    body: JSONObject,
    headers: Map<String, String> = mapOf(),
    onError: (String) -> Unit,
    onResponse: (String) -> Unit,
) {
    runCatching {
        var request =
            Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaType(), body.toString()))

        headers.forEach { (key, value) ->
            request = request.addHeader(key, value)
        }

        client.newCall(request.build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        onResponse(response.body?.string().toString())
                    } else {
                        onError(response.body?.string().toString())
                    }
                }
            })
    }.onFailure {
        onError(it.message.toString())
    }
}

fun putRequest(
    url: String,
    body: JSONObject,
    headers: Map<String, String> = mapOf(),
    onError: (String) -> Unit,
    onResponse: (String) -> Unit,
) {

    runCatching {

        val request =
            Request.Builder()
                .url(url)
                .put(RequestBody.create("application/json".toMediaType(), body.toString()))


        headers.forEach { (key, value) ->
            request.addHeader(key, value)
        }

        client.newCall(request.build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        onResponse(response.body?.string().toString())
                    } else {
                        onError(response.body?.string().toString())
                    }
                }
            })
    }.onFailure {
        onError(it.message.toString())
    }
}