package com.bignerdranch.android.pr21kma

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataFragment : Fragment() {

    private val dateAndTime: Calendar = Calendar.getInstance()
    private lateinit var time_button: Button
    private lateinit var date_button: Button
    private lateinit var spinner: Spinner
    private lateinit var enter_list: Button
    private lateinit var data_add: Button
    private lateinit var title_data: EditText
    private lateinit var check_data: CheckBox

    private val dataList = arrayOf("Дома", "Дополнительно", "Работа")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data, container, false)

        time_button = view.findViewById(R.id.time_btn) as Button

        date_button = view.findViewById(R.id.date_btn) as Button

        spinner = view.findViewById(R.id.spinner) as Spinner

        enter_list = view.findViewById(R.id.enter_list) as Button

        data_add = view.findViewById(R.id.add_data) as Button

        title_data = view.findViewById(R.id.title_data) as EditText

        title_data.text = SpannableStringBuilder(loadTitle(requireContext()))

        check_data = view.findViewById(R.id.checkbox_data) as CheckBox

        time_button.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE),
                true
            ).show()
        }

        date_button.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dataList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        enter_list.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListFragment())
                .addToBackStack(null)
                .commit()
        }

        data_add.apply {
            isEnabled = false
        }

        data_add.setOnClickListener {
            val newItem = DataList(
                data = title_data.text.toString(),
                importance = check_data.isChecked,
                category = spinner.selectedItem.toString(),
                date = "${dateAndTime.get(Calendar.DAY_OF_MONTH)}.${dateAndTime.get(Calendar.MONTH)+1}.${dateAndTime.get(Calendar.YEAR)}",
                time = "${dateAndTime.get(Calendar.HOUR_OF_DAY)}:${dateAndTime.get(Calendar.MINUTE)}"
            )
            ListFragment.addNewData(requireContext(), newItem)
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        saveTitle(requireContext(), title_data.text.toString())
    }

    override fun onStart() {
        super.onStart()
        if(title_data.text.toString().isNotEmpty()){
            data_add.isEnabled = true
        }
    }

    private val t = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        dateAndTime.set(Calendar.MINUTE, minute)
    }

    private val d = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    fun saveTitle(context: Context, title: String) {
        val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val json = Gson().toJson(title)
        editor.putString("title", json)
        editor.apply()
    }

    fun loadTitle(context: Context): String {
        val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("title", null)
        return if (json != null) {
            Gson().fromJson(json, String::class.java) ?: ""
        } else ""
    }
}
