package com.example.projekt

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import java.util.*

class Ustawienia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ustawienia)

        val sourceintent = intent

        val polskaObraz = findViewById<ImageView>(R.id.jezykPolski)
        val angielskiObraz = findViewById<ImageView>(R.id.jezykAngielski)

        polskaObraz.setOnClickListener{
           // Snackbar.make(findViewById(R.id.ustawieniaLayout),"Język ustawiono na polski", Snackbar.LENGTH_SHORT).show()
            val sIntent: Intent = Intent(this, Panel::class.java)
            sIntent.putExtra("language", "pl")
            sIntent.putExtra("User", sourceintent.getStringExtra("User").toString())
            startActivity(sIntent)
            finish()

        }
        angielskiObraz.setOnClickListener{
          //  Snackbar.make(findViewById(R.id.ustawieniaLayout),"Już wkrótce", Snackbar.LENGTH_SHORT).show()
            val sIntent: Intent = Intent(this, Panel::class.java)
            sIntent.putExtra("language", "en")
            sIntent.putExtra("User", sourceintent.getStringExtra("User").toString())
            startActivity(sIntent)
            finish()
        }
    }
}