package com.bignerdranch.android.pr21kma

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class EnterFragment : Fragment() {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enter, container, false)

        login = view.findViewById(R.id.edit_log) as EditText

        password = view.findViewById(R.id.edit_pass) as EditText

        enter_button = view.findViewById(R.id.ent_btn) as Button

        login.text = SpannableStringBuilder(loadText(requireContext(), false))
        password.text = SpannableStringBuilder(loadText(requireContext(), true))

        enter_button.setOnClickListener {
            if(login.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()){
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DataFragment())
                .addToBackStack(null)
                .commit()
            }
            else {
                val alert = AlertDialog.Builder(requireContext())
                    .setTitle("Ошибка")
                    .setMessage("У вас есть незаполненные поля")
                    .setPositiveButton("Ok", null)
                    .create()
                    .show()
            }
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        saveText(requireContext(), false, login.text.toString())
        saveText(requireContext(), true, password.text.toString())
    }

    override fun onStart() {
        super.onStart()
    }

    fun saveText(context: Context, pass: Boolean, text: String) {
        val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val json = Gson().toJson(text)
        if (pass) {
            editor.putString("pass", json)
        }
        else editor.putString("text", json)
        editor.apply()
    }

    fun loadText(context: Context, pass: Boolean): String {
        val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var t = "text"
        if (pass) {
            t = "pass"
        }
        else t = "text"
        val json = sharedPref.getString(t, null)
        return if (json != null) {
            Gson().fromJson(json, String::class.java) ?: ""
        } else ""
    }
}