package com.yasm.polyhome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api
import java.text.FieldPosition

class GarageActivity : AppCompatActivity() {


    private var listGarage: List<PeriphData>? = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)


        val tokenClean = intent.getStringExtra("token")?.replace("\"", "")

        if(tokenClean != null){
            listPeriph(tokenClean)

        }
    }

    private fun listPeriph(tokenString: String) {

        val tokenClean = tokenString.trim().replace("\"", "")

        Api().get<PeriphResponseData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/683/devices",
            ::listPeriphSuccess,
            tokenString)


    }

    private fun listPeriphSuccess(responseCode: Int, responsePeriph: PeriphResponseData?) {
        if (responseCode == 200 && responsePeriph != null) {

            val garage = responsePeriph.devices.find { it.type.contains("garage door") }
            val toggleGarage = findViewById<Switch>(R.id.toggle_portegarage)
            val tokenCleanToToggle = intent.getStringExtra("token")?.trim()?.replace("\"", "")

            //Pour que toggleGarageDoor accepte bien garage en optionnel
            garage?.let { garage ->
                toggleGarage.setOnClickListener {
                    toggleGarageDoor(garage, tokenCleanToToggle)
                }
            }

        }
    }

    private fun sendCommand(periphId: String, commandString: String, tokenClean: String?) {

        val houseId = "683" //A changer, récup propre d'un intent et pas hardcodé
        val url =
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$periphId/command"

        val requestData = CommandeData(commandString)
        Log.d("API_COMMAND", "Commande envoyée: $commandString au periph: $periphId")

        Api().post<CommandeData, Any>(url, requestData, ::sendCommandSuccess, tokenClean)
    }

    private fun sendCommandSuccess(responseCode: Int, responseCommand: Any?) {
        if (responseCode == 200) {
            Log.d("API_COMMAND", "Commande bien exécutée")
            val tokenStringUpdate = intent.getStringExtra("token")
            if (tokenStringUpdate != null) {
                listPeriph(tokenStringUpdate) // Update des états des périphériques à chaque commande
            }
        } else {
            Log.e("API_COMMAND", "Erreur code: $responseCode")
        }
    }


    private fun toggleGarageDoor(periph: PeriphData, tokenClean: String?) {
        if (periph.type == "garage door") {
            if (periph.opening == 0) {
                val commande = "OPEN"
                sendCommand(periph.id, commande, tokenClean)
            }
            else if(periph.opening == 1) {
                val commande = "CLOSE"
                sendCommand(periph.id, commande, tokenClean)
            }
            else{
                val commande = "STOP"
                sendCommand(periph.id, commande,tokenClean)
            }
        }
    }


}
