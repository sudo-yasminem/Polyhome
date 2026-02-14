
package com.yasm.polyhome


import android.content.Intent
import android.os.Bundle
import android.util.Log
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



        if(token != null){
            listHouse(token)// token est un String
            grantAccessHouse(token, loginSaisi)



        }
        else{
            Log.e("TOKEN_DEBUG", "Aucun token reçu")
        }

    }

    private fun listHouseSuccess(responseCode: Int,maisons: List<HouseData>?){
        if(responseCode == 200 && maisons != null){
            Log.d("API_RESPONSE","Code reçu")

            maisons.forEach{
                    house -> Log.d("API_RESPONSE","ID Maison: ${house.houseId} | Owner: ${house.owner}")
            }

            //listPeriph(tokenString) //Marche pas comme ça, il faut l'appeler ailleurs

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

            val intent_toPeriph = Intent(this, LightActivity::class.java)
            intent_toPeriph.putExtra("token", tokenString)
            startActivity(intent_toPeriph)

            //finish ? à tester
        }
        else{
            Log.d(tag,"Erreur code: $responseCode")
        }

    }

    private fun listHouse(tokenString: String){

        Api().get<List<HouseData>>("https://polyhome.lesmoulinsdudev.com/api/houses",::listHouseSuccess,
            tokenString)

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