package com.ehsannarmani.arvprj.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.R
import com.ehsannarmani.arvprj.models.Country
import com.ehsannarmani.arvprj.navigation.Routes
import com.ehsannarmani.arvprj.viewModels.SignUpViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel = viewModel()
) {

    val context = LocalContext.current


    val firstCountryCode = remember {
        mutableStateOf<Country?>(null)
    }
    val countryCode = remember {
        mutableStateOf<Country?>(null)
    }


    onResume {
        val saved = AppData.data["country_code"] as Country?
        countryCode.value = saved ?: firstCountryCode.value
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            context.resources.openRawResource(R.raw.countries)
                .bufferedReader()
                .readText()
                .also {
                    val gson = Gson()
                    val type = object : TypeToken<List<Country>>() {}
                    gson.fromJson(it, type)
                        .also {
                            firstCountryCode.value = it.first()
                        }
                }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null
                )
            }
        })
    }) { it ->
        it
        val phoneNumber = remember {
            mutableStateOf("")
        }
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Enter Your Phone Number", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please confirm your country code and enter your phone number",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(45.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.LightGray)
                        .padding(horizontal = 8.dp)
                        .clickable {
                            navController.navigate(Routes.CountryCode.route)
                        },
                    contentAlignment = Alignment.Center
                ) {

                    countryCode.value?.let {
                        Text(text = "${it.flag} ${it.dial_code}")
                    }
                }
                AppTextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    placeholder = "Phone Number",
                    options = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    AppData.data["country_code2"] = countryCode.value!!
                    AppData.data["phone_number"] = phoneNumber.value
                    if (phoneNumber.value.isNotEmpty()) {
                        signUpViewModel.signUp(
                            countryCode = countryCode.value?.code.orEmpty(),
                            phoneNumber = phoneNumber.value,
                            onError = {
                                      scope.launch(Dispatchers.Main){
                                          Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

                                      }
                            },
                            onSuccess = {
                                scope.launch (Dispatchers.Main){
                                    Toast.makeText(context, "Success, Sending Sms", Toast.LENGTH_SHORT).show()

                                }
                                countryCode.value?.let {
                                    signUpViewModel.sendSms(
                                        country = countryCode.value!!,
                                        phone = phoneNumber.value,
                                        onError ={
                                                 scope.launch(Dispatchers.IO){
                                                     Toast.makeText(context, "it", Toast.LENGTH_SHORT).show()

                                                 }
                                        },
                                        onSuccess = {
                                            scope.launch(Dispatchers.Main){
                                                navController.navigate(Routes.Confirmation.route)
                                            }
                                        }
                                    )
                                }

                            }
                        )
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5)
                )
            ) {
                Text(text = "Continue", style = buttonTextStyle)
            }
        }
    }
}

@Composable
fun onResume(resume: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resume()
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }
}

