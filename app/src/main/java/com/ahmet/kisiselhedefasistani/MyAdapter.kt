package com.ahmet.kisiselhedefasistani

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(private val context:android.content.Context, private var dataList:List<DataClass>):RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].iwhedefresim).into(holder.recImage)
        holder.recTitle.text = dataList[position].ethedefismi
        holder.recCat.text = dataList[position].ethedefkategori
        holder.recPriority.text = dataList[position].ethedefoncelik

        holder.recCard.setOnClickListener {
            val intent = Intent(context,DetayActivity::class.java)
            intent.putExtra("Image", dataList[holder.adapterPosition].iwhedefresim)
            intent.putExtra("Description", dataList[holder.adapterPosition].ethedefaciklama)
            intent.putExtra("Category", dataList[holder.adapterPosition].ethedefkategori)
            intent.putExtra("Title", dataList[holder.adapterPosition].ethedefismi)
            intent.putExtra("Priority", dataList[holder.adapterPosition].ethedefoncelik)
            intent.putExtra("Key", dataList[holder.adapterPosition].key)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    fun searchDataList(searchList: List<DataClass>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    var recImage: ImageView
    var recTitle: TextView
    var recCat: TextView
    var recPriority: TextView
    var recCard: CardView

    init {
        recImage = itemView.findViewById(R.id.recImage)
        recTitle = itemView.findViewById(R.id.recTitle)
        recCat = itemView.findViewById(R.id.recCat)
        recPriority = itemView.findViewById(R.id.recPriority)
        recCard = itemView.findViewById(R.id.recCard)
    }

}


