package xcom.niteshray.apps.mywallet.data

data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String,
    val selectedCurrency: String,
    val avalableAmount : Int
)
