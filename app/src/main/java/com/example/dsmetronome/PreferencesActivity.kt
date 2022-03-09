package com.example.dsmetronome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
    }

    fun goBack(view: View){
        onBackPressed()
    }
}