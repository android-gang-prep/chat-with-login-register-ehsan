package com.ehsannarmani.arvprj.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
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
    val filteredCountries = remember {
        mutableStateOf<List<Country>?>(null)
    }


    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val gson = Gson()
            context.resources.openRawResource(R.raw.countries)
                .bufferedReader()
                .readText()
                .also {
                    val type = object : TypeToken<List<Country>>() {}
                    gson.fromJson(it, type).also {
                        countries.clear()
                        countries.addAll(it)
                    }
                }
        }
    }

    val searchOpen = remember {
        mutableStateOf(false)
    }
    val searchQuery = remember {
        mutableStateOf("")
    }
    val scrollState = rememberScrollState()
    val scrollPercentReached = scrollState.value/scrollState.maxValue.toDouble()
    LaunchedEffect(searchQuery.value) {
        if (searchOpen.value){
            filteredCountries.value = countries.filter { it.name.containsOneOf(searchQuery.value) }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {
            AnimatedContent(targetState = searchOpen.value) {
                if (it) {
                    TextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(text = "Search...")
                        }
                    )
                } else {
                    Text(text = "Choose a country")
                }
            }
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }, actions = {
            IconButton(onClick = {
                if (searchOpen.value){
                    filteredCountries.value = null
                }
                searchOpen.value = !searchOpen.value
            }) {
                AnimatedContent(targetState = searchOpen.value, transitionSpec = {
                    (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                }) {
                    if (it) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                }
            }
        })
    }) { it ->
        BoxWithConstraints(modifier= Modifier
            .fillMaxSize()
            .padding(it)) {
            val height = maxHeight.value
            Row(modifier=Modifier.fillMaxSize()) {
                println(scrollPercentReached)
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                ) {
                    (filteredCountries.value ?: countries).forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 20.dp)
                                .clickable {
                                    AppData.data["country_code"] = it
                                    navController.popBackStack()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = it.flag)
                                Text(text = it.name)
                            }
                            Text(text = it.dial_code,color = Color(0xFF2196F3))
                        }
                    }
                }

                val dividerY = try {
                    val value = (height*scrollPercentReached)
                    if (value.isNaN()){
                        0.dp
                    }else{
                        value.dp
                    }
                }catch (e:Exception){
                    0.dp
                }
                Box(modifier= Modifier
                    .width(7.dp)
                    .height(45.dp)
                    .offset(y = dividerY)
                    .clip(CircleShape)
                    .background(Color.DarkGray.copy(.3f))
                ) {

                }
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}