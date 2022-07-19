package com.example.projekt

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import com.example.projekt.ResponsClasses.User
import com.google.gson.Gson

class Admin : AppCompatActivity() {

    lateinit var user: User
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val kalendarz: CalendarView = findViewById(R.id.kalendarzAdmin)
        val wylogujGuzik: Button = findViewById(R.id.wylogujAdmin)

        val sIntent = intent
        user = gson.fromJson(sIntent.getStringExtra("User"), User::class.java)


        wylogujGuzik.setOnClickListener(){
            wyloguj()
        }

        kalendarz.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            wybierzDzien(year, month, dayOfMonth)
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Czy na pewno chcesz się wylogować?")
            .setCancelable(false)
            .setPositiveButton("Tak", ){ _, _ -> this@Admin.finish() }
            .setNegativeButton("Nie", null)
            .show()
    }

    private fun wyloguj() {
        this@Admin.finish()
    }

    private fun wybierzDzien(year: Int, month: Int, dayOfMonth: Int) {
        var dzien: String
        var miesiac: String
        if(month<10)
        {
            miesiac="0"+(month+1)
        }
        else
        {
            miesiac=(month+1).toString()
        }
        if(dayOfMonth<10)
        {
            dzien="0"+dayOfMonth
        }
        else
        {
            dzien=dayOfMonth.toString()
        }
        var wybranyDzien = miesiac+dzien+year.toString()

        dzienGodz(wybranyDzien)

    }

    private fun dzienGodz(wybranyDzien: String) {
        val intent: Intent = Intent(this, AdminWidok::class.java)

        intent.putExtra("DzienAdmin", wybranyDzien)
        intent.putExtra("User", gson.toJson(user))
        startActivity(intent)
    }
}