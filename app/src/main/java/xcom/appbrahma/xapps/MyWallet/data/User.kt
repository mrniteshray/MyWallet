package xcom.appbrahma.xapps.MyWallet.data

data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String,
    val selectedCurrency: String,
    val avalableAmount : Int
){
    constructor() : this("", "", "", "", "", 0)
}
