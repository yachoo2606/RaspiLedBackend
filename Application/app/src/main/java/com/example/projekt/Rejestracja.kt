package com.example.projekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.projekt.ResponsClasses.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.example.projekt.network.RaspiLedAPIService


class Rejestracja : AppCompatActivity() {

    private lateinit var raspiLedAPIService: RaspiLedAPIService
    private lateinit var user: User
    val objectMaper : ObjectMapper = ObjectMapper()
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rejestracja)

        raspiLedAPIService = RaspiLedAPIService(application)

        val regulaminPrzycisk2: Button = findViewById(R.id.regulaminStrona)
        val rejestracja: Button = findViewById(R.id.rejestracjaDoSerwera)

        regulaminPrzycisk2.setOnClickListener{
            val intent: Intent = Intent(this, Regulamin::class.java)
            intent.putExtra("Login", " ")
            startActivity(intent)
        }

        rejestracja.setOnClickListener{
            rejestruj()
        }
    }

    private fun rejestruj() {
        val loginRejestracja = findViewById<EditText>(R.id.loginRejestracja)
        val nameRejestracja = findViewById<EditText>(R.id.nameRejestracja)
        val hasloRejestracja = findViewById<EditText>(R.id.hasloRejestracja)
        val phoneNumberRejestracja = findViewById<EditText>(R.id.phoneNumberRejestracja)
        val checkBoxRejestracja = findViewById<CheckBox>(R.id.akceptacjaRegulaminu)
        if(checkBoxRejestracja.isChecked==true)
        {
//            if(loginRejestracja.text.toString().length>3 && loginRejestracja.text.toString().length<13) {
//                if (emailRejestracja.text.toString().length > 3 && loginRejestracja.text.toString().length < 13 && isEmailValid(emailRejestracja.text.toString())) {
//                    if (hasloRejestracja.text.toString().length > 3 && hasloRejestracja.text.toString().length < 13) {
//                        if (sprawdzdane() == true) {
//                            val intent: Intent = Intent(this, Panel::class.java)
//                            intent.putExtra("Login", loginRejestracja.text.toString())
//                            startActivity(intent)
//                        } else {
//                            Snackbar.make(
//                                findViewById(R.id.rejestracjaLayout),
//                                "Taki użytkownik istnieje",
//                                Snackbar.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        Snackbar.make(
//                            findViewById(R.id.rejestracjaLayout),
//                            "Źle wpisane dane",
//                            Snackbar.LENGTH_SHORT
//                        ).show()
//                    }
//                } else {
//                    Snackbar.make(
//                        findViewById(R.id.rejestracjaLayout),
//                        "Źle wpisane dane",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                }
//            }else {
//                Snackbar.make(
//                    findViewById(R.id.rejestracjaLayout),
//                    "Źle wpisane dane",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
            user = objectMaper.readValue(
                gson.toJson(
                    raspiLedAPIService.registerUser(
                        loginRejestracja.text.toString(),
                        nameRejestracja.text.toString(),
                        phoneNumberRejestracja.text.toString(),
                        hasloRejestracja.text.toString()
                    )
                )
                ,User::class.java)

            Snackbar.make(
                findViewById(R.id.rejestracjaLayout),
                "${user.login} ${user.name} ${user.phoneNumber} USER Registerd",
                Snackbar.LENGTH_SHORT
            ).show()

        }
        else
        {
            Snackbar.make(findViewById(R.id.rejestracjaLayout),"Źle wpisane dane",Snackbar.LENGTH_SHORT).show()
        }
    }
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sprawdzdane(): Any {
        return true
        // sprawdzenie z baza danych i tworzenie jezeli mozna jest
    }
}