package com.example.projekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Instrukcja : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instrukcja)

        val instrukcjaText: TextView = findViewById(R.id.instrukcjaText)
        val instrukcja =
            "informację o właścicielu aplikacji;\ninformację o prawach autorskich do aplikacji a także o licencji udzielanej do korzystania z aplikacji z określeniem chwili, w której licencja zostaje udzielona;\n"

        /*  zasady udostępniania treści, w tym ewentualne zabezpieczenia na wypadek niemożności korzystani z aplikacji w określonych sytuacjach, czy w związku z wystąpieniem nieprzewidzianych trudności;
          sposób korzystania z aplikacji, ewentualne koszty związane z korzystaniem z aplikacji i sposoby płatności;
       określenie wymogów sprzętowych dla aplikacji;
       sposób i terminy odstąpienia od umowy;
       odpowiedzialność usługodawcy;
       sposób kierowania i realizowania reklamacji, z uwzględnieniem do kogo, na jaki adres i w jakich terminach reklamacje należy kierować oraz terminach rozpatrzenia reklamacji;
      polityka prywatności, jest to obecnie niezmiernie istotna część regulaminu a dotyczy podmiotów uprawnionych do przetwarzania danych osobowych osób korzystających z aplikacji, zakresu ich przetwarzania, rodzaju i sposobu przechowywania tych danych.
             */ instrukcjaText.text=instrukcja
    }
}