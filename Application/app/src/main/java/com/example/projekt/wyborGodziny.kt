package com.example.projekt


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt.ResponsClasses.User
import com.example.projekt.network.RaspiLedAPIService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson

class wyborGodziny : AppCompatActivity() {
    lateinit var pom2: String

    val gson = Gson()
    val objectMaper : ObjectMapper = ObjectMapper()

    lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wybor_godziny)
        val sIntent = intent
        val dzien = sIntent.getStringExtra("Dzien")!!
        val login = sIntent.getStringExtra("Login")
        val sumaZamowien= sIntent.getStringExtra("sumaZamowien")
        val zatwierdz: Button = findViewById(R.id.wyborZatwierdz)
        // laczenie z baza danych do zajetych

        val raspiLedAPIService  = RaspiLedAPIService(application)

        user = gson.fromJson(sIntent.getStringExtra("User"), User::class.java)

        val zajete: String = raspiLedAPIService.getReservedHours(dzien)

        wylaczZajete(zajete)

        wybrane(sumaZamowien, dzien)

        zatwierdz.setOnClickListener {
            zatwierdzanie(sumaZamowien, dzien, login)
            finish()
        }
    }
    private fun zatwierdzanie(sumaZamowien: String?, dzien: String?, login:String?) {
            val godzina00: CheckBox = findViewById(R.id.godzina0)
            val godzina01: CheckBox = findViewById(R.id.godzina1)
            val godzina02: CheckBox = findViewById(R.id.godzina2)
            val godzina03: CheckBox = findViewById(R.id.godzina3)
            val godzina04: CheckBox = findViewById(R.id.godzina4)
            val godzina05: CheckBox = findViewById(R.id.godzina5)
            val godzina06: CheckBox = findViewById(R.id.godzina6)
            val godzina07: CheckBox = findViewById(R.id.godzina7)
            val godzina08: CheckBox = findViewById(R.id.godzina8)
            val godzina09: CheckBox = findViewById(R.id.godzina9)
            val godzina10: CheckBox = findViewById(R.id.godzina10)
            val godzina11: CheckBox = findViewById(R.id.godzina11)
            val godzina12: CheckBox = findViewById(R.id.godzina12)
            val godzina13: CheckBox = findViewById(R.id.godzina13)
            val godzina14: CheckBox = findViewById(R.id.godzina14)
            val godzina15: CheckBox = findViewById(R.id.godzina15)
            val godzina16: CheckBox = findViewById(R.id.godzina16)
            val godzina17: CheckBox = findViewById(R.id.godzina17)
            val godzina18: CheckBox = findViewById(R.id.godzina18)
            val godzina19: CheckBox = findViewById(R.id.godzina19)
            val godzina20: CheckBox = findViewById(R.id.godzina20)
            val godzina21: CheckBox = findViewById(R.id.godzina21)
            val godzina22: CheckBox = findViewById(R.id.godzina22)
            val godzina23: CheckBox = findViewById(R.id.godzina23)

            val ilosc = sumaZamowien!!.length
            var i = 0
            var sumaZamowienNowa: String = ""
            while (i < ilosc) {
                var j = 0
                var caloscData = ""
                while (j < 10) {
                    caloscData += sumaZamowien[i + j]
                    j += 1
                }
                if (caloscData.substring(0, 8) != dzien) {
                    sumaZamowienNowa += caloscData
                }
                i += 10
            }
            if (godzina00.isChecked) {
                sumaZamowienNowa += dzien + "00"
            }
            if (godzina01.isChecked == true) {
                sumaZamowienNowa += dzien + "01"
            }
            if (godzina02.isChecked == true) {
                sumaZamowienNowa += dzien + "02"
            }
            if (godzina03.isChecked == true) {
                sumaZamowienNowa += dzien + "03"
            }
            if (godzina04.isChecked == true) {
                sumaZamowienNowa += dzien + "04"
            }
            if (godzina05.isChecked == true) {
                sumaZamowienNowa += dzien + "05"
            }
            if (godzina06.isChecked == true) {
                sumaZamowienNowa += dzien + "06"
            }
            if (godzina07.isChecked == true) {
                sumaZamowienNowa += dzien + "07"
            }
            if (godzina08.isChecked == true) {
                sumaZamowienNowa += dzien + "08"
            }
            if (godzina09.isChecked == true) {
                sumaZamowienNowa += dzien + "09"
            }
            if (godzina10.isChecked == true) {
                sumaZamowienNowa += dzien + "10"
            }
            if (godzina11.isChecked == true) {
                sumaZamowienNowa += dzien + "11"
            }
            if (godzina12.isChecked == true) {
                sumaZamowienNowa += dzien + "12"
            }
            if (godzina13.isChecked == true) {
                sumaZamowienNowa += dzien + "13"
            }
            if (godzina14.isChecked == true) {
                sumaZamowienNowa += dzien + "14"
            }
            if (godzina15.isChecked == true) {
                sumaZamowienNowa += dzien + "15"
            }
            if (godzina16.isChecked == true) {
                sumaZamowienNowa += dzien + "16"
            }
            if (godzina17.isChecked == true) {
                sumaZamowienNowa += dzien + "17"
            }
            if (godzina18.isChecked == true) {
                sumaZamowienNowa += dzien + "18"
            }
            if (godzina19.isChecked == true) {
                sumaZamowienNowa += dzien + "19"
            }
            if (godzina20.isChecked == true) {
                sumaZamowienNowa += dzien + "20"
            }
            if (godzina21.isChecked == true) {
                sumaZamowienNowa += dzien + "21"
            }
            if (godzina22.isChecked == true) {
                sumaZamowienNowa += dzien + "22"
            }
            if (godzina23.isChecked == true) {
                sumaZamowienNowa += dzien + "23"
            }
            val intent: Intent = Intent(this, Panel::class.java)
            intent.putExtra("sumaZamowienNowa", sumaZamowienNowa)
            intent.putExtra("User", gson.toJson(user))
            intent.putExtra("Login", login)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            setResult(RESULT_OK, intent)
        }

    private fun wybrane(sumaZamowien: String?, dzien: String?) {
        val godzina00: CheckBox = findViewById(R.id.godzina0)
        val godzina01: CheckBox = findViewById(R.id.godzina1)
        val godzina02: CheckBox = findViewById(R.id.godzina2)
        val godzina03: CheckBox = findViewById(R.id.godzina3)
        val godzina04: CheckBox = findViewById(R.id.godzina4)
        val godzina05: CheckBox = findViewById(R.id.godzina5)
        val godzina06: CheckBox = findViewById(R.id.godzina6)
        val godzina07: CheckBox = findViewById(R.id.godzina7)
        val godzina08: CheckBox = findViewById(R.id.godzina8)
        val godzina09: CheckBox = findViewById(R.id.godzina9)
        val godzina10: CheckBox = findViewById(R.id.godzina10)
        val godzina11: CheckBox = findViewById(R.id.godzina11)
        val godzina12: CheckBox = findViewById(R.id.godzina12)
        val godzina13: CheckBox = findViewById(R.id.godzina13)
        val godzina14: CheckBox = findViewById(R.id.godzina14)
        val godzina15: CheckBox = findViewById(R.id.godzina15)
        val godzina16: CheckBox = findViewById(R.id.godzina16)
        val godzina17: CheckBox = findViewById(R.id.godzina17)
        val godzina18: CheckBox = findViewById(R.id.godzina18)
        val godzina19: CheckBox = findViewById(R.id.godzina19)
        val godzina20: CheckBox = findViewById(R.id.godzina20)
        val godzina21: CheckBox = findViewById(R.id.godzina21)
        val godzina22: CheckBox = findViewById(R.id.godzina22)
        val godzina23: CheckBox = findViewById(R.id.godzina23)

        var ilosc = sumaZamowien!!.length
        var i = 0
        var pom: String
        println(sumaZamowien)
            while (i < ilosc) {
                var j = 0
                var caloscData = ""
                while (j < 10) {
                    caloscData += sumaZamowien[i + j]
                    j += 1
                }
                var pomString=caloscData.substring(0,8)

                if ( pomString== dzien) {
                   pom = caloscData[8].toString() + caloscData[9].toString()
                    when (pom) {
                        "00" -> godzina00.isChecked = true
                        "01" -> godzina01.isChecked = true
                        "02" -> godzina02.isChecked = true
                        "03" -> godzina03.isChecked = true
                        "04" -> godzina04.isChecked = true
                        "05" -> godzina05.isChecked = true
                        "06" -> godzina06.isChecked = true
                        "07" -> godzina07.isChecked = true
                        "08" -> godzina08.isChecked = true
                        "09" -> godzina09.isChecked = true
                        "10" -> godzina10.isChecked = true
                        "11" -> godzina11.isChecked = true
                        "12" -> godzina12.isChecked = true
                        "13" -> godzina13.isChecked = true
                        "14" -> godzina14.isChecked = true
                        "15" -> godzina15.isChecked = true
                        "16" -> godzina16.isChecked = true
                        "17" -> godzina17.isChecked = true
                        "18" -> godzina18.isChecked = true
                        "19" -> godzina19.isChecked = true
                        "20" -> godzina20.isChecked = true
                        "21" -> godzina21.isChecked = true
                        "22" -> godzina22.isChecked = true
                        "23" -> godzina23.isChecked = true
                    }

                }
                i += 10
            }
        }

    private fun wylaczZajete(zajete: String) {
        val godzina00: CheckBox

 = findViewById(R.id.godzina0)
        val godzina01: CheckBox

 = findViewById(R.id.godzina1)
        val godzina02: CheckBox

 = findViewById(R.id.godzina2)
        val godzina03: CheckBox

 = findViewById(R.id.godzina3)
        val godzina04: CheckBox

 = findViewById(R.id.godzina4)
        val godzina05: CheckBox

 = findViewById(R.id.godzina5)
        val godzina06: CheckBox

 = findViewById(R.id.godzina6)
        val godzina07: CheckBox

 = findViewById(R.id.godzina7)
        val godzina08: CheckBox

 = findViewById(R.id.godzina8)
        val godzina09: CheckBox

 = findViewById(R.id.godzina9)
        val godzina10: CheckBox

 = findViewById(R.id.godzina10)
        val godzina11: CheckBox

 = findViewById(R.id.godzina11)
        val godzina12: CheckBox

 = findViewById(R.id.godzina12)
        val godzina13: CheckBox

 = findViewById(R.id.godzina13)
        val godzina14: CheckBox

 = findViewById(R.id.godzina14)
        val godzina15: CheckBox

 = findViewById(R.id.godzina15)
        val godzina16: CheckBox

 = findViewById(R.id.godzina16)
        val godzina17: CheckBox

 = findViewById(R.id.godzina17)
        val godzina18: CheckBox

 = findViewById(R.id.godzina18)
        val godzina19: CheckBox

 = findViewById(R.id.godzina19)
        val godzina20: CheckBox

 = findViewById(R.id.godzina20)
        val godzina21: CheckBox

 = findViewById(R.id.godzina21)
        val godzina22: CheckBox

 = findViewById(R.id.godzina22)
        val godzina23: CheckBox

 = findViewById(R.id.godzina23)

        val ilosc = zajete.length

        var i = 0
        while (i < ilosc) {
            val godz: String = (zajete[i]).toString()+(zajete[i+1]).toString()
            when(godz)
            {
                "00"-> godzina00.isEnabled=false
                "01"-> godzina01.isEnabled=false
                "02"-> godzina02.isEnabled=false
                "03"-> godzina03.isEnabled=false
                "04"-> godzina04.isEnabled=false
                "05"-> godzina05.isEnabled=false
                "06"-> godzina06.isEnabled=false
                "07"-> godzina07.isEnabled=false
                "08"-> godzina08.isEnabled=false
                "09"-> godzina09.isEnabled=false
                "10"-> godzina10.isEnabled=false
                "11"-> godzina11.isEnabled=false
                "12"-> godzina12.isEnabled=false
                "13"-> godzina13.isEnabled=false
                "14"-> godzina14.isEnabled=false
                "15"-> godzina15.isEnabled=false
                "16"-> godzina16.isEnabled=false
                "17"-> godzina17.isEnabled=false
                "18"-> godzina18.isEnabled=false
                "19"-> godzina19.isEnabled=false
                "20"-> godzina20.isEnabled=false
                "21"-> godzina21.isEnabled=false
                "22"-> godzina22.isEnabled=false
                "23"-> godzina23.isEnabled=false
            }
            i += 2
        }
    }
}
