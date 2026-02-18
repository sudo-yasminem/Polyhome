
package com.yasm.polyhome


import android.content.Intent
import android.hardware.lights.Light
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class ManageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.manage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val token = intent.getStringExtra("token")
        val loginSaisi = intent.getStringExtra("loginSaisi")

        val bouton_lumiere = findViewById<Button>(R.id.button_lights)
        val bouton_garage = findViewById<Button>(R.id.button_garages)

        bouton_lumiere.setOnClickListener {
            if (token != null) {
                listHouse(token)// token est un String
                //grantAccessHouse(token, loginSaisi)

            } else {
                Log.e("TOKEN_DEBUG", "Aucun token reçu")
            }
        }

        bouton_garage.setOnClickListener {
            if(token != null){
                val intentToGarage = Intent(this, GarageActivity::class.java)
                intentToGarage.putExtra("token", token)
                startActivity(intentToGarage)
            }
        }
    }

    private fun listHouseSuccess(responseCode: Int,maisons: List<HouseData>?){
        if(responseCode == 200 && maisons != null){
            Log.d("API_RESPONSE","Code reçu")

            maisons.forEach { house ->
                Log.d("API_RESPONSE", "ID Maison: ${house.houseId} | Owner: ${house.owner}")
            }

        }
        else{
            Log.e("API_ERROR", "Erreur cdoe $responseCode")
        }
    }

    private fun grantAccessSuccess(responseCode: Int){

        val tag = "API_ACCESS"

        if(responseCode == 200 || responseCode == 409){
            Log.d(tag,"Accès Accordé: $responseCode")

            val tokenString = intent.getStringExtra("token")
        }
        else{
            Log.d(tag,"Erreur code: $responseCode")
        }

    }

    private fun listHouse(tokenString: String){

        Api().get<List<HouseData>>("https://polyhome.lesmoulinsdudev.com/api/houses",{ responseCode, maisons ->
            val intentToPeriph = Intent(this, LightActivity::class.java)
            intentToPeriph.putExtra("token", tokenString)
            startActivity(intentToPeriph)

        },tokenString)

    }

    private fun grantAccessHouse(tokenString: String, login: String?){
        val dataAccess = DataAccess(userLogin = login)

        Api().post<DataAccess>("https://polyhome.lesmoulinsdudev.com/api/houses/683/users", dataAccess,  ::grantAccessSuccess, tokenString)
    }

    private fun deleteAccessHouse(tokenString: String, login: String?){
        val dataAccess = DataAccess(userLogin = login)

        Api().delete<DataAccess>("https://polyhome.lesmoulinsdudev.com/api/houses/683/users", dataAccess,  ::grantAccessSuccess, tokenString)
    }



}