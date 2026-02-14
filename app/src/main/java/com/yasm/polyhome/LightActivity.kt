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
            //Lister les périph après la vérification du token
        }


    }

    private fun listPeriphSuccess(responseCode: Int, responsePeriph: PeriphResponseData?){

        val tag = "API_PERIPH"

        if(responseCode == 200 && responsePeriph != null){
            Log.d(tag, "Code PeriphSucces: $responseCode")

            val listePeripheriques = responsePeriph.peripheriques

            listePeripheriques.forEach { periph ->
                Log.d(tag, "Id: ${periph.id} / Type: ${periph.type} / Commandes: ${periph.availableCommands} / Openeing: ${periph.opening}")

            }
        }
        else{
            Log.d(tag, "Erreur Code: $responseCode")
        }
    }

    private fun listPeriph(tokenString: String){

        Api().get<PeriphResponseData>("https://polyhome.lesmoulinsdudev.com/api/houses/683/devices", ::listPeriphSuccess, tokenString)
    }
    //fonction Recup les periph de la maison


}