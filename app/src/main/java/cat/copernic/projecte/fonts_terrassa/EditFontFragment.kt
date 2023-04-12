package cat.copernic.projecte.fonts_terrassa

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentEditFontBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.Double.parseDouble
import java.lang.Exception

class EditFontFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var oldFontId = ""
    private var hasImage = false
    private lateinit var imageView: ImageView
    private lateinit var storageRef: StorageReference
    private lateinit var binding: FragmentEditFontBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_font, container, false)
        var longYlat = arguments?.getString("font_lon")+","+arguments?.getString("font_lat")
        //Introduim les dades als camps que ho necesiten
        binding.inputIdFont.setText(arguments?.getString("id_font"))
        binding.txtNomFont.setText(arguments?.getString("font_name"))
        oldFontId = binding.inputIdFont.text.toString()
        binding.editLon.setText(longYlat) // tenemos que modificar este apartado para que pille logitud y latitud
        binding.txtInformacio.setText(arguments?.getString("font_info"))
        binding.editAdreca.setText(arguments?.getString("font_adreca"))
        when(arguments?.getInt("estat")){
            0 ->
                binding.estatSpinner?.setSelection(0)
            1 ->
                binding.estatSpinner?.setSelection(1)
        }
        var myFontType = 0
        when (arguments?.getInt("font_type")) {
            1 -> {
                myFontType = 1
                binding.btnTypeSel1.setImageResource(R.drawable.gota_1_selected)
            }
            2 -> {
                myFontType = 2
                binding.btnTypeSel2.setImageResource(R.drawable.gota_2_selected)
            }
            3 -> {
                myFontType = 3
                binding.btnTypeSel3.setImageResource(R.drawable.gota_3_selected)
            }
            4 -> {
                myFontType = 4
                binding.btnTypeSel4.setImageResource(R.drawable.gota_4_selected)
            }
            5 -> {
                myFontType = 5
                binding.btnTypeSel5.setImageResource(R.drawable.gota_5_selected)
            }
        }

        //Definim imgFont com a imageView
        imageView = binding.imgFont
        storageRef = FirebaseStorage.getInstance().reference

        //Click al botó de pujar imatge
        binding.btnUpload.setOnClickListener {
            openGallery()
        }

        //Click al botó de tornat endarrera
        binding.btnBack.setOnClickListener {
            findNavController().navigate(EditFontFragmentDirections.actionEditFontFragmentToFontAdminListFragment())
        }

        //Click al botó de borrar imatge
        binding.btnRemoveImg.setOnClickListener {
            //Delete Image
            if (hasImage) {
                deleteImage(binding.inputIdFont.text.toString())
                hasImage = false
                hideImageDelete()
                Snackbar.make(
                    binding.frameLayout,
                    R.string.imatge_eliminada,
                    BaseTransientBottomBar.LENGTH_SHORT
                ).show()
            }
        }

        //Selecció de fonts gràfica
        binding.btnTypeSel1.setOnClickListener {
            myFontType = 1
            binding.btnTypeSel1.setImageResource(R.drawable.gota_1_selected)
            binding.btnTypeSel2.setImageResource(R.drawable.gota_2_small)
            binding.btnTypeSel3.setImageResource(R.drawable.gota_3_small)
            binding.btnTypeSel4.setImageResource(R.drawable.gota_4_small)
            binding.btnTypeSel5.setImageResource(R.drawable.gota_5_small)
        }
        binding.btnTypeSel2.setOnClickListener {
            myFontType = 2
            binding.btnTypeSel1.setImageResource(R.drawable.gota_1_small)
            binding.btnTypeSel2.setImageResource(R.drawable.gota_2_selected)
            binding.btnTypeSel3.setImageResource(R.drawable.gota_3_small)
            binding.btnTypeSel4.setImageResource(R.drawable.gota_4_small)
            binding.btnTypeSel5.setImageResource(R.drawable.gota_5_small)
        }
        binding.btnTypeSel3.setOnClickListener {
            myFontType = 3
            binding.btnTypeSel1.setImageResource(R.drawable.gota_1_small)
            binding.btnTypeSel2.setImageResource(R.drawable.gota_2_small)
            binding.btnTypeSel3.setImageResource(R.drawable.gota_3_selected)
            binding.btnTypeSel4.setImageResource(R.drawable.gota_4_small)
            binding.btnTypeSel5.setImageResource(R.drawable.gota_5_small)
        }
        binding.btnTypeSel4.setOnClickListener {
            myFontType = 4
            binding.btnTypeSel1.setImageResource(R.drawable.gota_1_small)
            binding.btnTypeSel2.setImageResource(R.drawable.gota_2_small)
            binding.btnTypeSel3.setImageResource(R.drawable.gota_3_small)
            binding.btnTypeSel4.setImageResource(R.drawable.gota_4_selected)
            binding.btnTypeSel5.setImageResource(R.drawable.gota_5_small)
        }
        binding.btnTypeSel5.setOnClickListener {
            myFontType = 5
            binding.btnTypeSel1.setImageResource(R.drawable.gota_1_small)
            binding.btnTypeSel2.setImageResource(R.drawable.gota_2_small)
            binding.btnTypeSel3.setImageResource(R.drawable.gota_3_small)
            binding.btnTypeSel4.setImageResource(R.drawable.gota_4_small)
            binding.btnTypeSel5.setImageResource(R.drawable.gota_5_selected)
        }

        //Botó guardar canvis
        binding.btnSave.setOnClickListener {
            //Save font data
            if (binding.txtNomFont.text.isNotEmpty() && binding.editLon.text.isNotEmpty() && myFontType != 0) {
                var numeric = true
                val parts = binding.editLon.text.toString().split(",")
                val long1 =parts[0].toDouble()
                val lat1 =parts[1].toDouble()
                try {
                    long1
                    lat1
                } catch (e: Exception) {
                    numeric = false
                }
                if (numeric) {
                    var calulatedStringId = oldFontId
                    db.collection("fonts")
                        .get()
                        .addOnSuccessListener { documents ->
                            if (oldFontId == "") {
                                var calculatedId = 0
                                for (document in documents) {
                                    val actualId =
                                        document.get("id").toString().substring(3).toInt()
                                    if (actualId >= calculatedId) {
                                        calculatedId = actualId + 1
                                    }
                                }

                                calulatedStringId = when (calculatedId.toString().length) {
                                    1 ->
                                        "FBO00$calculatedId"
                                    2 ->
                                        "FBO0$calculatedId"
                                    else ->
                                        "FBO$calculatedId"
                                }
                            } else {
                                calulatedStringId = oldFontId
                            }
                            //Delete old image
                            //deleteImageBeforeUploading(oldFontId)
                            if (oldFontId.isEmpty()) {
                                //Upload image
                                pujarImatge(calulatedStringId)
                            }
                            db.collection("fonts").document(calulatedStringId)
                                .set(
                                    hashMapOf(
                                        "lat" to lat1 ,
                                        "lon" to long1,
                                        "id" to calulatedStringId,
                                        "name" to binding.txtNomFont.text.toString(),
                                        "info" to binding.txtInformacio.text.toString(),
                                        "estat" to binding.estatSpinner?.selectedItemPosition,
                                        "address" to binding.editAdreca.text.toString(),
                                        "type" to myFontType
                                    )
                                )
                        }
                    findNavController().navigate(EditFontFragmentDirections.actionEditFontFragmentToAdminFragment())
                } else {
                    showErrorLatLonNotDouble()
                }
            }else{
                showBlankFields()
            }
        }

        descarregarImatgeGlide(oldFontId)

        return binding.root
    }

    /**
     * Funció que mostra error si no s'han completat els camps obligatoris per la creació de la font
     */
    private fun showBlankFields() {
        val objectAlerDialog = AlertDialog.Builder(context)
        objectAlerDialog.setTitle(R.string.error)
        objectAlerDialog.setMessage(R.string.camps_no_omplerts)
        objectAlerDialog.setPositiveButton(R.string.acceptar, null)
        val alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()
    }

    /**
     * Funció que mostra error si no editLat o editLon no son double
     */
    private fun showErrorLatLonNotDouble() {
        val objectAlerDialog = AlertDialog.Builder(context)
        objectAlerDialog.setTitle(R.string.error)
        objectAlerDialog.setMessage(R.string.lat_lon_notDouble)
        objectAlerDialog.setPositiveButton(R.string.acceptar, null)
        val alertDialog: AlertDialog = objectAlerDialog.create()
        alertDialog.show()
    }

    /**
     * Funció que mostra l'imatge de la galeria al imageVIew (imgFont)
     */
    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            binding.imgFont.setImageURI(data)
            hasImage = true
            showImageDelete()
            Snackbar.make(
                binding.frameLayout,
                R.string.imatge_pujada_correctament,
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
            if(oldFontId.isNotEmpty()){
                pujarImatge(oldFontId)
            }
        }
    }

    /**
     * Funció que obre la galeria per poder seleccionar una imatge
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

    /**
     * Funció que puja imatges al Firestore Storage
     */
    private fun pujarImatge(fontId: String?) {
        val imgPath = "images/$fontId.jpg"
        val pathReference = storageRef.child(imgPath)
        try {
            val bitmap: Bitmap? = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = pathReference.putBytes(data)
            uploadTask.addOnFailureListener {
                //Error al pujar
            }.addOnSuccessListener {
                //Pujat correctament
            }
        }catch (e: Exception){

        }
    }

    /**
     * FUnció que descarrega imatges del FIrebase Storage al imageView
     */
    private fun descarregarImatgeGlide(fontId: String?) {
        val imgPath = "images/$fontId.jpg"
        val imageRef = storageRef.child(imgPath)
        imageRef.downloadUrl.addOnSuccessListener { url ->

            Glide.with(this)
                .load(url.toString())
                .centerInside()
                .placeholder((R.drawable.loading))
                .error(R.drawable.ic_noimage)
                .into(imageView)
            hasImage = true
            showImageDelete()

        }.addOnFailureListener {
            binding.imgFont.setImageResource(R.drawable.ic_noimage)
            hasImage = false
            hideImageDelete()
        }
    }

    /**
     * Funció que borra l'imatge de Firebase Storage i posa com a imatge image = no_image al imageView
     */
    private fun deleteImage(fontId: String?) {
        val imgPath = "images/$fontId.jpg"
        val imageRef = storageRef.child(imgPath)
        imageRef.delete().addOnSuccessListener {
            // File deleted successfully
        }.addOnFailureListener {
            // Filed to remove the image
        }
        binding.imgFont.setImageResource(R.drawable.ic_noimage)
    }

    /**
     * Funció que borra l'imatge de Firebase Storage sense tocar l'imageView
     */
    private fun deleteImageBeforeUploading(fontId: String) {
        val imgPath = "images/$fontId.jpg"
        val imageRef = storageRef.child(imgPath)
        imageRef.delete().addOnSuccessListener {
            // File deleted successfully
        }.addOnFailureListener {
            // Filed to remove the image
        }
    }

    private fun showImageDelete(){
        binding.imgFont.visibility = View.VISIBLE
        binding.btnRemoveImg.visibility = View.VISIBLE
    }

    private fun hideImageDelete(){
        binding.imgFont.visibility = View.INVISIBLE
        binding.btnRemoveImg.visibility = View.INVISIBLE
    }
}
