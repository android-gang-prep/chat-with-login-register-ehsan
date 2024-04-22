package com.ehsannarmani.arvprj.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.AspectRatioFrameLayout.ResizeMode
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.R
import com.ehsannarmani.arvprj.navigation.Routes
import java.io.File

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        val exoPlayer = remember {
            ExoPlayer.Builder(context)
                .build()
                .apply {
                    repeatMode = ExoPlayer.REPEAT_MODE_ALL
                }
        }
        LaunchedEffect(Unit) {
            val cacheFile = File(context.cacheDir,"file.mp4")
            cacheFile.createNewFile()
            cacheFile.writeBytes(context.resources.openRawResource(R.raw.splash2).readBytes())
            exoPlayer.addMediaItem(MediaItem.fromUri(cacheFile.toUri()))
            exoPlayer.prepare()
            exoPlayer.play()
        }
        AndroidView(factory = {PlayerView(it)},modifier=Modifier.fillMaxSize()){
            it.player = exoPlayer
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            it.hideController()
            it.useController = false
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .5f)))
        Column(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 88.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Welcome to our online\nchatroom", modifier=Modifier.fillMaxWidth(),style = TextStyle(lineHeight = 35.sp),color = Color.White,textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = {
                             navController.navigate(Routes.SignUp.route)
            },modifier= Modifier
                .fillMaxWidth()
                .height(55.dp), colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF002DE3)
            )) {
                Text(text = "SignUp",style = buttonTextStyle)
            }
            TextButton(onClick = {
                navController.navigate(Routes.SignIn.route)

            },modifier=Modifier.fillMaxWidth()) {
                Text(text = "Login", style = buttonTextStyle)
            }
        }
    }
}

val buttonTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp,color = Color.White)