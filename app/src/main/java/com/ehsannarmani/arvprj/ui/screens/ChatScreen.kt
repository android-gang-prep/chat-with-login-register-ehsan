package com.ehsannarmani.arvprj.ui.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsannarmani.arvprj.R
import com.ehsannarmani.arvprj.models.Content
import com.ehsannarmani.arvprj.models.Message
import com.ehsannarmani.arvprj.models.MessageText
import com.ehsannarmani.arvprj.models.Profile
import com.ehsannarmani.arvprj.utils.getRequest
import com.ehsannarmani.arvprj.utils.postRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ChatScreen(navController: NavController) {
    val context = LocalContext.current
    val gson = Gson()
    val messages = remember {
        mutableStateListOf<Message>()
    }
    val scrollState = rememberLazyListState()
    LaunchedEffect(messages.count()) {
        if (messages.isNotEmpty()){
            scrollState.scrollToItem(0)
        }
    }
    val scope = rememberCoroutineScope()
    val profile = remember {
        mutableStateOf<Profile?>(null)
    }

    val profileColors = remember {
        mutableStateMapOf<String, Color>()
    }
    var token by remember {
        mutableStateOf("")
    }
    val replyMessage = remember {
        mutableStateOf<Message?>(null)
    }

    LaunchedEffect(Unit) {
        token =
            context.getSharedPreferences("main", Context.MODE_PRIVATE).getString("token", "") ?: ""
        launch(Dispatchers.IO) {
            while (true) {
                getRequest(
                    url = "http://wsk2019.mad.hakta.pro/api/messages",
                    onError = {
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onResponse = {
                        val type = object : TypeToken<Content<List<Message>>>() {}
                        val result = gson.fromJson(it, type).content.map {
                            val text = it.text
                                .replace("\\n", "")
                                .replace("\\", "")
                            if (it.author.id !in profileColors.keys) {
                                profileColors.put(it.author.id, badgeColors.random())
                            }
                            val decodedText = gson.fromJson(text, MessageText::class.java)
                            it.copy(text = decodedText.text)
                        }
                        runCatching {
                            println("adding new messages")
                            messages.addAll(result.filter { it.id !in messages.map { it.id } })

                        }
                    },
                    headers = mapOf(
                        "Token" to token
                    )
                )
                delay(1000)
            }
        }
        launch(Dispatchers.IO) {
            getRequest(
                url = "http://wsk2019.mad.hakta.pro/api/user/profile",
                headers = mapOf("Token" to token),
                onError = {
                    scope.launch(Dispatchers.Main) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                onResponse = {
                    val type = object : TypeToken<Content<Profile>>() {}
                    profile.value = gson.fromJson(it, type).content
                }
            )
        }
    }
    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .background(
            Color(0xfff7f7fc)
        )) {
        val width = maxWidth.value
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                reverseLayout = true,
                state = scrollState,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (messages.isEmpty() || profile.value == null) {
                    item {
                        CircularProgressIndicator()
                    }
                } else {
                    items(messages.reversed(), key = {it.id}) {
                        val isMe = it.author.id == profile.value?.id
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    end = if (isMe) 8.dp else 22.dp
                                )
                                .widthIn(max = (width - (width / 8)).dp)
                        ) {
                            val offsetX = remember {
                                Animatable(0f)
                            }

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(x = offsetX.value.toInt(), y = 0) }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDrag = { _, amount ->
                                            if (offsetX.value.absoluteValue <= 200f && amount.x < 0) {
                                                scope.launch {
                                                    offsetX.snapTo(offsetX.value + amount.x)
                                                }
                                            }
                                            if (offsetX.value.absoluteValue >= 195f) {
                                                println("detected reply: $it")
                                                replyMessage.value = it
                                            }
                                        },
                                        onDragEnd = {
                                            scope.launch {
                                                offsetX.animateTo(
                                                    0f,
                                                    animationSpec = tween(500)
                                                )
                                            }
                                        }
                                    )
                                }){
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    ,
                                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                ) {
                                    Box {
                                        Column(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topEnd = 12.dp,
                                                        topStart = 12.dp,
                                                        bottomEnd = if (!isMe) 12.dp else 0.dp,
                                                        bottomStart = if (!isMe) 0.dp else 12.dp
                                                    )
                                                )
                                                .background(
                                                    if (isMe) Color(0xff002DE3) else Color.White
                                                )
                                                .padding(16.dp)
                                        ) {
                                            if (it.haveReply){
                                                Row(modifier= Modifier
                                                    .fillMaxWidth()
                                                    .height(65.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (isMe) Color.White else Color(
                                                            0xffEDEDED
                                                        )
                                                    )
                                                ) {
                                                    val pink = Color(0xffFF00F5)
                                                    val blue = Color(0xff002DE3)
                                                    val replyColor = if (isMe) pink else blue
                                                    val findReplyMessage = messages.find { filter-> filter.id == it.replyToMessageId }
                                                    println("reply id: $findReplyMessage")
                                                    Box(modifier = Modifier
                                                        .fillMaxHeight()
                                                        .width(5.dp)
                                                        .background(replyColor))
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                                                        Text(text = (if (findReplyMessage?.author?.id == profile.value?.id) "You" else findReplyMessage?.author?.id?.take(2)).orEmpty(),color = replyColor, fontSize = 13.sp)
                                                        Text(text = findReplyMessage?.textWithoutReply.orEmpty(),color = Color.Black)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                            Text(
                                                text = it.textWithoutReply,
                                                color = if (isMe) Color.White else Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            val (hour, minute, second) = try {
                                                it.date.split(" ")[1].split(":")
                                            }catch (e:Exception){
                                                listOf(0,0,0)
                                            }
                                            Text(
                                                text = "$hour:$minute",
                                                color = if (isMe) Color.White else Color.LightGray,
                                                fontSize = 13.sp
                                            )
                                        }
                                        if (!isMe) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .offset(x = 16.dp, y = 16.dp)
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        profileColors.getOrDefault(
                                                            it.author.id,
                                                            Color.Red
                                                        )
                                                    ), contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = it.author.id.take(2), color = Color.White)
                                            }
                                        }
                                    }
                                }
                                Box(modifier = Modifier
                                    .size(50.dp)
                                    .offset(x = 75.dp)
                                    .align(Alignment.CenterEnd)
                                    .clip(CircleShape)
                                    .background(Color(0xffE6E6E6)), contentAlignment = Alignment.Center){
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_message),
                                        contentDescription = null,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(22.dp))
                        }
                    }
                }
            }
            val message = remember {
                mutableStateOf("")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Divider(thickness = .5.dp)
                AnimatedVisibility(replyMessage.value != null,enter = expandVertically(),exit = shrinkVertically()) {
                    if (replyMessage.value != null){
                        Row(modifier= Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier=Modifier.padding(top = 12.dp)) {
                                Text(text = "Reply to ${replyMessage.value?.author?.id?.take(2)}",color = Color(0xff0373F3))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = replyMessage.value?.textWithoutReply.orEmpty())
                            }
                            IconButton(onClick = { replyMessage.value = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color(0xff0373F3)
                                )
                            }
                        }
                    }
                }
                TextField(value = message.value,
                    onValueChange = { message.value = it },
                    placeholder = {
                        Text(text = "Waiting for you",color = Color.Black)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (message.value.isNotEmpty()) {
                                        scope.launch(Dispatchers.IO) {
                                           if (message.value.isNotEmpty()){
                                               val jsonObject = JSONObject().apply {
                                                   if (replyMessage.value == null) {
                                                       put("text", message.value)
                                                   } else {
                                                       put(
                                                           "text",
                                                           "[reply:${replyMessage.value?.id}] ${message.value}"
                                                       )
                                                   }
                                               }
                                               postRequest(
                                                   url = "http://wsk2019.mad.hakta.pro/api/messages",
                                                   body = jsonObject,
                                                   headers = mapOf("Token" to token),
                                                   onError = {
                                                       scope.launch(Dispatchers.Main) {
                                                           Toast
                                                               .makeText(
                                                                   context,
                                                                   it,
                                                                   Toast.LENGTH_SHORT
                                                               )
                                                               .show()
                                                       }
                                                   },
                                                   onResponse = {
                                                       message.value = ""
                                                       replyMessage.value = null
                                                   }
                                               )
                                           }
                                        }
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = null,
                                tint = Color(0xff002DE3),
                                modifier=Modifier.size(30.dp)
                            )
                        }
                    })
            }
        }
    }
}

val badgeColors = listOf(
    Color(0xffFF72F9),
    Color(0xffA293FF),
    Color(0xFFFF9800),
    Color(0xFFFF5722),
    Color(0xFF009688),
    Color(0xFFE91E63),
)
