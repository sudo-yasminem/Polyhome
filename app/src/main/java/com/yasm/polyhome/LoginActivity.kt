package com.yasm.polyhome


import android.content.Intent
import android.media.MediaCodec
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api
import android.util.Log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        login()

    }

    private fun loginSuccess(responseCode: Int, token_object: LoginDataResult?){
        if(responseCode == 200){
            Log.d("API_DEBUG", "Response code $responseCode")
            intent = Intent(this, ManageActivity::class.java)
            intent.putExtra("token", token_object?.token)

            val intentLogin = Intent(this, ManageActivity::class.java)
           //intentLogin.putExtra("loginSaisi",)
            startActivity(intent)

            startActivity(intent)
        }

    }

    private fun handleLoginSuccess(login: String): (Int, LoginDataResult?) -> Unit {
        return { responseCode, loginResult ->
            if(responseCode==200 && loginResult != null){
                val intent = Intent(this, ManageActivity::class.java)
                intent.putExtra("token", loginResult.token)
                intent.putExtra("loginSaisi", login)
                startActivity(intent)
            }
            else{
                Log.e("ERREUR_API", "Response Code: $responseCode")
            }

        }
    }

    private fun login(){
        val buttonManage = findViewById<Button>(R.id.button_connexion)

        buttonManage?.setOnClickListener {
            val login = findViewById<EditText>(R.id.id_connexion).text.toString()
            val loginPassword = findViewById<EditText>(R.id.mdp_connexion).text.toString()

            val loginData = LoginData(login, loginPassword)



            Api().post<LoginData, LoginDataResult>("https://polyhome.lesmoulinsdudev.com/api/users/auth", loginData, handleLoginSuccess(login))

        }

    }

    private fun goToRegister(){
        val button_register = findViewById<Button>(R.id.button_creercompte)

        button_register?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }







}