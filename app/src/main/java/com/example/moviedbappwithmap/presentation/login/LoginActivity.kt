package com.example.moviedbappwithmap.presentation.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.moviedbappwithmap.R
import com.example.moviedbappwithmap.presentation.MainActivity
import com.example.moviedbappwithmap.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        buttonLogin.setOnClickListener{
            loginViewModel.login(
                editTextUsername.text.toString(),
                editTextPassword.text.toString()
            )
        }
        setData()
    }
    private fun setData() {
        loginViewModel.liveData.observe(this, Observer { state ->
            when(state) {
                is LoginViewModel.State.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LoginViewModel.State.HideLoading -> {
                    progressBar.visibility = View.INVISIBLE
                }
                is LoginViewModel.State.ApiResult -> {
                    if (state.success && state.session_id != "") {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        AppPreferences.setAccountId(this, state.account_id)
                        AppPreferences.setSessionId(this, state.session_id)
                        Log.d("MovieDb: sessionId", state.session_id)
                        Log.d("MovieDb: accountId", state.account_id.toString())
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid username, password credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}