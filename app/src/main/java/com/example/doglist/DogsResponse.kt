package com.example.doglist

import com.google.gson.annotations.SerializedName
//los nombre de la anotacion tienen que ser los del json, las variables del nombre que queramos
data class DogsResponse (
    @SerializedName("status")var status:String,
    @SerializedName("message")var images:List<String>)
