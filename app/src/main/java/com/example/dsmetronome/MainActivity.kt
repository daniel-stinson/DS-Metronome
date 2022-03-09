package com.example.dsmetronome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    //Goes into the actual app
    fun enterApp(view: View){
        var intent = Intent(this,MetronomeActivity::class.java)
        intent.putExtra("name",nameId.text.toString())
        startActivity(intent)
    }

    //Goes to the about page
    fun aboutApp(view: View){
        var intent = Intent(this,HelpActivity::class.java)
        startActivity(intent)
    }
}