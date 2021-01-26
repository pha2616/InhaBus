package com.example.inhabus

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(list: List<Button>, context: Context): BaseAdapter() {
    private lateinit var context: Context
    private lateinit var list: List<Button>
    private lateinit var inflate: LayoutInflater
    private lateinit var viewHolder: ViewHolder

    init {
        this.list = list
        this.context = context
        this.inflate = LayoutInflater.from(context)
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var convertView: View? = p1
        if(convertView == null){
            convertView = inflate.inflate(R.layout.search_list, null)
            viewHolder = ViewHolder()
            viewHolder.label = convertView.findViewById(R.id.label) as Button

            convertView.setTag(viewHolder)
        }else{
            viewHolder = convertView.getTag() as ViewHolder
        }
        viewHolder.label = list[p0]
        return convertView
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }

    class ViewHolder{
        public lateinit var label: Button
    }
}