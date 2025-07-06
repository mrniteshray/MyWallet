package xcom.appbrahma.xapps.MyWallet.ui.Fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.Budget

class BudgetAdapter(private val budgets: List<Budget>,private val onItemDelete: (String) -> Unit) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {
    class BudgetViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val tvCategory = itemview.findViewById<TextView>(R.id.tvCategory)
        val tvBudgetAmount = itemview.findViewById<TextView>(R.id.tvBudgetAmount)
        val progressBar = itemview.findViewById<ProgressBar>(R.id.progressBar)
        val percentage = itemview.findViewById<TextView>(R.id.tvPercentage)
        val totalBudget = itemview.findViewById<TextView>(R.id.tvTotalBudget)
        val btnDelete = itemview.findViewById<ImageView>(R.id.btn_delete)

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
        holder.totalBudget.text = "Total Budget : ₹${current.totalAmont}"
        val percentage = (current.spentAmount / current.totalAmont) * 100
        holder.percentage.text = "${percentage.toInt()}%"
        holder.progressBar.max = current.totalAmont.toInt()
        holder.progressBar.progress = current.spentAmount.toInt()
        holder.btnDelete.setOnClickListener {
            onItemDelete(current.category)
        }
        holder.progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor(getProgressColor(current.spentAmount, current.totalAmont)))
    }

    private fun getProgressColor(spentAmount: Double, totalAmont: Double): String? {
        val percentage = (spentAmount / totalAmont) * 100
        if (percentage<80){
            return "#20DE00"
        }else if (percentage>80 && percentage<95){
            return "#FF7700"
        }else{
            return "#FF0000"
        }
    }
}