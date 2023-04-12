package cat.copernic.projecte.fonts_terrassa

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentViewFontBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class viewFontFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var binding: FragmentViewFontBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_view_font, container, false
        )

        //Agafem els arguments del bundle
        val fontId = arguments?.getString("fontId")

        binding.btnTestWater.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("fontId", fontId)
            findNavController().navigate(R.id.action_viewFontFragment_to_evaluateFragment, bundle)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(viewFontFragmentDirections.actionViewFontFragmentToFragmentList())
        }

        /*
        * A partir de la fontId, obtenim el font type i depenent del valor canviem text i imatge del tipus de la font
        * Finalment seleccionem l'ubicació a anar amb el botó del mapa de la pantalla
        */
        db.collection("fonts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (fontId == document.get("id").toString()) {
                        binding.txtNomFont.text = document.get("name").toString()
                        if (document.get("info").toString().isEmpty()) {
                            binding.imgInfo.visibility = View.GONE
                            binding.txtInformacio.visibility = View.GONE
                            binding.txtTitolInformacio.visibility = View.GONE
                        } else {
                            binding.txtInformacio.text = document.get("info").toString()
                        }
                        when (document.get("estat").toString().toInt()) {
                            0 -> {
                                binding.estatText?.text =
                                    requireContext().getText(R.string.disponible)
                            }
                            1 -> {
                                binding.estatText?.text =
                                    requireContext().getText(R.string.no_disponible)
                            }
                        }
                        when (document.get("type").toString().toInt()) {
                            3, 4, 5 -> {
                                showWarningAlert()
                                hideEvaluate()
                            }
                        }
                        when (document.get("type").toString().toInt()) {
                            1 -> {
                                binding.tipusFontTxt.text =
                                    requireContext().getText(R.string.font_boca)
                                binding.imgFontType.visibility = View.VISIBLE
                                binding.imgFontType.setImageResource(R.drawable.gota_1)
                            }
                            2 -> {
                                binding.tipusFontTxt.text =
                                    requireContext().getText(R.string.font_boca_singular)
                                binding.imgFontType.visibility = View.VISIBLE
                                binding.imgFontType.setImageResource(R.drawable.gota_2)
                            }
                            3 -> {
                                binding.tipusFontTxt.text =
                                    requireContext().getText(R.string.font_ornamental)
                                binding.imgFontType.visibility = View.VISIBLE
                                binding.imgFontType.setImageResource(R.drawable.gota_3)
                            }
                            4 -> {
                                binding.tipusFontTxt.text =
                                    requireContext().getText(R.string.font_natural)
                                binding.imgFontType.visibility = View.VISIBLE
                                binding.imgFontType.setImageResource(R.drawable.gota_4)
                            }
                            5 -> {
                                binding.tipusFontTxt.text =
                                    requireContext().getText(R.string.font_gos)
                                binding.imgFontType.visibility = View.VISIBLE
                                binding.imgFontType.setImageResource(R.drawable.gota_5)
                            }
                        }
                        binding.btnGoToMaps.setOnClickListener {
                            val latitude = document.get("lat").toString().toDouble()
                            val longitude = document.get("lon").toString().toDouble()
                            val label = document.get("name").toString()
                            val uriBegin = "geo:$latitude,$longitude"
                            val query = "$latitude,$longitude($label)"
                            val encodedQuery = Uri.encode(query)
                            val uriString = "$uriBegin?q=$encodedQuery&z=14"
                            val uri = Uri.parse(uriString)
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                        }
                    }
                }
            }

        //Inicialitzem mitjana valoracions
        var gustTotal = 0
        var olorTotal = 0
        var transperenciaTotal = 0
        var contador = 0

        //Calculem les 3 valoracions mitjanes a partir de les dades de les based de dades
        db.collection("valoracions")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.get("font") == fontId) {
                        contador++
                        gustTotal += document.get("gust").toString().toInt()
                        olorTotal += document.get("olor").toString().toInt()
                        transperenciaTotal += document.get("transperencia").toString().toInt()
                    }
                }
                if (contador != 0) {
                    setGustAvg(gustTotal / contador)
                    setOlorAvg(olorTotal / contador)
                    setTrasperenciaAvg(transperenciaTotal / contador)
                }
                binding.countValoracionsText?.text =
                    contador.toString() + " " + requireContext().getText(R.string.valoracions) + "."
            }

        if (fontId != null) {
            descarregarImatgeGlide(fontId)
        }

        binding.btnIncidencia.setOnClickListener {
            val uri =
                Uri.parse("https://play.google.com/store/apps/details?id=com.idasasistemas.terrassacuidem")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return binding.root
    }

    /**
     * Amb aquesta funció posem l'imatge emagatzemada a l'Storage al lloc que volem (imgFont)
     */
    private fun descarregarImatgeGlide(fontId: String) {
        val imgPath = "images/" + fontId + ".jpg"
        val imageRef = storageRef.child(imgPath)
        imageRef.downloadUrl.addOnSuccessListener { url ->

            Glide.with(this)
                .load(url.toString())
                .centerInside()
                .placeholder((R.drawable.loading))
                .error(R.drawable.ic_noimage)
                .into(binding.imgFont)

        }.addOnFailureListener {
            binding.imgFont.setImageResource(R.drawable.ic_noimage)
        }
    }

    /**
     * Amb aquesta funció posem les estrelles a partir de la valoració mitjana del gust
     */
    private fun setGustAvg(gustAvg: Int) {
        when (gustAvg) {
            1 -> {
                binding.starGust1.setImageResource(R.drawable.ic_starcomplete)
            }
            2 -> {
                binding.starGust1.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust2.setImageResource(R.drawable.ic_starcomplete)
            }
            3 -> {
                binding.starGust1.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust2.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust3.setImageResource(R.drawable.ic_starcomplete)
            }
            4 -> {
                binding.starGust1.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust2.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust3.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust4.setImageResource(R.drawable.ic_starcomplete)
            }
            5 -> {
                binding.starGust1.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust2.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust3.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust4.setImageResource(R.drawable.ic_starcomplete)
                binding.starGust5.setImageResource(R.drawable.ic_starcomplete)
            }
        }
    }

    /**
     * Amb aquesta funció posem les estrelles a partir de la valoració mitjana de l'olor
     */
    private fun setOlorAvg(olorAvg: Int) {
        when (olorAvg) {
            1 -> {
                binding.starOlor1.setImageResource(R.drawable.ic_starcomplete)
            }
            2 -> {
                binding.starOlor1.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor2.setImageResource(R.drawable.ic_starcomplete)
            }
            3 -> {
                binding.starOlor1.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor2.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor3.setImageResource(R.drawable.ic_starcomplete)
            }
            4 -> {
                binding.starOlor1.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor2.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor3.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor4.setImageResource(R.drawable.ic_starcomplete)
            }
            5 -> {
                binding.starOlor1.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor2.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor3.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor4.setImageResource(R.drawable.ic_starcomplete)
                binding.starOlor5.setImageResource(R.drawable.ic_starcomplete)
            }
        }
    }

    /**
     * Amb aquesta funció posem les estrelles a partir de la valoració mitjana de la transperencia
     */
    private fun setTrasperenciaAvg(transperenciaAvg: Int) {
        when (transperenciaAvg) {
            1 -> {
                binding.starTransperencia1.setImageResource(R.drawable.ic_starcomplete)
            }
            2 -> {
                binding.starTransperencia1.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia2.setImageResource(R.drawable.ic_starcomplete)
            }
            3 -> {
                binding.starTransperencia1.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia2.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia3.setImageResource(R.drawable.ic_starcomplete)
            }
            4 -> {
                binding.starTransperencia1.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia2.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia3.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia4.setImageResource(R.drawable.ic_starcomplete)
            }
            5 -> {
                binding.starTransperencia1.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia2.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia3.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia4.setImageResource(R.drawable.ic_starcomplete)
                binding.starTransperencia5.setImageResource(R.drawable.ic_starcomplete)
            }
        }
    }

    private fun showWarningAlert() {
        val objectAlerDialog = AlertDialog.Builder(context)
        objectAlerDialog.setTitle(R.string.atencio)
        objectAlerDialog.setMessage(R.string.garentia_sanitaria)
        objectAlerDialog.setPositiveButton(R.string.acceptar, null)
        val alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()
    }

    private fun hideEvaluate() {
        binding.txtTitolValoracio.visibility = View.GONE
        binding.imgCheck.visibility = View.GONE
        binding.txtgust.visibility = View.GONE
        binding.txtOlor.visibility = View.GONE
        binding.txtTrasperencia.visibility = View.GONE
        binding.btnTestWater.visibility = View.GONE
        binding.starTransperencia5.visibility = View.GONE
        binding.starTransperencia4.visibility = View.GONE
        binding.starTransperencia3.visibility = View.GONE
        binding.starTransperencia2.visibility = View.GONE
        binding.starTransperencia1.visibility = View.GONE
        binding.starOlor5.visibility = View.GONE
        binding.starOlor4.visibility = View.GONE
        binding.starOlor3.visibility = View.GONE
        binding.starOlor2.visibility = View.GONE
        binding.starOlor1.visibility = View.GONE
        binding.starGust5.visibility = View.GONE
        binding.starGust4.visibility = View.GONE
        binding.starGust3.visibility = View.GONE
        binding.starGust2.visibility = View.GONE
        binding.starGust1.visibility = View.GONE
        binding.countValoracionsText?.visibility = View.GONE
    }
}
