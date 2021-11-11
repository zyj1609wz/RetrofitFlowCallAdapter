package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import com.zyj.retrofit.adapter.FlowCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addCallAdapterFactory(FlowCallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.body.setOnClickListener {
            flowBody()
        }

        binding.response.setOnClickListener {
            flowResponse()
        }
    }

    private fun flowBody() {
        lifecycleScope.launch {
            service.listReposWithBody("zyj1609wz")
                .catch {
                    it.printStackTrace()
                    Log.d("my--", "error:${it.message}")
                }
                .collect { list ->
                    val item = list[0]
                    item?.let {
                        Log.d("my--", "onResponse:id:${item.id} name:${item.name}")
                    }
                }
        }
    }

    private fun flowResponse() {
        lifecycleScope.launch {
            service.listReposWithResponse("zyj1609wz")
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                    Log.d("my--", "error:${it.message} ${Thread.currentThread().name}")
                }
                .collect {
                    val list = it.body()
                    val item = list?.get(0)
                    item?.let {
                        Log.d("my--", "onResponse:id:${item.id} name:${item.name}")
                    }
                }
        }
    }
}