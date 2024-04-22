package com.ehsannarmani.arvprj.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.AppData
import com.ehsannarmani.arvprj.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
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
        val name = rememberSaveable {
            mutableStateOf("")
        }
        val password = rememberSaveable {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter Your Basic Information",
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
            AppTextField(value = name.value, onValueChange = {
                name.value = it
            }, placeholder = "Name")
            Spacer(modifier = Modifier.height(8.dp))
            AppTextField(value = password.value, onValueChange = {
                password.value = it
            }, placeholder = "Password")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                if (email.value.isNotEmpty() && name.value.isNotEmpty() && password.value.isNotEmpty()){
                    if (email.value.validEmail()){
                        if (password.value.length >= 6 ){
                            if(password.value.containsOneOf("!","?","*",",")){
                                if (password.value.any { it in 'A'..'Z' || it in 'a'..'z' }){
                                    AppData.data["email"] = email.value
                                    AppData.data["name"] = name.value
                                    AppData.data["password"] = password.value
                                    navController.navigate(Routes.PhoneNumber.route)
                                }else{
                                    scope.launch(Dispatchers.Main){
                                        Toast.makeText(context, "Password should be contains at least one alphabet", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }else{
                                scope.launch(Dispatchers.Main){
                                    Toast.makeText(context, "Password should be contains one of !,?,*,(,)", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            scope.launch(Dispatchers.Main){
                                Toast.makeText(context, "Password should be at least 6 chars", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        scope.launch(Dispatchers.Main){
                            Toast.makeText(context, "Email is not valid", Toast.LENGTH_SHORT).show()

                        }
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
                Text(text = "Continue",style = buttonTextStyle)
            }
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    options:KeyboardOptions = KeyboardOptions.Default
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.LightGray), contentAlignment = Alignment.CenterStart
    ) {
        TextField(
            value = value, onValueChange = onValueChange, modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            keyboardOptions = options,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            placeholder = {
                Text(text = placeholder, fontSize = 12.sp)
            }, textStyle = TextStyle(fontSize = 12.sp)
        )
    }
}
fun String.containsOneOf(vararg value:String):Boolean{
    return value.any {
        contains(it)
    }
}
fun String.validEmail():Boolean{
    return if (contains("@")){
        var split = split("@")
        if (split.count() == 2){
            split = split[1].split(".")
            split.count() ==2
        }else{
            false
        }
    }else{
        false
    }
}