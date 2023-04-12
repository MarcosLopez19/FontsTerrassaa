package cat.copernic.projecte.fonts_terrassa.models

data class User(val email: String) {
    var myEmail: String? = null

    init {
        this.myEmail = email
    }

}