package com.example.projekt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projekt.ResponsClasses.User
import com.example.projekt.network.RaspiLedAPIService
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okio.ByteString.Companion.encode
import okio.ByteString.Companion.encodeUtf8
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.*

class AdminWidok : AppCompatActivity() {
    var dzien: String =""
    var godzina=0

    lateinit var raspiLedAPIService: RaspiLedAPIService
    lateinit var user: User
    val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_widok)

        val sIntent = intent

        raspiLedAPIService = RaspiLedAPIService(application)

        user = gson.fromJson(sIntent.getStringExtra("User"), User::class.java)



        dzien = sIntent.getStringExtra("DzienAdmin").toString()

        println("dzien: ${dzien}")

        val nastepnyPrzycisk: Button = findViewById(R.id.przyciskNastepny)
        val poprzedniPrzycisk: Button = findViewById(R.id.przyciskPoprzedni)
        val usunPrzycisk: Button = findViewById(R.id.przyciskUsun)
        val akceptacjaPrzycisk: Button = findViewById(R.id.przyciskAkceptuj)
        val dataText = findViewById<TextView>(R.id.dataAdmin)
        wyswietl()
        usunPrzycisk.setOnClickListener{
            var g=""
            if(godzina<10)
            {
                g="0"+godzina.toString()
            }
            else
            {
                g=godzina.toString()
            }
            usun(dzien+g)
        }
        akceptacjaPrzycisk.setOnClickListener{

        }
        nastepnyPrzycisk.setOnClickListener{
            val nastepnyPrzycisk: Button = findViewById(R.id.przyciskNastepny)
            if(godzina<23)
            {
                godzina+=1
                wyswietl()
            }
            else
            {
                nastepnyPrzycisk.isEnabled=false
                Snackbar.make(findViewById(R.id.AdminWidokLayout),"Koniec dnia!!!", Snackbar.LENGTH_SHORT).show()
            }
        }
        poprzedniPrzycisk.setOnClickListener{
            val poprzedniPrzycisk: Button = findViewById(R.id.przyciskPoprzedni)
            if(godzina>0)
            {
                godzina-=1
                wyswietl()
            }
            else
            {
                poprzedniPrzycisk.isEnabled=false
                Snackbar.make(findViewById(R.id.AdminWidokLayout),"Koniec dnia!!!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun usun(s: String) {
        raspiLedAPIService.deleteReservation(s)
        wyswietl()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun wyswietl()
    {
        val nastepnyPrzycisk: Button = findViewById(R.id.przyciskNastepny)
        val poprzedniPrzycisk: Button = findViewById(R.id.przyciskPoprzedni)
        val dataText = findViewById<TextView>(R.id.dataAdmin)
        val kupionytext = findViewById<TextView>(R.id.kupionyText)
        val kupionyObrazek= findViewById<ImageView>(R.id.kupionyObrazek)

        var g=""

        kupionyObrazek.setImageDrawable(getDrawable(android.R.drawable.ic_menu_close_clear_cancel))
        kupionytext.text =""

        if(godzina<10)
        {
            g="0"+godzina.toString()
        }
        else
        {
            g=godzina.toString()
        }
        var dzienDoWypisania= dzien[2].toString()+dzien[3].toString()+"/"+dzien[0].toString()+dzien[1].toString()+"/"+dzien[4].toString()+dzien[5].toString()+dzien[6].toString()+dzien[7].toString()+" : "+g
        dataText.text=dzienDoWypisania

        czyJest(dzien+g)

        nastepnyPrzycisk.isEnabled=true
        poprzedniPrzycisk.isEnabled=true

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun czyJest(s: String): Boolean {
        wypiszzakupy(s)
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun wypiszzakupy(temp:String) {
        val dzientemp = temp.subSequence(2,4).toString()
        val miesiac = temp.subSequence(0,2).toString()
        val rok = temp.subSequence(4,8).toString()
        val godzinatemp = temp.subSequence(8,10).toString()
        val myImage = findViewById<ImageView>(R.id.kupionyObrazek)
        val autorText = findViewById<TextView>(R.id.Autor)
        val usunPrzycisk: Button = findViewById(R.id.przyciskUsun)
        val akceptacjaPrzycisk: Button = findViewById(R.id.przyciskAkceptuj)
        val kupionyText= findViewById<TextView>(R.id.kupionyText)


        println(temp)
        println("${dzientemp}  ${miesiac}  ${rok}  ${godzinatemp}")
        val filesPaths = raspiLedAPIService.getHourReservation(dzientemp, miesiac,rok,godzinatemp)?.listDirectoryEntries()
        println(filesPaths)
        val downloads = Path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)

        autorText.text="Ta godzina nie została kupiona"
        usunPrzycisk.isEnabled=false
        akceptacjaPrzycisk.isEnabled=false

        var i=0
        filesPaths?.forEach{ it ->
            i++
            println(it)

            val mimeType = Files.probeContentType(it)
            if(mimeType != null && mimeType.split("/")[0].equals("image")){
                println(downloads.toString()+"/Temp/"+it.fileName.name)
                usunPrzycisk.isEnabled=true
                akceptacjaPrzycisk.isEnabled=true
                myImage.setImageBitmap(
                    Bitmap.createScaledBitmap(
                        BitmapFactory.decodeFile(downloads.toString()+"/Temp/"+it.fileName.name),
                        100,
                        100,
                        true)
                )
            }else if(it.fileName.name =="author.txt"){
                val file = File(downloads.toString()+"/Temp/"+it.fileName.name)
                BufferedReader(FileReader(file)).use { br ->
                    var line: String?
                    var lineInt = 0
                    while (br.readLine().also { line = it } != null) {
                        if (lineInt==1){
                            autorText.text = line
                        }
                        if (lineInt == 2){
                            autorText.text = (autorText.text.toString() + " "+ line)
                        }
                        lineInt++
                    }
                }
            }else{
                val file = File(downloads.toString()+"/Temp/"+it.fileName.name)
                usunPrzycisk.isEnabled=true
                akceptacjaPrzycisk.isEnabled=true
                BufferedReader(FileReader(file)).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        kupionyText.text = line
                    }
                }
            }
        }
        if(Path(downloads.toString()+"/Temp/").exists()){
            Path(downloads.toString()+"/Temp/").listDirectoryEntries().forEach{ it ->
                println(it.toUri())
                File(it.toUri()).delete()
            }
            Path(downloads.toString()+"/Temp/").deleteIfExists()
        }
        Path(downloads.toString()+"Temp.zip").deleteIfExists()

    }

}