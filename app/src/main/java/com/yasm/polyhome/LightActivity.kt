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

class LightActivity : AppCompatActivity() {

    var listLights: List<PeriphData>? = null

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
        val tokenClean = tokenString?.trim()?.replace("\"", "")



        if (tokenString != null) {
            Log.d("TOKEN_PERIPH", "Token OK pour LightActivity")

            //Test pour détécter les erreurs API de listPeriph
            testAccess(tokenClean)
            listPeriph(tokenString)


        } else {
            Log.d("TOKEN_PERIPH", "Erreur token null")
        }


    }

    private fun listPeriph(tokenString: String) {

        val tokenClean = tokenString.trim().replace("\"", "").replace("\n", "").replace("\r", "")

        Log.d("API_PERIPH", "Appel listPeriph - token: $tokenClean")

        Api().get<PeriphResponseData>("https://polyhome.lesmoulinsdudev.com/api/houses/683/devices", ::listPeriphSuccess, tokenClean)
    }

    private fun listPeriphSuccess(responseCode: Int, responsePeriph: PeriphResponseData?) {

        val tag = "API_PERIPH"

        val tokenCleanToToggle = intent.getStringExtra("token")?.trim()?.replace("\"", "")

        if (responseCode == 200 && responsePeriph != null) {
            Log.d("API_PERIPH", "Retour listPeriphSuccess OK")

            val listePeripheriques = responsePeriph.devices
            val listLightsOnly = listePeripheriques.filter { it.type.contains("light") }

            this.listLights = listLightsOnly

            runOnUiThread {
                val listViewLights = findViewById<ListView>(R.id.listview_lights)

                //Creation de l'adapter
                val adapterLights = object : ArrayAdapter<PeriphData>(this, R.layout.item_light, listLightsOnly) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        
                        val row = convertView ?: layoutInflater.inflate(R.layout.item_light, parent, false)

                       
                        val device = getItem(position)

                        
                        val nameText = row.findViewById<TextView>(R.id.name_light)
                        val toggleSwitch = row.findViewById<Switch>(R.id.toggle_light)

                        if (device != null) {
                            nameText.text = device.id

                           
                            toggleSwitch.setOnCheckedChangeListener(null) // Sécurité
                            toggleSwitch.isChecked = (device.power == 1)

                            
                            toggleSwitch.setOnClickListener {
                               
                                toggleLight(device, tokenCleanToToggle)
                            }
                        }
                        return row
                    }
                }
               //On passe à la listView l'adapter
                listViewLights.adapter = adapterLights
            }


            //Check des valeurs sur Logcat uniquement
            listePeripheriques.forEach { periph ->

                Log.d(tag, "ID: ${periph.id}")
                Log.d(tag, "TYPE: ${periph.type}")
                if (periph.opening != null) {
                    Log.d(tag, "Status Ouverture: ${periph.opening}")
                }
                if (periph.power != null) {
                    Log.d(tag, "Power status : ${periph.availableCommands}")
                }
            }
        } else {
            Log.d("API_PERIPH", "Erreur listPeriphSuccess code: $responseCode")
        }


    }


    private fun testAccess(tokenClean: String?) {

        Api().get<List<UserDataResponse>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/683/users",
            { responseCode, users ->
                Log.d("TEST_ACCESS", "Réponse TestAccess code: $responseCode - Users: $users")
            },
            tokenClean
        )
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
                listPeriph(tokenStringUpdate)
            }
        } else {
            Log.e("API_COMMAND", "Erreur code: $responseCode")
        }
    }

    private fun toggleLight(periph: PeriphData, tokenClean: String?) {
        if (periph.type == "light") {
            if (periph.power == 0) {
                val commande = "TURN ON"
                sendCommand(periph.id, commande, tokenClean)
            } else {
                val commande = "TURN OFF"
                sendCommand(periph.id, commande, tokenClean)
            }
        }
    }



}
