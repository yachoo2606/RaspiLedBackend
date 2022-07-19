package com.example.projekt.network

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Path
import android.os.Build
import android.os.Environment
import android.os.Parcel
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.example.projekt.ResponsClasses.Reservation
import com.example.projekt.ResponsClasses.User
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.internal.wait
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Files.exists
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString

private const val PROTOCOL = "http"
private const val PROTOCOLSSL = "https"
private const val IP = "192.168.43.93"
//private const val IP = "192.168.1.212"
private const val PORT = 5000
private const val GETRESERVEDHOURS = "getReservedHours"
private const val REGISTERENDPOINT = "register"
private const val LOGINENDPOINT = "login"
private const val ADDRESERVATION = "addReservation"
private const val GETUSERSRESERVATIONS = "getUsersReservations"
private const val GETRESERVATIONSBYDATEHOUR = "getReservationsByDateHour"
private const val REMOVERESERVATION = "removeReservation"


private lateinit var client: OkHttpClient
private lateinit var applicationTemp:Application
private const val BUFFER_SIZE = 4096

class RaspiLedAPIService(application: Application) {

    val objectMaper : ObjectMapper = ObjectMapper()

    init {
        applicationTemp = application
        println("Client okHttp initialized")
        client = OkHttpClient.Builder().cache(
            Cache(
                directory = File(application.cacheDir,"http_cache"),
                maxSize = 50L * 1024L * 1024L
            ))
            .build()
    }

    fun login(login: String, password: String): User {

        var user: User = User()

        try {

            val url: HttpUrl = HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(IP)
                .port(PORT)
                .addPathSegment(LOGINENDPOINT)
                .addQueryParameter("login", login)
                .addQueryParameter("password", password)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(FormBody.Builder().build())
                .build()

            client.newCall(request).execute().use { respons->
                if(!respons.isSuccessful) throw IOException("Unexpected code $respons")

                user = objectMaper.readValue(respons.body!!.string(), User::class.java)
            }



        }catch (e:Exception){
            println(e.message)
        }
        return user
    }


    fun registerUser(
        nickname: String,
        name: String,
        phoneNumber: String,
        password: String): User {

        var user: User = User()

        try {

            val url: HttpUrl = HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(IP)
                .port(PORT)
                .addPathSegment(REGISTERENDPOINT)
                .addQueryParameter("login",nickname)
                .addQueryParameter("name",name)
                .addQueryParameter("phoneNumber",phoneNumber)
                .addQueryParameter("password",password)
                .addQueryParameter("role","0")
                .build()

            val request = Request.Builder()
                .url(url)
                .post(FormBody.Builder().build())
                .build()

            client.newCall(request).execute().use{ respons->
                if(!respons.isSuccessful) throw IOException("Unexpected code $respons")

                user = objectMaper.readValue(respons.body!!.string(), User::class.java)

            }
        }catch (e: Exception){
            println(e.message)
        }
        return user
    }


