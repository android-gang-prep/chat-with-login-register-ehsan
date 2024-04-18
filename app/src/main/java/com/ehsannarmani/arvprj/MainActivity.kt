package com.ehsannarmani.arvprj

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ehsannarmani.arvprj.navigation.Routes
import com.ehsannarmani.arvprj.ui.screens.ChatScreen
import com.ehsannarmani.arvprj.ui.screens.ConfirmationScreen
import com.ehsannarmani.arvprj.ui.screens.CountryCodeScreen
import com.ehsannarmani.arvprj.ui.screens.PhoneNumberScreen
import com.ehsannarmani.arvprj.ui.screens.SignInScreen
import com.ehsannarmani.arvprj.ui.screens.SignUpScreen
import com.ehsannarmani.arvprj.ui.screens.SplashScreen
import com.ehsannarmani.arvprj.ui.theme.ArvPrjTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArvPrjTheme(false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddings -> paddings
                    val navController = rememberNavController()
                    val sharedPrefs = remember {
                        getSharedPreferences("main",Context.MODE_PRIVATE)
                    }
                    val start = if (sharedPrefs.getString("token",null) != null) {
                        Routes.Chat.route
                    }else{
                        Routes.Splash.route
                    }
                    NavHost(navController = navController, startDestination = start){
                        composable(Routes.Splash.route){
                            SplashScreen(navController = navController)
                        }
                        composable(Routes.SignIn.route){
                            SignInScreen(navController = navController)
                        }
                        composable(Routes.PhoneNumber.route){
                            PhoneNumberScreen(navController = navController)
                        }
                        composable(Routes.SignUp.route){
                            SignUpScreen(navController = navController)
                        }
                        composable(Routes.Confirmation.route){
                            ConfirmationScreen(navController = navController)
                        }
                        composable(Routes.CountryCode.route){
                            CountryCodeScreen(navController = navController)
                        }
                        composable(Routes.Chat.route){
                            ChatScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
