package com.example.doglist

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

//aqui esta el metodo por el cual accedemos a nuestro api
interface APIService {

    @GET //tipo de llamada
    //dame los perros por raza
   suspend fun getDogsByBreeds(@Url url:String): Response<DogsResponse>

}