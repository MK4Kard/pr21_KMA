package com.bignerdranch.android.pr21kma

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListFragment : Fragment() {

    companion object {
        val typeList = object : TypeToken<MutableList<DataList>>() {}.type

        var globalList: MutableList<DataList> = mutableListOf()

        fun addNewData(context: Context, item: DataList) {
            globalList.add(item)
            saveGlobalList(context, globalList)
        }

        fun saveGlobalList(context: Context, list: MutableList<DataList>) {
            val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val json = Gson().toJson(list)
            editor.putString("global_list", json)
            editor.apply()
        }

        fun loadGlobalList(context: Context): MutableList<DataList> {
            val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val json = sharedPref.getString("global_list", "[]")
            return Gson().fromJson(json, typeList)
        }
    }

    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        globalList = loadGlobalList(requireContext())

        val listView = view.findViewById<ListView>(R.id.list_view)

        adapter = CustomAdapter(requireContext(), globalList)
        listView.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        (view?.findViewById<ListView>(R.id.list_view)?.adapter as? CustomAdapter)?.notifyDataSetChanged()
    }
}

class CustomAdapter(
    context: Context,
    private val dataList: List<DataList>
) : ArrayAdapter<DataList>(context, R.layout.list_item, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = convertView ?: inflater.inflate(R.layout.list_item, parent, false)

        val item = dataList[position]

        val data = rowView.findViewById<TextView>(R.id.text_data)
        val check = rowView.findViewById<CheckBox>(R.id.check_data)
        val category = rowView.findViewById<TextView>(R.id.category_data)
        val time = rowView.findViewById<TextView>(R.id.time_data)
        val date = rowView.findViewById<TextView>(R.id.date_data)

        data.text = item.data
        category.text = item.category
        time.text = item.time
        date.text = item.date
        check.isChecked = item.importance

        return rowView
    }
}