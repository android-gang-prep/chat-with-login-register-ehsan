package com.ehsannarmani.arvprj.models

data class Message(
    val id:Int,
    val date:String,
    val text:String,
    val author: Author
){
    val haveReply = text.contains("[reply")
    val replyToMessageId:Int? get() {
        println("data rep: ${text.split(":")[1].split("]")[0].trim()}")
        return text.split(":")[1].split("]")[0].trim().toIntOrNull()
    }
    val textWithoutReply:String get(){
        return try {
            if (haveReply){
                text.split("]")[1].trim()
            }else{
                text
            }
        }catch (e:Exception){
            ""
        }
    }
    data class Author(
        val id:String,
        val name:String,
        val avatar:String
    )
}
