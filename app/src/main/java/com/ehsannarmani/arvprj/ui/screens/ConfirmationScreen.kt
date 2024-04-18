package com.ehsannarmani.arvprj.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.models.Country
import com.ehsannarmani.arvprj.navigation.Routes
import com.ehsannarmani.arvprj.viewModels.ConfirmationViewModel
import com.ehsannarmani.arvprj.viewModels.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(navController: NavController, signUpViewModel: SignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), viewModel:ConfirmationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("main",Context.MODE_PRIVATE)
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
    }) {it->
        val focus = remember{
            FocusRequester()
        }
        LaunchedEffect(Unit) {
            focus.requestFocus()
        }
        Box(modifier = Modifier.fillMaxSize()){
            val hiddenCode = remember{
                mutableStateOf("")
            }
            LaunchedEffect(hiddenCode.value) {
                if (hiddenCode.value.length == 4){
                    viewModel.confirm(
                        code = hiddenCode.value,
                        onError = {
                            scope.launch(Dispatchers.Main){
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onSuccess = {
                            scope.launch(Dispatchers.Main){
                                Toast.makeText(
                                    context,
                                    "activation success, logging in",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            scope.launch(Dispatchers.IO){
                                viewModel.login(
                                    email = AppData.data["email"] as String,
                                    password = AppData.data["password"] as String,
                                    onError = {
                                              scope.launch(Dispatchers.Main){
                                                  Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

                                              }
                                    },
                                    onSuccess = {

                                        scope.launch(Dispatchers.Main){
                                            sharedPrefs.edit()
                                                .putString("token",it)
                                                .apply()

                                            Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                                            navController.navigate(Routes.Chat.route)
                                        }

                                    }
                                )
                            }
                        }
                    )
                }
            }
            TextField(value = hiddenCode.value, onValueChange = {
                hiddenCode.value = it
            },modifier=Modifier.focusRequester(focus), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Column(modifier= Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 64.dp)
                .padding(bottom = 64.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = "Enter Code", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "We have sent you an SMS with the code to +62 13091710",modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(64.dp))
                Row(modifier= Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = {
                            focus.requestFocus()
                        }
                    ), horizontalArrangement = Arrangement.SpaceBetween) {
                    repeat(4){
                        val code = hiddenCode.value.getOrNull(it)?.toString()
                        Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center){
                            AnimatedContent(targetState = code, transitionSpec = {
                                (scaleIn()+ fadeIn()) togetherWith  (scaleOut()+ fadeOut())
                            }) {
                                if (code == null){
                                    Box(modifier= Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray))
                                }else{
                                    Text(text = it.orEmpty(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                TextButton(onClick = {
                    scope.launch(Dispatchers.IO){
                        signUpViewModel.sendSms(
                            country = AppData.data["country_code2"] as Country,
                            phone = AppData.data["phone_number"] as String,
                            onError ={
                                scope.launch(Dispatchers.IO){
                                    Toast.makeText(context, "it", Toast.LENGTH_SHORT).show()

                                }
                            },
                            onSuccess = {
                                scope.launch(Dispatchers.Main){
                                    Toast.makeText(context, "Sms Code Resent", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }) {
                    Text(text = "Resend Code",color = Color(0xFF3F51B5), fontSize = 16.sp)
                }
            }
        }

    }
}
