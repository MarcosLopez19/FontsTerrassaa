package cat.copernic.projecte.fonts_terrassa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentRecuperarBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecuperarFragment : Fragment() {
    private lateinit var binding: FragmentRecuperarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_recuperar, container, false
        )
        super.onCreate(savedInstanceState)
        setup()

        return binding.root
    }

    /**
     * Mètode el qual permet enviar un correu de recuperació de contrasenya al escriure un correu
     * electrònic correcte.
     */
    private fun setup() {
        binding.retrieveButton.setOnClickListener {
            val emailAddress = binding.emailEditText.text.toString()
            if (binding.emailEditText.text.toString().isNotEmpty()) {
                Firebase.auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            view?.let { it1 ->
                                Snackbar.make(
                                    it1,
                                    R.string.missatge_enviat,
                                    BaseTransientBottomBar.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            view?.let { it1 ->
                                Snackbar.make(
                                    it1,
                                    R.string.correu_incorrecte,
                                    BaseTransientBottomBar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }
    }
}