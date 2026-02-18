package com.yasm.polyhome





import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class LightActivity: AppCompatActivity() {

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

        if(tokenString != null){
            Log.d("TOKEN_PERIPH", "Token OK pour LightActivity")

            //Test pour détécter les erreurs API de listPeriph
            testAccess(tokenString)
            listPeriph(tokenString)
            //ENSUITE LIST PERIPH

        }
        else{
            Log.d("TOKEN_PERIPH", "Erreur token null")
        }


    }

    private fun listPeriph(tokenString: String){

        val tokenClean = tokenString.trim().replace("\"","")
        Log.d("API_PERIPH", "Appel listPeriph - token: $tokenClean")

        Api().get<PeriphResponseData>("https://polyhome.lesmoulinsdudev.com/api/houses/683/devices", ::listPeriphSuccess, tokenClean)
    }

    private fun listPeriphSuccess(responseCode: Int, responsePeriph: PeriphResponseData?){

        val tag = "API_PERIPH"

        if(responseCode == 200 && responsePeriph != null){
            Log.d("API_PERIPH", "Retour listPeriphSuccess OK")

            val listePeripheriques = responsePeriph.devices

            listePeripheriques.forEach { periph ->

                Log.d(tag, "ID: ${periph.id}" )
                Log.d(tag, "TYPE: ${periph.type}")
                if(periph.opening != null){
                    Log.d(tag, "Status Ouverture: ${periph.opening}")
                }
                if (periph.power != null){
                    Log.d(tag,"Commandes : ${periph.availableCommands}")
                }
            }
        }
        else{
            Log.d("API_PERIPH", "Erreur listPeriphSuccess code: $responseCode")
        }


    }


    private fun testAccess(tokenString: String){

        val tokenTrim = tokenString.trim().replace("\"","")

        Log.d("TEST_ACCESS", "Header envoyé: $tokenString")

        Api().get<List<UserDataResponse>>("https://polyhome.lesmoulinsdudev.com/api/houses/683/users", { responseCode, users ->
           Log.d("TEST_ACCESS","Réponse TestAccess code: $responseCode - Users: $users")
        }, tokenTrim)
    }


}