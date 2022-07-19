package com.example.projekt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projekt.ResponsClasses.User
import com.example.projekt.network.RaspiLedAPIService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import kotlin.io.path.*

class MojeZakupy : AppCompatActivity() {

    lateinit var raspiLedAPIService: RaspiLedAPIService
    lateinit var user: User
    val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moje_zakupy)

        raspiLedAPIService = RaspiLedAPIService(application)

        val sIntent = intent

        user = gson.fromJson(sIntent.getStringExtra("User"), User::class.java)


        val login = sIntent.getStringExtra("Login")
        val loginText = findViewById<TextView>(R.id.loginText)
        loginText.text=login
        wypiszzakupy(login)



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun wypiszzakupy(login: String?) {
        val mainContainer = findViewById<ConstraintLayout>(R.id.mojeZakupyLayout)
        val loginName = findViewById<TextView>(R.id.loginText)
        val filesPaths = raspiLedAPIService.getUsersReservations(user.id.toString()).listDirectoryEntries()
        val downloads = Path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        loginName.text = "${user.login} ${user.name}"
        var i=0
        filesPaths.forEach{ it ->
            i++
            val mimeType = Files.probeContentType(it)
            if(mimeType != null && mimeType.split("/")[0].equals("image")){
                val myImage = ImageView(this)
//                println(downloads.toString()+"/Temp/"+it.fileName.name)

                myImage.x += 100*i

                myImage.setImageBitmap(
                    Bitmap.createScaledBitmap(
                        BitmapFactory.decodeFile(downloads.toString()+"/Temp/"+it.fileName.name),
                        100,
                        100,
                        true)
                )
                mainContainer.addView(myImage)
            }else{
                val file = File(downloads.toString()+"/Temp/"+it.fileName.name)
                BufferedReader(FileReader(file)).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        val myText = TextView(this)
                        myText.text = line
                        myText.y = myText.y + 100
                        myText.x += 100*i
                        mainContainer.addView(myText)
                    }
                }
            }
        }

        Path(downloads.toString()+"/Temp/").listDirectoryEntries().forEach{ it ->
            println(it.toUri())
            File(it.toUri()).delete()
        }
        Path(downloads.toString()+"/Temp/").deleteIfExists()
    }




}