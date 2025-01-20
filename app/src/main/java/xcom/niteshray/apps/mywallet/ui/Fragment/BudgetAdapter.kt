package xcom.niteshray.apps.mywallet.ui.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.Budget

class BudgetAdapter(private val budgets: List<Budget>) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {
    class BudgetViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val tvCategory = itemview.findViewById<TextView>(R.id.tvCategory)
        val tvBudgetAmount = itemview.findViewById<TextView>(R.id.tvBudgetAmount)
        val progressBar = itemview.findViewById<ProgressBar>(R.id.progressBar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return budgets.size
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        var current = budgets[position]
        holder.tvCategory.text = current.category
        holder.tvBudgetAmount.text = "₹${current.spentAmount} / ₹${current.totalAmont}"
        holder.progressBar.max = current.totalAmont.toInt()
        holder.progressBar.progress = current.spentAmount.toInt()
    }
}