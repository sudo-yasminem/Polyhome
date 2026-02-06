
package com.yasm.polyhome


import android.os.Bundle
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
        setContentView(R.layout.register_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registerSuccess(responseCode: Int){
        if (responseCode == 200){
            println(responseCode)
            finish()
        }
    }

    private fun register(){

        val buttonRegister = findViewById<Button>(R.id.button3)

        val loginRegister = findViewById<EditText>(R.id.editTextlogin).text.toString()
        val passwordRegister = findViewById<EditText>(R.id.editTextpasswordLogin).text.toString()
        val dataRegister = RegisterData(loginRegister,passwordRegister)

        Api().post<RegisterData>("https://polyhome.lesmoulinsdudev.com/api/users/register", dataRegister,::registerSuccess)

    }
}