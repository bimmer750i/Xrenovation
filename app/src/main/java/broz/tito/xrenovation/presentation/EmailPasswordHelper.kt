package broz.tito.xrenovation.presentation

fun String.checkIfEmailCorrect() : Boolean {
    if (!this.contains("@")) {
        return false
    }
    else if (this.indexOf("@") == 0) {
        return false
    }
    else if (this.indexOf("@") == this.length - 1) {
        return false
    }
    else if (!this.contains(".")) {
        return false
    }
    else if (this.drop(this.indexOf("@") + 1).indexOf(".") < 1) {
        return false
    }
    else if (this.drop(this.indexOf(".") + 1).length < 2) {
        return false
    }
    else {
        return true
    }
}