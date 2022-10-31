package com.redrock.crashdecter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.redrock.crashdecter.ui.login.LoginActivity


class MainActivity : AppCompatActivity() {
    private val btn: Button by lazy { findViewById(R.id.btn) }
    private val btn1: Button by lazy { findViewById(R.id.btn1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            throw Exception("eee")
        }

        btn1.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}