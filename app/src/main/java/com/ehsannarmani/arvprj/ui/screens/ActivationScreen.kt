package com.ehsannarmani.arvprj.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.R
import com.ehsannarmani.arvprj.navigation.Routes

@Composable
fun ActivationScreen(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .padding(20.dp)){
        Button(onClick = {
            navController.navigate(Routes.Chat.route){
                popUpTo(0){
                    inclusive = false
                }
            }
        },modifier= Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .height(55.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF002DE3)
        )) {
            Text(text = "Start Messaging",style = buttonTextStyle)
        }
        Column(modifier=Modifier.fillMaxSize().padding(bottom = 32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.activation_png),
                contentDescription = null,
                modifier=Modifier.scale(1.5f),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(64.dp))
            Text(text = "Now you can connect\neasily with your family\nand friends over\ncountries", fontSize = 30.sp, fontWeight = FontWeight.Bold,style = TextStyle(lineHeight = 45.sp, textAlign = TextAlign.Center),modifier=Modifier.fillMaxWidth())
        }
    }
}