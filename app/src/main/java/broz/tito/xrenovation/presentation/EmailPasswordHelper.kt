package broz.tito.xrenovation.presentation

fun String.checkIfEmailCorrect() : Boolean {
    val regex = Regex("[a-zA-Z\\d][a-zA-Z\\d.]{0,28}[a-z\\d]?@[a-z\\d]+\\.[a-z]{2,3}")
    val dotRegex = Regex(".*\\.\\..*")
    return regex.matches(this) && !dotRegex.matches(this)
}
