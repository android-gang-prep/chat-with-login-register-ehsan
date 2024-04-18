package com.ehsannarmani.arvprj

import com.ehsannarmani.arvprj.models.Country
import kotlinx.coroutines.flow.MutableStateFlow

class AppData {
    companion object{
        var data = mutableMapOf<String,Any>()
        val countryCode = MutableStateFlow<Country?>(null)
    }
}