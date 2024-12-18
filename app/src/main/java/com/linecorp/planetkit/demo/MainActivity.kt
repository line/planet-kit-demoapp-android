package com.linecorp.planetkit.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.linecorp.planetkit.demo.databinding.ActivityMainBinding
import com.linecorp.planetkit.demo.uikit.utils.Permissions


class MainActivity: AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val requiredPermissions = Permissions.checkAllRequirePermissions(this)
        if (requiredPermissions.isNotEmpty()) {
            Permissions.requestPermissions(this, requiredPermissions.toTypedArray())
        }
    }
}