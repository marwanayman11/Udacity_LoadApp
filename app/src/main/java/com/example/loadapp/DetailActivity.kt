package com.example.loadapp

import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val id = intent.extras?.getInt("nId", -1)
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancel(id!!)
        file_name_value.text = intent.extras?.getString("body")
        status_value.text = when (intent.extras?.getBoolean("status")) {
            true -> getString(R.string.success)
            else -> getString(R.string.fail)
        }
        status_value.setTextColor(
            when (intent.extras?.getBoolean("status")) {
                true -> getColor(R.color.teal_700)
                else -> getColor(R.color.red)
            }
        )
        button.setOnClickListener {
            this.finish()
        }

    }

}
