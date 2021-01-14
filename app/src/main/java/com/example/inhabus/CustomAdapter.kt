package com.example.inhabus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*

class CustomAdapter: RecyclerView.Adapter<Holder>() {
    var alarmList = mutableListOf<AlarmList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val alarmlist = alarmList.get(position)
        holder.setAlarmList(alarmlist)
    }

}

class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
    fun setAlarmList(alarmList: AlarmList){
        itemView.id_date.text = alarmList.date
        itemView.id_time.text = alarmList.time
        itemView.id_direction.text = alarmList.direction
        itemView.id_city.text = alarmList.city
    }
}