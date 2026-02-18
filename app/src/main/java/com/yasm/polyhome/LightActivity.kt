package com.yasm.polyhome





import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class LightActivity: AppCompatActivity() {

    var listPeripheriquesTotal: List<PeriphData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_light)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.light)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val tokenString = intent.getStringExtra("token")
        val tokenClean = tokenString?.trim()?.replace("\"","")



        if(tokenString != null){
            Log.d("TOKEN_PERIPH", "Token OK pour LightActivity")

            //Test pour détécter les erreurs API de listPeriph
            testAccess(tokenClean)
            listPeriph(tokenString)

            val bouton_onoff = findViewById<Button>(R.id.bouton_light_on1)
            bouton_onoff.setOnClickListener {

                val light1 = listPeripheriquesTotal?.find {
                    it.type.contains("light")
                }
                if (light1 != null){
                    toggleLight(light1, tokenClean)
                }
                else{
                    Log.d("DEBUG_TOGGLE","Erreur Liste vide")
                }


            }

        }
        else{
            Log.d("TOKEN_PERIPH", "Erreur token null")
        }


    }

    private fun listPeriph(tokenString: String) {

        val tokenClean = tokenString.trim().replace("\"", "")?.replace("\n", "")

        Log.d("API_PERIPH", "Appel listPeriph - token: $tokenClean")

        Api().get<PeriphResponseData>("https://polyhome.lesmoulinsdudev.com/api/houses/683/devices", ::listPeriphSuccess, tokenClean)
    }

    private fun listPeriphSuccess(responseCode: Int, responsePeriph: PeriphResponseData?){

        val tag = "API_PERIPH"

        if(responseCode == 200 && responsePeriph != null){
            Log.d("API_PERIPH", "Retour listPeriphSuccess OK")

            val listePeripheriques = responsePeriph.devices
            this.listPeripheriquesTotal = listePeripheriques

            listePeripheriques.forEach { periph ->

                Log.d(tag, "ID: ${periph.id}" )
                Log.d(tag, "TYPE: ${periph.type}")
                if(periph.opening != null){
                    Log.d(tag, "Status Ouverture: ${periph.opening}")
                }
                if (periph.power != null){
                    Log.d(tag,"Power status : ${periph.availableCommands}")
                }
            }
        }
        else{
            Log.d("API_PERIPH", "Erreur listPeriphSuccess code: $responseCode")
        }


    }


    private fun testAccess(tokenClean: String?) {

        Api().get<List<UserDataResponse>>("https://polyhome.lesmoulinsdudev.com/api/houses/683/users", { responseCode, users ->
           Log.d("TEST_ACCESS","Réponse TestAccess code: $responseCode - Users: $users")
        }, tokenClean)
    }

    private fun sendCommand(periphId: String, commandString: String, tokenClean: String?){

        val houseId = "683" //A changer, récup propre d'un intent et pas hardcodé
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$periphId/command"

        val requestData= CommandeData(commandString)
        Log.d("API_COMMAND", "Commande envoyée: $commandString au periph: $periphId")

        Api().post<CommandeData, Any>(url, requestData, ::sendCommandSuccess, tokenClean)
    }

    private fun sendCommandSuccess(responseCode: Int, responseCommand: Any?){
        if(responseCode == 200){
            Log.d("API_COMMAND", "Commande bien exécutée")
            val tokenStringUpdate = intent.getStringExtra("token")
            if(tokenStringUpdate != null){
                listPeriph(tokenStringUpdate)
            }
        }
        else{
            Log.e("API_COMMAND", "Erreur code: $responseCode")
        }
    }

    private fun toggleLight(periph: PeriphData, tokenClean: String?){
        if(periph.type == "light"){
            if(periph.power == 0){
                val commande = "TURN ON"
                sendCommand(periph.id, commande, tokenClean)
            }
            else{
                val commande = "TURN OFF"
                sendCommand(periph.id, commande, tokenClean)
            }
        }
    }



}