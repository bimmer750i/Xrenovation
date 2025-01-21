package broz.tito.xrenovation.presentation

fun String.checkIfEmailCorrect() : Boolean {
    val regex = Regex("([a-z\\d])[a-z0-9.]{4,28}[a-z\\d]@[a-z\\d]+\\.[a-z]{2,3}")
    val dotRegex = Regex(".*\\.\\..*")
    return regex.matches(this) && !dotRegex.matches(this)
}
