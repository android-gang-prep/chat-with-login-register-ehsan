package com.ehsannarmani.arvprj.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.models.Country
import com.ehsannarmani.arvprj.models.ErrorModel
import com.ehsannarmani.arvprj.models.Login
import com.ehsannarmani.arvprj.utils.postRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginViewModel: ViewModel() {

    fun login(
        email:String,
        password:String,
        onError: (String)->Unit,
        onSuccess:(token:String)->Unit,
    ){
        viewModelScope.launch(Dispatchers.IO){
            with(AppData){

                val jsonObject = JSONObject().apply {
                    put("email",email)
                    put("password",password)
                }
                val gson = Gson()
                postRequest(
                    url = "http://wsk2019.mad.hakta.pro/api/user/login",
                    body = jsonObject,
                    onError = {
                        runCatching {
                            gson.fromJson(it,ErrorModel::class.java).also {
                                onError(it.error)
                            }
                        }
                            .onFailure {
                                onError(it.message.toString())
                            }
                    },
                    onResponse = {
                        runCatching{
                            gson.fromJson(it, Login::class.java).also {
                                onSuccess(it.token)
                            }
                        }.onFailure {
                            onError(it.message.toString())
                        }
                    }
                )
            }

        }
    }


}