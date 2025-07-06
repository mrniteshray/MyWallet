package xcom.appbrahma.xapps.MyWallet.data

data class ExpenseData(
    val cateroryName: String,
    val date: String,
    val amount: Int,
    val note: String = "",
    val id: Int,
    val timestamp: Long = System.currentTimeMillis()
){
    constructor() : this("", "", 0, "", 0)
}
