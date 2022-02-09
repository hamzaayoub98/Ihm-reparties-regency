package com.example.ihm_reparties

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity  : AppCompatActivity()
{
    private lateinit var restAddressEditText: EditText
    private lateinit var restPortEditText: EditText
    private lateinit var wsAddressEditText: EditText
    private lateinit var wsPortEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Paramètres")

        restAddressEditText = this.findViewById(R.id.ipv4)
        restPortEditText = this.findViewById(R.id.port)
        wsAddressEditText = this.findViewById(R.id.WsAddress)
        wsPortEditText = this.findViewById(R.id.WsPort)

        saveChanges()

        loadSettings()
    }

    fun loadSettings(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("app", Context.MODE_PRIVATE)
        val restAddress = sharedPreferences.getString(getString(R.string.ipv4), "")
        val restPort = sharedPreferences.getString(getString(R.string.port), "")
        val wsAddress = sharedPreferences.getString(getString(R.string.wsAddress), "")
        val wsPort = sharedPreferences.getString(getString(R.string.wsPort), "")

        restAddressEditText.setText(restAddress)
        restPortEditText.setText(restPort)
        wsAddressEditText.setText(wsAddress)
        wsPortEditText.setText(wsPort)
    }

    fun saveChanges(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("app", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()


        restAddressEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                editor.putString(getString(R.string.ipv4), s.toString())
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        restPortEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                editor.putString(getString(R.string.port), s.toString())
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        wsAddressEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                editor.putString(getString(R.string.wsAddress), s.toString())
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        wsPortEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                editor.putString(getString(R.string.wsPort), s.toString())
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
    }
}