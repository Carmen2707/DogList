package com.example.doglist

import android.annotation.SuppressLint
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doglist.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnQueryTextListener {
    //binding es para enlazar las vistas al codigo
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImage= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svDogs.setOnQueryTextListener(this)//le implementamos la clase al buscador
        initRecyclerView()
    }

    private fun initRecyclerView() {
       adapter= DogAdapter(dogImage)
           binding.rvDogs.layoutManager=LinearLayoutManager(this)
       binding.rvDogs.adapter=adapter
    }

    //creamos una instancia del objecto retrofit, va a tener la url, la conversion de json y la configuracion para hacer llamadas a internet
    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")//siempre tiene que terminar en /
            .addConverterFactory(GsonConverterFactory.create())//la libreria de gson converter
            .build()
    }
    //aqui nos llga lo que el usuario ha escrito

    private fun searchByName(query:String){
        //corrutina para que todo lo que ejecutemos dentro se haga en un hilo secundaro(la llamada a internet)
        CoroutineScope(Dispatchers.IO).launch {
            val call=getRetrofit().create(APIService::class.java).getDogsByBreeds("$query/images")//dame el retrofit cargado y le pasamos la interfaz
            val puppies=call.body()//body es donde esta la respuesta de la llamada
           runOnUiThread{
               if(call.isSuccessful){//si ha funcionado la llamada...
                val images=puppies?.images ?: emptyList()//ponemos ? porque puede ser nulo o un listado de strings, ?: es que si lo de delante es nulo tenemos una lista vacia
                   dogImage.clear()
                   dogImage.addAll(images)
                   adapter.notifyDataSetChanged()
               }else{
                   showError()
               }
               hideKeyboard() //oculta el teclado cada vez que le damos a buscar
           }



        }
    }

    private fun hideKeyboard() {
        val imm=getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken,0)//viewRoot es la id de contraint del activitymain
    }

    private fun showError() {
        Toast.makeText(this,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()
    }
//metodos que avisan cuando el usuario escriba o borre
    override fun onQueryTextSubmit(query: String?): Boolean {
        //cuando le demos habuscar se ejecuta este metodo
        if (!query.isNullOrEmpty()){//si lo que ha escrito el usuario no es vacio ni nulo
            searchByName(query.toLowerCase())//lo ponemos en minusculas para evitar problemas con el servidor

        }
    return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}