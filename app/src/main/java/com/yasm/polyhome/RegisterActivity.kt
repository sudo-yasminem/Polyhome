
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

class RegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        register()
    }

    private fun goToLogin(){
        val button_login = findViewById<Button>(R.id.button_creationcompte)

        button_login?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun goToManage(){
        val button_manage = findViewById<Button>(R.id.button_connexioncompte)

        button_manage?.setOnClickListener {
            val intent = Intent(this, ManageActivity::class.java)
            startActivity(intent)
        }

    }



    private fun registerSuccess(responseCode: Int){
        if (responseCode == 200){
            println(responseCode)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(){

        val buttonRegister = findViewById<Button>(R.id.button_creationcompte)

        buttonRegister?.setOnClickListener {

            val loginRegister = findViewById<EditText>(R.id.id_creation).text.toString()
            val passwordRegister = findViewById<EditText>(R.id.mdp_creation).text.toString()
            val dataRegister = RegisterData(loginRegister, passwordRegister)

            Log.d("API_DATA", "Login récupéré: '$loginRegister'")
            Log.d("API_DATA", "Password récupéré: '$passwordRegister'")

            Api().post<RegisterData>(
                "https://polyhome.lesmoulinsdudev.com/api/users/register",
                dataRegister,
                ::registerSuccess
            )
        }
    }
}