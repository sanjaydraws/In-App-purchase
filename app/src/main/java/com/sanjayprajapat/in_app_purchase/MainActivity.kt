package com.sanjayprajapat.in_app_purchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sanjayprajapat.in_app_purchase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.apply {
            setContentView(this.root)
            executePendingBindings()
            lifecycleOwner = this@MainActivity
        }

    }
}