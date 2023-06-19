package com.zero.androidgrpc

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.zero.androidgrpc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.get.setOnClickListener {
            binding.loginIdForGet.text.toString().let {
                if (it.isNotBlank()) viewModel.get(it)
            }
        }

        binding.update.setOnClickListener {
            binding.genreForUpdate.text.toString().let {
                if (it.isNotBlank()) viewModel.update(it)
            }
        }

        viewModel.result.observe(this) {
            binding.result.text = it
        }
    }
}