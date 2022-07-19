package com.example.projekt

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.projekt.ResponsClasses.User
import com.example.projekt.network.RaspiLedAPIService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.net.InetAddress
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var raspiLedAPIService: RaspiLedAPIService
    private lateinit var user: User
    val objectMaper : ObjectMapper = ObjectMapper()
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this,
            arrayOf(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ),
            1)

        raspiLedAPIService = RaspiLedAPIService(application)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val regulaminPrzycisk: Button = findViewById(R.id.regulaminButton)
        val instrukcjaPrzycisk: Button = findViewById(R.id.instrukcjaButton)
        val logowaniePrzycisk: Button = findViewById(R.id.logowanieButton)
        val rejestracjaPrzycisk: Button = findViewById(R.id.rejestracjaButton)

        regulaminPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Regulamin::class.java)
            intent.putExtra("Login", " ")
            startActivity(intent)
        }
        instrukcjaPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Instrukcja::class.java)
            intent.putExtra("Login", " ")
            startActivity(intent)
        }
        rejestracjaPrzycisk.setOnClickListener{
            val intent: Intent = Intent(this, Rejestracja::class.java)
            intent.putExtra("Login", " ")
            startActivity(intent)
        }

        logowaniePrzycisk.setOnClickListener{
            logowanie()
        }
    }

    private fun logowanie() {
        val login: EditText = findViewById(R.id.login)
        val haslo: EditText = findViewById(R.id.haslo)


        user = objectMaper.readValue(
            gson.toJson(raspiLedAPIService.login(login.text.toString(),haslo.text.toString())),
            User::class.java)

        if(user.login!="ERROR" && user.phoneNumber!="ERROR" && user.password!="ERROR" && user.role == false){
            val intent: Intent = Intent(this, Panel::class.java)
            intent.putExtra("User", gson.toJson(user))
            startActivity(intent)
        }else if(user.login!="ERROR" && user.phoneNumber!="ERROR" && user.password!="ERROR" && user.role == true){
            val intent: Intent = Intent(this, Admin::class.java)
            intent.putExtra("User", gson.toJson(user))
            startActivity(intent)
        }else{
            Snackbar.make(findViewById(R.id.MainActivity),"Źle wpisane dane", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun sprawdzLogowanie(login: String, haslo: String): Boolean {
        if(login.length>5 && haslo.length>5)
        {
            return true
        }
        return false
        // z basa danych
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Czy na pewno chcesz wyjść?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ -> exitProcess(-1) }
            .setNegativeButton("Nie", null)
            .show()
    }
}