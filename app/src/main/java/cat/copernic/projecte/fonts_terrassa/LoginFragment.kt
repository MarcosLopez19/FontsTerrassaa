package cat.copernic.projecte.fonts_terrassa

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Login2Fragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        super.onCreate(savedInstanceState)
        setup()

        binding.textviewContrasenya.setOnClickListener {
            findNavController().navigate(Login2FragmentDirections.actionLoginFragmentToRecuperarFragment())
        }

        return binding.root
    }

    /**
     * Aquesta funció permet l'accés dels usuaris a partir de vàries comprobacions.
     */
    private fun setup() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            if (binding.emailEditText.text.toString().lowercase()
                    .isNotEmpty() && binding.passwordEditText.text.toString().lowercase().isNotEmpty()
            ) {
                auth.signInWithEmailAndPassword(
                    binding.emailEditText.text.toString().lowercase(),
                    binding.passwordEditText.text.toString()
                ).addOnCompleteListener {
                    Log.d("correu", binding.emailEditText.text.toString().lowercase())
                    if (it.isSuccessful) {
                        corrutinaLogin()
                    } else {
                        showAlertIncorrect()
                    }
                }
            }
        }
    }

    private fun showAlertInactive() {
        val objectAlerDialog = AlertDialog.Builder(context)
        objectAlerDialog.setTitle(R.string.error)
        objectAlerDialog.setMessage(R.string.usuari_inactiu)
        objectAlerDialog.setPositiveButton(R.string.acceptar, null)
        var alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()
    }

    private fun showAlertIncorrect() {
        val objectAlerDialog = AlertDialog.Builder(context)
        objectAlerDialog.setTitle(R.string.error)
        objectAlerDialog.setMessage(R.string.dades_incorrectes)
        objectAlerDialog.setPositiveButton(R.string.acceptar, null)
        var alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()
    }

    private fun showHome() {
        findNavController().navigate(Login2FragmentDirections.actionLoginFragmentToAdminFragment())
    }

    /**
     * Aquesta corrutina es connecta a Firebase per comprobar si les dades introduides per l'usuari
     * son correctes.
     */
    private fun corrutinaLogin() =
        GlobalScope.launch(
            Dispatchers.Main
        ) {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            db.collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.get("email")
                                .toString() == binding.emailEditText.text.toString().lowercase()
                        ) {
                            if (document.get("active").toString().toBoolean()) {
                                auth.signOut()
                                showHome()
                            } else {
                                showAlertInactive()
                            }
                        }
                    }
                }
        }
}
