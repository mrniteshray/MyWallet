package xcom.niteshray.apps.mywallet.ui.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.RecentActivity

class RecentAdapter(private val list: ArrayList<RecentActivity>) : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {
    class ViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val categoryName = itemview.findViewById<TextView>(R.id.categoryName)
        val amount : TextView = itemview.findViewById(R.id.amountTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_itemview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var current = list[position]
        holder.categoryName.text = current.cateroryName
        holder.amount.text = current.amount.toString()
    }
}