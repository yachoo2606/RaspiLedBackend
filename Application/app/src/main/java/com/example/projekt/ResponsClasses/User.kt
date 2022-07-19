package com.example.projekt.ResponsClasses

import okhttp3.internal.userAgent

class User {
    val id: Int? = null
    val login: String? = null
    val name: String? = null
    val phoneNumber: String? = null
    val password: String? = null
    val role: Boolean? = null

    fun printUser(): String{
        return "<User(id=${this.id}, name=${this.name}, login=${this.login}, phoneNumber=${this.phoneNumber}, role=${this.role})>"
    }

}