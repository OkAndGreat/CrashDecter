package com.redrock.crashdecter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.processphoenix.ProcessPhoenix

class MainActivity : AppCompatActivity() {
    private val btn: Button by lazy { findViewById(R.id.btn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Handler(Looper.getMainLooper()).postDelayed({
//            ProcessPhoenix.triggerRebirth(this);
//        }, 1000)

        btn.setOnClickListener {
            throw Exception("eee")
        }

    }
}