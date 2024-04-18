package com.ehsannarmani.arvprj.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.R
import com.ehsannarmani.arvprj.models.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeScreen(navController: NavController) {
    val context = LocalContext.current
    val countries = remember {
        mutableStateListOf<Country>()
    }


    LaunchedEffect(Unit) {
        launch(Dispatchers.IO){
            val gson = Gson()
            context.resources.openRawResource(R.raw.countries)
                .bufferedReader()
                .readText()
                .also {
                    val type = object : TypeToken<List<Country>>() {}
                    gson.fromJson(it,type).also {
                        countries.clear()
                        countries.addAll(it)
                    }
                }
        }
    }
    
    Scaffold(modifier=Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Choose a country") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack()}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        })
    }) {it->
        LazyColumn(modifier=Modifier.padding(it)) {
            items(countries){
                Row(modifier= Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp)
                    .clickable {
                        AppData.data["country_code"] = it
                        navController.popBackStack()
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        Text(text = it.flag)
                        Text(text = it.name)
                    }
                    Text(text = it.dial_code)
                }
            }
        }
    }
}