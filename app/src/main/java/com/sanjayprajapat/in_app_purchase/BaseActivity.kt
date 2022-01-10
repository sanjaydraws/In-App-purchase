package com.sanjayprajapat.in_app_purchase

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private inline val context: Context
        get() = this

    protected fun init() {
        initArguments()
        initViews()
        setupListener()
        initObservers()
        loadData()
    }

    protected interface OptionClickedListener {
        fun onBackBtnPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun showToast(msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }


    protected abstract fun initArguments()
    protected abstract fun initViews()
    protected abstract fun setupListener()
    protected abstract fun initObservers()
    protected abstract fun loadData()






}
