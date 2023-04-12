package cat.copernic.projecte.fonts_terrassa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentEvaluateBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class EvaluateFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding: FragmentEvaluateBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_evaluate, container, false
        )

        val fontId = arguments?.getString("fontId")

        //Valorar Gust
        var gustValue = 0
        binding.starGustVal1.setOnClickListener {
            gustValue = 1
            binding.starGustVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal2.setImageResource(R.drawable.ic_starempty)
            binding.starGustVal3.setImageResource(R.drawable.ic_starempty)
            binding.starGustVal4.setImageResource(R.drawable.ic_starempty)
            binding.starGustVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starGustVal2.setOnClickListener {
            gustValue = 2
            binding.starGustVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal3.setImageResource(R.drawable.ic_starempty)
            binding.starGustVal4.setImageResource(R.drawable.ic_starempty)
            binding.starGustVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starGustVal3.setOnClickListener {
            gustValue = 3
            binding.starGustVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal4.setImageResource(R.drawable.ic_starempty)
            binding.starGustVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starGustVal4.setOnClickListener {
            gustValue = 4
            binding.starGustVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal4.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starGustVal5.setOnClickListener {
            gustValue = 5
            binding.starGustVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal4.setImageResource(R.drawable.ic_starcomplete)
            binding.starGustVal5.setImageResource(R.drawable.ic_starcomplete)
        }

        //Valorar Olor
        var olorValue = 0
        binding.starOlorVal1.setOnClickListener {
            olorValue = 1
            binding.starOlorVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal2.setImageResource(R.drawable.ic_starempty)
            binding.starOlorVal3.setImageResource(R.drawable.ic_starempty)
            binding.starOlorVal4.setImageResource(R.drawable.ic_starempty)
            binding.starOlorVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starOlorVal2.setOnClickListener {
            olorValue = 2
            binding.starOlorVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal3.setImageResource(R.drawable.ic_starempty)
            binding.starOlorVal4.setImageResource(R.drawable.ic_starempty)
            binding.starOlorVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starOlorVal3.setOnClickListener {
            olorValue = 3
            binding.starOlorVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal4.setImageResource(R.drawable.ic_starempty)
            binding.starOlorVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starOlorVal4.setOnClickListener {
            olorValue = 4
            binding.starOlorVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal4.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starOlorVal5.setOnClickListener {
            olorValue = 5
            binding.starOlorVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal4.setImageResource(R.drawable.ic_starcomplete)
            binding.starOlorVal5.setImageResource(R.drawable.ic_starcomplete)
        }

        //Valorar Transperencia
        var transperenciaValue = 0
        binding.starTransperenciaVal1.setOnClickListener {
            transperenciaValue = 1
            binding.starTransperenciaVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal2.setImageResource(R.drawable.ic_starempty)
            binding.starTransperenciaVal3.setImageResource(R.drawable.ic_starempty)
            binding.starTransperenciaVal4.setImageResource(R.drawable.ic_starempty)
            binding.starTransperenciaVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starTransperenciaVal2.setOnClickListener {
            transperenciaValue = 2
            binding.starTransperenciaVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal3.setImageResource(R.drawable.ic_starempty)
            binding.starTransperenciaVal4.setImageResource(R.drawable.ic_starempty)
            binding.starTransperenciaVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starTransperenciaVal3.setOnClickListener {
            transperenciaValue = 3
            binding.starTransperenciaVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal4.setImageResource(R.drawable.ic_starempty)
            binding.starTransperenciaVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starTransperenciaVal4.setOnClickListener {
            transperenciaValue = 4
            binding.starTransperenciaVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal4.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal5.setImageResource(R.drawable.ic_starempty)
        }
        binding.starTransperenciaVal5.setOnClickListener {
            transperenciaValue = 5
            binding.starTransperenciaVal1.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal2.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal3.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal4.setImageResource(R.drawable.ic_starcomplete)
            binding.starTransperenciaVal5.setImageResource(R.drawable.ic_starcomplete)
        }

        //Boto enviar valoracions seleccionades a la base de dades
        binding.btnEvaluate.setOnClickListener {
            //Comprovar si s'han evaluat els 3 camps
            if (gustValue != 0 && olorValue != 0 && transperenciaValue != 0) {
                //Send values to DB
                db.collection("valoracions").document()
                    .set(
                        hashMapOf(
                            "font" to fontId,
                            "gust" to gustValue,
                            "olor" to olorValue,
                            "transperencia" to transperenciaValue
                        )
                    )
                //Show Snackbar
                view?.let { it1 ->
                    Snackbar.make(
                        it1,
                        R.string.gracies_avaluacio,
                        BaseTransientBottomBar.LENGTH_SHORT
                    ).show()
                }

                //Go to viewFont
                val bundle = Bundle()
                bundle.putSerializable("fontId", fontId)
                findNavController().navigate(
                    R.id.action_evaluateFragment_to_viewFontFragment,
                    bundle
                )
            } else {
                //Show Snackbar
                view?.let { it1 ->
                    Snackbar.make(
                        it1,
                        R.string.no_avaluat,
                        BaseTransientBottomBar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root
    }
}