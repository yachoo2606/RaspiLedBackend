package com.example.projekt

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt.ResponsClasses.User
import com.example.projekt.network.RaspiLedAPIService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.util.*

class Kupowanie : AppCompatActivity() {

    lateinit var user: User
    lateinit var photo: Bitmap

    val objectMaper : ObjectMapper = ObjectMapper()
    val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kupowanie)

        val sIntent = intent

        user = gson.fromJson(sIntent.getStringExtra("User"),User::class.java)


        val raspiLedAPIService = RaspiLedAPIService(application)



        var sumaZamowien = sIntent.getStringExtra("sumaZamowien")
        val tekstObraz = findViewById<Switch>(R.id.przelacznikTekstObraz)
        val obrazPrzycisk = findViewById<Button>(R.id.obrazWybor)
        val tekstDoWyswietlenia = findViewById<EditText>(R.id.tekstDoWyswietlenia)
        val kupButton = findViewById<Button>(R.id.kupowanieButton)

        wyswietlZamowienie(sumaZamowien)
        obrazPrzycisk.isEnabled=false
        tekstObraz.setOnClickListener{
            if(tekstObraz.isChecked)
            {
                tekstDoWyswietlenia.isEnabled=false
                obrazPrzycisk.isEnabled=true
            }
            else if(!tekstObraz.isChecked)
        {
            obrazPrzycisk.isEnabled=false
            tekstDoWyswietlenia.isEnabled=true
        }

        }

        kupButton.setOnClickListener{
            if(tekstObraz.isChecked)
            {
                var i=0

                val file_path = Environment.getExternalStorageDirectory().absolutePath +
                        "/Temp"
                val dir = File(file_path)
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "sketchpadTEMP.png")
                val fOut = FileOutputStream(file)

                photo.compress(Bitmap.CompressFormat.PNG, 85, fOut)

                while(i<sumaZamowien!!.length){

                    raspiLedAPIService.addReservationPhoto(
                        user.id.toString(),
                        sumaZamowien!!.substring(i,i+10),
                        file
                    )
                    i+=10
                }
                fOut.flush()
                fOut.close()
                val intent: Intent = Intent(this, Panel::class.java)
                intent.putExtra("User", gson.toJson(user))
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                setResult(RESULT_OK, intent)
                finish()
            }
            else
            {
                var i=0

                while(i<sumaZamowien!!.length){

                    raspiLedAPIService.addReservationText(
                        user.id.toString(),
                        sumaZamowien!!.substring(i,i+10),
                        tekstDoWyswietlenia.text.toString())
                    i+=10
                }
                sumaZamowien=""

                intent.putExtra("User", gson.toJson(user))
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        obrazPrzycisk.setOnClickListener {
            openGalleryForImage()
        }

    }

    private fun wyswietlZamowienie(sumaZamowien: String?) {
        val wyswietlText = findViewById<TextView>(R.id.listaZakupow)
        var tekstWyswietlany: String = ""
        var i = 0
        var ilosc= sumaZamowien!!.length
        while(i<ilosc)
        {
            tekstWyswietlany+="${sumaZamowien[i+2]}${sumaZamowien[i+3]}/${sumaZamowien[i+0]}${sumaZamowien[i+1]}/${sumaZamowien[i+4]}${sumaZamowien[i+5]}${sumaZamowien[i+6]}${sumaZamowien[i+7]}:${sumaZamowien[i+8]}${sumaZamowien[i+9]}\n"
            i+=10
        }
        wyswietlText.text=tekstWyswietlany
    }

    private fun openGalleryForImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==200 && resultCode== RESULT_OK){

            val matrix = Matrix()
//            matrix.postRotate(90F)

            photo= Bitmap.createScaledBitmap(
                MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data),
                16,
                16,
                true)
            photo = Bitmap.createBitmap(
                photo,
                0,
                0,
                photo.getWidth(),
                photo.getHeight(),
                matrix,
                true
            )
        }
    }

}