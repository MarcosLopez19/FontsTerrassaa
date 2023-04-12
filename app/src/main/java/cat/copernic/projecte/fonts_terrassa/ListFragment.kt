package cat.copernic.projecte.fonts_terrassa

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cat.copernic.projecte.fonts_terrassa.ViewModel.ListViewModel
import cat.copernic.projecte.fonts_terrassa.adapters.FontRecyclerAdapter
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentListBinding
import cat.copernic.projecte.fonts_terrassa.models.Font
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore

class ListFragment : Fragment() {

    private var fonts: ArrayList<Font> = arrayListOf()
    private var matchedFonts: ArrayList<Font> = arrayListOf()
    private var fontAdapter: FontRecyclerAdapter = FontRecyclerAdapter(fonts)
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()
    private var fontsArray = arrayOf<String>()
    lateinit var imageView: ImageView
    private lateinit var selectedFont: BooleanArray
    private var fontList: ArrayList<Int> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )

        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            showErrorLocationNotEnabled()
        }

        fontsArray = arrayOf(
            resources.getString(R.string.fonts_boca),
            resources.getString(R.string.fonts_boca_singulars),
            resources.getString(R.string.fonts_ornamentals),
            resources.getString(R.string.fonts_naturals),
            resources.getString(R.string.fonts_gossos)
        )

        imageView = binding.imageView
        selectedFont = BooleanArray(fontsArray.size)

        for (j in 0..4) {
            selectedFont[j] = true
        }

        performSearch()
        viewModel.filterFontsByType(binding, requireContext(), selectedFont)

        imageView.setOnClickListener {
            fontList.clear()
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.seleccionar_tipus_font)

            viewModel.clearFontsByType(binding, requireContext())
            builder.setCancelable(false)
            builder.setMultiChoiceItems(
                fontsArray, selectedFont
            ) { _, _, _ ->

            }
            builder.setPositiveButton(
                R.string.acceptar
            ) { _, _ ->
                viewModel.filterFontsByType(binding, requireContext(), selectedFont)
            }
            builder.setNeutralButton(
                R.string.seleccionar_tot
            ) { _, _ ->
                selectedFont.fill(true, 0)
                viewModel.filterFontsByType(binding, requireContext(), selectedFont)
            }
            builder.show()
        }

        //Aquest listener s'encarrega de cridar els mètodes necessaris per ordenar el llistat de
        //fonts del Recyclerview.
        binding.spinnerOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (binding.spinnerOrder.selectedItem.toString()) {
                    resources.getString(R.string.nom_asc) ->
                        viewModel.sortFontNameASC(binding, requireContext())
                    resources.getString(R.string.nom_desc) ->
                        viewModel.sortFontNameDESC(binding, requireContext())
                    resources.getString(R.string.distancia_asc) ->
                        viewModel.sortFontLocationASC(binding, requireContext())
                    resources.getString(R.string.distancia_desc) ->
                        viewModel.sortFontLocationDESC(binding, requireContext())
                    resources.getString(R.string.tipus) ->
                        viewModel.sortFontTypeASC(binding, requireContext())
                }
            }
        }

        initRecyclerView()
        return binding.root
    }

    override fun onResume() {
        initRecyclerView()
        super.onResume()
    }

    private fun initRecyclerView() {
        fonts = viewModel.getFonts()

        fontAdapter = FontRecyclerAdapter(fonts).also {
            binding.rvFonts.adapter = it
            binding.rvFonts.adapter!!.notifyDataSetChanged()
        }
        binding.svFonts.isSubmitButtonEnabled = true
    }

    private fun showErrorLocationNotEnabled() {
        val objectAlerDialog = AlertDialog.Builder(context)
        objectAlerDialog.setTitle(cat.copernic.projecte.fonts_terrassa.R.string.error)
        objectAlerDialog.setMessage(cat.copernic.projecte.fonts_terrassa.R.string.gps_network_not_enabled)
        objectAlerDialog.setPositiveButton(
            cat.copernic.projecte.fonts_terrassa.R.string.acceptar,
            null
        )
        val alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()
    }

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
     * Aquest mètode té la funcionalitat de realitzar un filtre dels elements del Recyclerview
     * a l'escriure dins del Searchview del fragment.
     */
    private fun search(text: String?) {
        matchedFonts = arrayListOf()
        fonts.clear()
        lateinit var myActualPos: Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    myActualPos = it
                    db.collection("fonts").get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            val fontLoc = Location("")
                            fontLoc.latitude = document.get("lat").toString().toDouble()
                            fontLoc.longitude = document.get("lon").toString().toDouble()
                            val value = (myActualPos.distanceTo(fontLoc) / 1000).toDouble()
                            fonts.add(
                                Font(
                                    document.get("id").toString(),
                                    document.get("name").toString(),
                                    document.get("lat").toString().toDouble(),
                                    document.get("lon").toString().toDouble(),
                                    document.get("info").toString(),
                                    document.get("estat").toString().toInt(),
                                    document.get("type").toString().toInt(),
                                    document.get("address").toString(),
                                    (Math.round(value * 100) / 100.0)
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
                                Log.d("buit", R.string.buit.toString())
                            }
                            updateRecyclerView()
                        }
                    }
                }
            }
    }

    /**
     * Mètode encarregat d'actualitzar els elements del Recycclerview a l'utilitzar la búsqueda.
     */
    private fun updateRecyclerView() {
        binding.rvFonts.apply {
            fontAdapter.fonts.clear()
            fontAdapter.fonts.addAll(matchedFonts)
            viewModel.sortFontNameASC(binding, requireContext())
            fontAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Aquest mètode selecciona tots els tipus de fonts.
     */
    fun selectAllFonts() {
        for (j in 0..4) {
            selectedFont[j] = true
        }
    }

    fun getItemSelected(): Int {
        when (binding.spinnerOrder.selectedItem.toString()) {
            resources.getString(R.string.nom_asc) ->
                return 0
            resources.getString(R.string.nom_desc) ->
                return 1
            resources.getString(R.string.distancia_asc) ->
                return 2
            resources.getString(R.string.distancia_desc) ->
                return 3
            resources.getString(R.string.tipus) ->
                return 4
        }

        return -1
    }
}
