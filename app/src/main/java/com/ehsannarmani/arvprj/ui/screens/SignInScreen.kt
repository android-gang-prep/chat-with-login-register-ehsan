package com.ehsannarmani.arvprj.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.navigation.Routes
import com.ehsannarmani.arvprj.viewModels.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController,viewModel:LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                )
            }
        })
    }) { paddings ->
        val email = rememberSaveable {
            mutableStateOf("")
        }
        val password = rememberSaveable {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter Your Login Information",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(64.dp))
            AppTextField(value = email.value, onValueChange = {
                email.value = it
            }, placeholder = "Email")
            Spacer(modifier = Modifier.height(8.dp))
            AppTextField(value = password.value, onValueChange = {
                password.value = it
            }, placeholder = "Password")
            Spacer(modifier = Modifier.height(32.dp))

            val sharedPrefs = remember {
                context.getSharedPreferences("main",Context.MODE_PRIVATE)
            }
            Button(onClick = {
                if (email.value.isNotEmpty() && password.value.isNotEmpty()){
                    AppData.data["email"] = email.value
                    AppData.data["password"] = password.value
                    scope.launch(Dispatchers.IO){
                        viewModel.login(
                            email.value,
                            password.value,
                            onError = {
                                scope.launch(Dispatchers.Main){
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onSuccess = {
                                scope.launch(Dispatchers.Main){
                                    sharedPrefs
                                        .edit()
                                        .putString("token",it)
                                        .apply()
                                    navController.navigate(Routes.Chat.route)
                                }
                            }
                        )
                    }
                }else{
                    scope.launch(Dispatchers.Main){
                        Toast.makeText(context, "Please fill all inputs", Toast.LENGTH_SHORT).show()
                    }
                }
            },modifier= Modifier
                .fillMaxWidth()
                .height(55.dp),colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF002DE3)
            )) {
                Text(text = "Login",style = buttonTextStyle)
            }
        }
    }
}