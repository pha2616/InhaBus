package com.example.inhabus.navigation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.inhabus.R
import com.example.inhabus.SearchAdapter

class SearchFragment : Fragment() {
    private lateinit var list: ArrayList<Button>
    private lateinit var listView: ListView
    private lateinit var editSearch: EditText
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var arraylist: ArrayList<Button>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)
        editSearch = view!!.findViewById(R.id.edit_search)
        listView = view!!.findViewById(R.id.listview_search)

        list = ArrayList<Button>()
        //
        var button = Button(view!!.context)
        button.text = "일산"
        //
        list.add(button)
        button.text = "강남"
        list.add(button)
        list.add(Button(view!!.context))

        arraylist = ArrayList<Button>()
        arraylist.addAll(list)

        searchAdapter = SearchAdapter(list, view!!.context)

        listView.adapter = searchAdapter

        editSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                var text: String = editSearch.text.toString()

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        return view
    }

    fun search(charText: String){
        //clear

        if(charText.length == 0){

        }
        else{

        }
    }
}