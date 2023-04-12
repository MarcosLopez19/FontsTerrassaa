package cat.copernic.projecte.fonts_terrassa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_info, container, false
        )

        when ((activity as MainActivity?)?.getLocale()) {
            "ca" ->
                binding.spinnerLanguage.setSelection(0)
            "es" ->
                binding.spinnerLanguage.setSelection(1)
            "en" ->
                binding.spinnerLanguage.setSelection(2)
        }

        changeLanguage(binding)

        binding.btnLoginAdmin.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionFragmentInfoToLoginFragment())
        }
        binding.btnBeure.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionFragmentInfoToInfoBeureFragment())
        }
        binding.btnSingular.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionFragmentInfoToInfoSingularFragment())
        }
        binding.btnOrnamental.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionFragmentInfoToInfoOrnamentalFragment())
        }
        binding.btnNatural.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionFragmentInfoToInfoNaturalFragment())
        }

        binding.btnGos.setOnClickListener {
            findNavController().navigate(InfoFragmentDirections.actionFragmentInfoToInfoGosFragment())
        }

        return binding.root
    }

    /**
     * Funció la qual permet cambiar l'idioma de l'app a partir del spinner del fragment.
     */
    private fun changeLanguage(binding: FragmentInfoBinding) {

        binding.spinnerLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (binding.spinnerLanguage.selectedItem.toString()) {
                        "Català" -> {
                            (activity as MainActivity?)?.setLocale("ca", 1)
                        }
                        "Español" -> {
                            (activity as MainActivity?)?.setLocale("es", 1)
                        }
                        "English" -> {
                            (activity as MainActivity?)?.setLocale("en", 1)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

}