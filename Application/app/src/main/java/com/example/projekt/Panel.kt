package com.example.projekt

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt.ResponsClasses.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import java.util.*


class Panel : AppCompatActivity() {
    lateinit var user: User
    lateinit var jezyk: String
    lateinit var sumaZamowien:String

    val objectMaper : ObjectMapper = ObjectMapper()
    val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)

        val sIntent = intent
        jezyk = sIntent.getStringExtra("language").toString()

        if(jezyk=="en"){

            val config = resources.configuration
            val locale = Locale("en")
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }
        if(jezyk=="pl" ){
            val config = resources.configuration
            val locale = Locale("pl")
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        user = gson.fromJson(sIntent.getStringExtra("User"),User::class.java)

        sumaZamowien = ""

        val kalendarz: CalendarView = findViewById(R.id.kalendarz)
        val wylogujGuzik: Button = findViewById(R.id.wylogujButton)
        val now = System.currentTimeMillis() - 1000

        kalendarz.minDate=now
        wylogujGuzik.setOnClickListener{
            wyloguj()
        }
        kalendarz.setOnDateChangeListener{ _, year, month, dayOfMonth ->
                wybierzDzien(year, month, dayOfMonth)
        }

        val regulaminPrzycisk: Button = findViewById(R.id.regulaminPanel)
        val instrukcjaPrzycisk: Button = findViewById(R.id.instrukcjaPanel)
        val ustawieniaPrzycisk: Button = findViewById(R.id.ustawieniaButton)
        val kupowaniePrzycisk: Button = findViewById(R.id.kupButton)
        val mojeZakupyPrzycisk: Button = findViewById(R.id.mojeZakupyButton)

        regulaminPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Regulamin::class.java)
            intent.putExtra("User", gson.toJson(user))
            startActivity(intent)
        }
        instrukcjaPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Instrukcja::class.java)
            intent.putExtra("User", gson.toJson(user))
            startActivity(intent)
        }
        ustawieniaPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Ustawienia::class.java)
            intent.putExtra("User", gson.toJson(user))
            startActivity(intent)
        }
        kupowaniePrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Kupowanie::class.java)
            intent.putExtra("User", gson.toJson(user))
            intent.putExtra("sumaZamowien", sumaZamowien)
            launchSettingsActivity2.launch(intent)
        }
        mojeZakupyPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, MojeZakupy::class.java)
            intent.putExtra("User", gson.toJson(user))
            startActivity(intent)
        }
    }

//    override fun onResume() {
//        super.onResume()
//        val sIntent = intent
//        user = gson.fromJson(sIntent.getStringExtra("User"),User::class.java)
//        println(user.printUser())
//    }

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

            var wybranyDzien = "$miesiac$dzien$year"

        dzienGodz(wybranyDzien)

        }


    private val launchSettingsActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode== RESULT_OK)
        {
            val sumaZamowienNowa = result.data?.getStringExtra("sumaZamowienNowa")
            if (sumaZamowienNowa != null) {
                sumaZamowien= sumaZamowienNowa
            }
            user = objectMaper.readValue(
                result.data?.getStringExtra("User"),
                User::class.java
            )
        }
    }

    private val launchSettingsActivity2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result2->
        if(result2.resultCode== RESULT_OK)
        {
            sumaZamowien= ""
            user = objectMaper.readValue(
                result2.data?.getStringExtra("User"),
                User::class.java
            )
        }
    }

    private fun dzienGodz(wybranyDzien: String) {
            val intent: Intent = Intent(this, wyborGodziny::class.java)
            intent.putExtra("Dzien", wybranyDzien)
            intent.putExtra("User", gson.toJson(user))
            intent.putExtra("sumaZamowien", sumaZamowien)
            launchSettingsActivity.launch(intent)
    }


    private fun wyloguj() {
        this@Panel.finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Czy na pewno chcesz się wylogować?")
            .setCancelable(false)
            .setPositiveButton("Tak"){ _, _ -> this@Panel.finish() }
            .setNegativeButton("Nie", null)
            .show()
    }
}
