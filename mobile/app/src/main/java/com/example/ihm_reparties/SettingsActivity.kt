package com.example.ihm_reparties

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity  : AppCompatActivity()
{
    private lateinit var ipv4EditText: EditText
    private lateinit var portEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Paramètres")

        ipv4EditText = this.findViewById(R.id.ipv4)
        portEditText = this.findViewById(R.id.port)

        saveChanges()

        loadSettings()
    }

    fun loadSettings(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("app", Context.MODE_PRIVATE)
        val ipv4 = sharedPreferences.getString(getString(R.string.ipv4), "")
        val port = sharedPreferences.getString(getString(R.string.port), "")

        ipv4EditText.setText(ipv4)
        portEditText.setText(port)
    }

    fun saveChanges(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("app", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()


        ipv4EditText.addTextChangedListener(object : TextWatcher {

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

        portEditText.addTextChangedListener(object : TextWatcher {

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
    }
}