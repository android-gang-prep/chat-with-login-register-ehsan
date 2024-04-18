package com.ehsannarmani.arvprj.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.models.Country
import com.ehsannarmani.arvprj.models.ErrorModel
import com.ehsannarmani.arvprj.utils.postRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SignUpViewModel: ViewModel() {

    fun sendSms(
        country: Country,
        phone:String,
        onError: (String)->Unit,
        onSuccess:()->Unit,
    ){
        viewModelScope.launch(Dispatchers.IO){
            with(AppData){

                val jsonObject = JSONObject().apply {
                    put("countryCode",country.dial_code)
                    put("phone",phone)
                }
                val gson = Gson()
                postRequest(
                    url = "http://wsk2019.mad.hakta.pro/api/user/smsCode",
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
                        onSuccess()
                    }
                )
            }
        }
    }


    fun signUp(
        countryCode:String,
        phoneNumber:String,
        onError: (String)->Unit,
        onSuccess:()->Unit,
    ){
        viewModelScope.launch(Dispatchers.IO){
            with(AppData){
                val email = data["email"] as String
                val name = data["name"] as String
                val password = data["password"] as String
                val jsonObject = JSONObject().apply {
                    put("email",email)
                    put("nickName",name)
                    put("password",password)
                    put("phone","$countryCode$phoneNumber")
                }


                val gson = Gson()
                postRequest(
                    url = "http://wsk2019.mad.hakta.pro/api/users",
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
                        if (it.contains("Success")) onSuccess() else onError(it)
                    }
                )
            }

        }
    }
}