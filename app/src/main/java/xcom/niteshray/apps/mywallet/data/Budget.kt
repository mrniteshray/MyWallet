package xcom.niteshray.apps.mywallet.data

data class Budget(
    val category : String,
    val totalAmont : Double,
    val spentAmount : Double,
    val duration : String,
    val startDate : String,
){
    constructor() : this ("", 0.0, 0.0, "", "")
}