    fun getReservedHours(dzien:String):String{

        var godziny: String = ""

        try {

            val url: HttpUrl = HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(IP)
                .port(PORT)
                .addPathSegment(GETRESERVEDHOURS)
                .addQueryParameter("day", "${dzien[2]}${dzien[3]}")
                .addQueryParameter("month","${dzien[0]}${dzien[1]}")
                .addQueryParameter("year", "${dzien[4]}${dzien[5]}${dzien[6]}${dzien[7]}")
                .build()

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { respons->
                if(!respons.isSuccessful) throw IOException("Unexpected code $respons")
                godziny =  respons.body!!.string()
//                return godziny
            }

        }catch (e:Exception){
            println(e.message)
        }
        return godziny
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun addReservationText(
        userId: String,
        start_date_temp: String,
        content: String): Reservation {

        var reservation = Reservation()

        val start_date_string = "${start_date_temp[4]}${start_date_temp[5]}${start_date_temp[6]}${start_date_temp[7]}-${start_date_temp[0]}${start_date_temp[1]}-${start_date_temp[2]}${start_date_temp[3]} ${start_date_temp[8]}${start_date_temp[9]}:00:00"

        val start_date = LocalDateTime.parse(start_date_string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val end_date = start_date.plusHours(1).minusSeconds(1)

        val end_date_string = "${end_date.year}-${end_date.monthValue}-${end_date.dayOfMonth} ${end_date.hour}:${end_date.minute}:${end_date.second}"

//        println(start_date)
//        println(end_date)
//        println("${end_date.year}-${end_date.monthValue}-${end_date.dayOfMonth} ${end_date.hour}:${end_date.minute}:${end_date.second}")

        try {

            val url: HttpUrl = HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(IP)
                .port(PORT)
                .addPathSegment(ADDRESERVATION)
                .addQueryParameter("userId",userId)
                .addQueryParameter("start_date",start_date_string)
                .addQueryParameter("end_date",end_date_string)
                .addQueryParameter("content",content)
                .addQueryParameter("type","text")
                .build()

            val request = Request.Builder()
                .url(url)
                .post(FormBody.Builder().build())
                .build()

            client.newCall(request).execute().use{ respons->
                if(!respons.isSuccessful) throw IOException("Unexpected code $respons")

                reservation = objectMaper.readValue(respons.body!!.string(), Reservation::class.java)

            }
        }catch (e: Exception){
            println(e.message)
        }

        println(reservation)
        return reservation
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addReservationPhoto(
        userId: String,
        start_date_temp: String,
        photo: File
    ): Reservation {

        var reservation = Reservation()

        val start_date_string = "${start_date_temp[4]}${start_date_temp[5]}${start_date_temp[6]}${start_date_temp[7]}-${start_date_temp[0]}${start_date_temp[1]}-${start_date_temp[2]}${start_date_temp[3]} ${start_date_temp[8]}${start_date_temp[9]}:00:00"

        val start_date = LocalDateTime.parse(start_date_string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val end_date = start_date.plusHours(1).minusSeconds(1)

        val end_date_string = "${end_date.year}-${end_date.monthValue}-${end_date.dayOfMonth} ${end_date.hour}:${end_date.minute}:${end_date.second}"

//        println(start_date)
//        println(end_date)
//        println("${end_date.year}-${end_date.monthValue}-${end_date.dayOfMonth} ${end_date.hour}:${end_date.minute}:${end_date.second}")

        try {

            val MEDIA_TYPE_IMG = "image/*".toMediaTypeOrNull()

            val reqBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(
                    "imagefile",
                    "test.png",
                RequestBody.create(MEDIA_TYPE_IMG, photo))
                .build()

            val url: HttpUrl = HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(IP)
                .port(PORT)
                .addPathSegment(ADDRESERVATION)
                .addQueryParameter("userId",userId)
                .addQueryParameter("start_date",start_date_string)
                .addQueryParameter("end_date",end_date_string)
                .addQueryParameter("content","")
                .addQueryParameter("type","image")
                .build()

            val request = Request.Builder()
                .url(url)
                .post(reqBody)
                .build()

            client.newCall(request).execute().use{ respons->
                if(!respons.isSuccessful) throw IOException("Unexpected code $respons")

                reservation = objectMaper.readValue(respons.body!!.string(), Reservation::class.java)

            }
        }catch (e: Exception){
            println(e.message)
        }

        println(reservation)
        return reservation
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getUsersReservations(userId:String): java.nio.file.Path {
//        try {

            val url: HttpUrl = HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(IP)
                .port(PORT)
                .addPathSegment(GETUSERSRESERVATIONS)
                .addQueryParameter("userId",userId)
                .build()

            val request = Request.Builder()
                .url(url)
//                .post(FormBody.Builder().build())
                .build()

            client.newCall(request).execute().use{ respons->
                if(!respons.isSuccessful) throw IOException("Unexpected code $respons")

                val downloads = Path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
                val tempFile = File(downloads.pathString+"/Temp.zip").writeBytes(respons.body!!.bytes())
//                downloads.listDirectoryEntries()
//                                .forEach{
//                                    println(it)
//                                }

                val zipFileTemp = ZipFile(downloads.pathString+"/Temp.zip")

                val destDir =  File(downloads.pathString+"/Temp")
                if( !destDir.exists()){
                    destDir.mkdir()
                }

                zipFileTemp.use { zip ->
                    zip.entries().asSequence().forEach { zipEntry ->
                        zip.getInputStream(zipEntry).use { input ->
                            val filepath = destDir.toString() + File.separator +zipEntry.name
                            extractFile(input, filepath)
                        }
                    }
                }

                return Path(downloads.pathString+"/Temp")

            }
//        }catch (e: Exception){
//            println(e.message)
//        }
    }

    fun getHourReservation(day:String,month:String,year:String,hour:String): java.nio.file.Path? {
//        try {

        val url: HttpUrl = HttpUrl.Builder()
            .scheme(PROTOCOL)
            .host(IP)
            .port(PORT)
            .addPathSegment(GETRESERVATIONSBYDATEHOUR)
            .addQueryParameter("day",day)
            .addQueryParameter("month",month)
            .addQueryParameter("year",year)
            .addQueryParameter("hour",hour)
            .build()

        println(url.toString())

        val request = Request.Builder()
            .url(url)
//                .post(FormBody.Builder().build())
            .build()

        client.newCall(request).execute().use{ respons->
            if(!respons.isSuccessful) throw IOException("Unexpected code $respons")

            val downloads = Path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)


            if (respons.peekBody(1048576).string() == "Brak"){
                respons.close()
                return null
            }

            val tempFile = File(downloads.pathString+"/Temp.zip").writeBytes(respons.body!!.bytes())
//                downloads.listDirectoryEntries()
//                                .forEach{
//                                    println(it)
//                                }


            val zipFileTemp = ZipFile(downloads.pathString+"/Temp.zip")

            val destDir =  File(downloads.pathString+"/Temp")
            if( !destDir.exists()){
                destDir.mkdir()
            }

            zipFileTemp.use { zip ->
                zip.entries().asSequence().forEach { zipEntry ->
                    zip.getInputStream(zipEntry).use { input ->
                        val filepath = destDir.toString() + File.separator +zipEntry.name
                        extractFile(input, filepath)
                    }
                }
            }

            return Path(downloads.pathString+"/Temp")

        }
//        }catch (e: Exception){
//            println(e.message)
//        }
    }

    fun deleteReservation(temp:String):String{

        val dzientemp = temp.subSequence(2,4).toString()
        val miesiac = temp.subSequence(0,2).toString()
        val rok = temp.subSequence(4,8).toString()
        val godzinatemp = temp.subSequence(8,10).toString()

        val url: HttpUrl = HttpUrl.Builder()
            .scheme(PROTOCOL)
            .host(IP)
            .port(PORT)
            .addPathSegment(REMOVERESERVATION)
            .addQueryParameter("day",dzientemp)
            .addQueryParameter("month",miesiac)
            .addQueryParameter("year",rok)
            .addQueryParameter("hour",godzinatemp)
            .build()

        println(url.toString())

        val request = Request.Builder()
            .url(url)
            .post(FormBody.Builder().build())
            .build()

        client.newCall(request).execute().use { respons ->
            if (!respons.isSuccessful) throw IOException("Unexpected code $respons")
            println(respons.body!!.string())
        }
        return "coś"
    }

    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

}