package cat.copernic.projecte.fonts_terrassa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.ViewModel.ListAdminViewModel
import cat.copernic.projecte.fonts_terrassa.adapters.FontAdminRecyclerAdapter
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentFontAdminListBinding
import cat.copernic.projecte.fonts_terrassa.models.FontAdmin
import com.google.firebase.firestore.FirebaseFirestore

class FontAdminListFragment : Fragment() {

    private var fonts: ArrayList<FontAdmin> = arrayListOf()
    private var matchedFonts: ArrayList<FontAdmin> = arrayListOf()
    private var fontAdapter: FontAdminRecyclerAdapter = FontAdminRecyclerAdapter(fonts)
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentFontAdminListBinding
    private val ViewModel: ListAdminViewModel by viewModels()
    var txtBuscar: SearchView? = null
    private var fontsArray = arrayOf<String>()
    lateinit var imageView: ImageView
    lateinit var selectedFont: BooleanArray
    private var fontList: ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_font_admin_list, container, false
        )

        fontsArray = arrayOf(resources.getString(R.string.fonts_boca),
            resources.getString(R.string.fonts_boca_singulars),
            resources.getString(R.string.fonts_ornamentals),
            resources.getString(R.string.fonts_naturals),
            resources.getString(R.string.fonts_gossos))

        txtBuscar = binding.svFonts
        imageView = binding.imageViewFilter
        selectedFont = BooleanArray(fontsArray.size)

        //Posem el menu de filtres amb totes les opcions seleccionades
        for (j in 0..4) {
            selectedFont[j] = true
        }

        if (fontAdapter.fontsAdmin.size == 0) {
            context?.let { ViewModel.filterFontsByType(binding, it, selectedFont) }
        }

        imageView.setOnClickListener {

            fontList.clear()

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.seleccionar_tipus_font)

            context?.let { ViewModel.clearFontsByType(binding, it) }
            builder.setCancelable(false)
            builder.setMultiChoiceItems(
                fontsArray, selectedFont
            ) { _, _, _ ->

            }
            builder.setPositiveButton(
                R.string.acceptar
            ) { _, _ ->
                context?.let { ViewModel.filterFontsByType(binding, it, selectedFont) }
            }
            builder.setNeutralButton(
                R.string.seleccionar_tot
            ) { _, _ ->
                for (j in 0..4) {
                    selectedFont[j] = true
                }
                context?.let { ViewModel.filterFontsByType(binding, it, selectedFont) }
            }
            builder.show()
        }

        binding.spinnerOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            /**
             * Funció que li fem "override" a la funció pare per re-filtrar les fonts
             * un cop es selecciona un item al filtre.
             */
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (binding.spinnerOrder.selectedItem.toString()) {
                    resources.getString(R.string.nom_asc) ->
                        context?.let {
                            ViewModel.sortFontNameASC(binding, it)
                        }

                    resources.getString(R.string.nom_desc) ->
                        context?.let {
                            ViewModel.sortFontNameDESC(binding, it)
                        }
                    resources.getString(R.string.tipus) ->
                        context?.let {
                            ViewModel.sortFontTypeASC(binding, it)
                        }
                }
            }
        }
        initRecyclerView()
        performSearch()

        binding.addFont.setOnClickListener {
            findNavController().navigate(FontAdminListFragmentDirections.actionFontAdminListFragmentToEditFontFragment2())
        }

        return binding.root
    }

    override fun onResume() {
        initRecyclerView()
        super.onResume()
    }

    /**
     * Funció que inicialitza el RecyclerView
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        fonts = ViewModel.getFonts()

        fontAdapter = FontAdminRecyclerAdapter(fonts).also {
            binding.rvFonts.adapter = it
            binding.rvFonts.adapter!!.notifyDataSetChanged()
        }
        binding.svFonts.isSubmitButtonEnabled = true
    }

    /**
     * Funció per al buscador on es crida la funció search quan canvia el text del buscardor fer fer la cerca.
     */
    private fun performSearch() {
        binding.svFonts.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }

    /**
     * Funció que cridem un cop es busca algo al buscador i fa la consulta a ala base de dades per
     * tornar a mostrar-les filtrant per les paraules que s'han buscar al buscador.
     */
    private fun search(text: String?) {
        matchedFonts = arrayListOf()
        fonts.clear()
        db.collection("fonts").get().addOnSuccessListener { documents ->
            for (document in documents) {
                fonts.add(
                    FontAdmin(
                        document.get("id").toString(),
                        document.get("name").toString(),
                        document.get("lat").toString().toDouble(),
                        document.get("lon").toString().toDouble(),
                        document.get("info").toString(),
                        document.get("estat").toString().toInt(),
                        document.get("type").toString().toInt(),
                        document.get("address").toString()
                    )
                )
            }
            text?.let {
                fonts.forEach { font ->
                    if (font.name.contains(text, true)
                    ) {
                        matchedFonts.add(font)
                    }
                }
                if (matchedFonts.isEmpty()) {
                    Log.d("buit", "esta buit")
                }
                updateRecyclerView()
            }
        }
    }


    /**
     * Funció que actualitza els resultats al ViewModel a partir de borrar i actualitzar les llistes.
     */
    private fun updateRecyclerView() {
        binding.rvFonts.apply {
            fontAdapter.fontsAdmin.clear()
            fontAdapter.fontsAdmin.addAll(matchedFonts)
            context?.let { ViewModel.sortFontNameASC(binding, it) }
            fontAdapter.notifyDataSetChanged()
        }
    }
}
