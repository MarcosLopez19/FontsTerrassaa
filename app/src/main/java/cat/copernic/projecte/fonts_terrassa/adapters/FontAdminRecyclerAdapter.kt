package cat.copernic.projecte.fonts_terrassa.adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.projecte.fonts_terrassa.R
import cat.copernic.projecte.fonts_terrassa.databinding.ItemFontListAdminBinding
import cat.copernic.projecte.fonts_terrassa.models.FontAdmin
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FontAdminRecyclerAdapter(var fontsAdmin: ArrayList<FontAdmin>) :
    RecyclerView.Adapter<FontAdminRecyclerAdapter.ViewHolder>() {
    var context: Context? = null
    private val db = FirebaseFirestore.getInstance()

    /**
     * Constructor de la classe on es passa la font de dades i el context sobre el que es mostrarà.
     */
    fun fontsAdminRecyclerAdapter(fontsList: ArrayList<FontAdmin>, contxt: Context) {
        this.fontsAdmin = fontsList
        this.context = contxt
    }

    /**
     * És l'encarregat de retornar el ViewHolder ja configurat.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            ItemFontListAdminBinding.inflate(
                layoutInflater, parent, false
            )
        )
    }

    /**
     * Aquest mètode s'encarrega de passar els objectes, un a un al ViewHolder personalitzat
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Per a cada "holder" que tenim li posem l'imatge que coicideix amb l'Id de la base de dades
        with(holder) {
            with(fontsAdmin[position]) {
                binding.txtFont.text = this.fontName
                this.fontId?.let { context?.let { it1 -> descarregarImatgeGlide(it1, it) } }
            }
            //Listener Delete Button per borrar la font, on hi borrarerm totes les seves dades de la base de dades.
            holder.itemView.findViewById<ImageView>(R.id.btnDeleteFont).setOnClickListener {
                val objectAlerDialog = AlertDialog.Builder(context)
                objectAlerDialog.setTitle(R.string.atencio)
                objectAlerDialog.setMessage(R.string.eliminar_font)
                objectAlerDialog.setPositiveButton(R.string.acceptar) { dialog, which ->
                    db.collection("fonts").document(fontsAdmin[position].id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                R.string.font_eliminada,
                                Toast.LENGTH_SHORT
                            ).show()
                            fontsAdmin.removeAt(position)
                            notifyDataSetChanged()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context,
                                R.string.error_eliminar_font,
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    fontsAdmin[position].fontId?.let { it1 -> deleteImage(it1) }
                }
                objectAlerDialog.setNegativeButton(R.string.descartar) { dialog, which ->
                    Toast.makeText(context, R.string.font_no_eliminada, Toast.LENGTH_SHORT).show()
                }
                var alertDialog: AlertDialog = objectAlerDialog.create()
                alertDialog.show()
            }
        }
        val item = fontsAdmin[position]
        holder.bind(item)

        //estamblim un listener
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("id_font", fontsAdmin[position].fontId)
            bundle.putSerializable("font_name", fontsAdmin[position].fontName)
            bundle.putSerializable("font_lat", fontsAdmin[position].lat.toString())
            bundle.putSerializable("font_lon", fontsAdmin[position].lon.toString())
            bundle.putSerializable("font_info", fontsAdmin[position].info)
            bundle.putSerializable("font_adreca", fontsAdmin[position].adreca)
            bundle.putSerializable("font_type", fontsAdmin[position].type)
            bundle.putSerializable("estat", fontsAdmin[position].estat)
            holder.itemView.findNavController().navigate(
                R.id.action_fontAdminListFragment_to_editFontFragment2, bundle
            )
        }
    }

    override fun getItemCount(): Int {
        return fontsAdmin.size
    }

    /**
     * Classe interna que ens permet realitzar funcions amb el binding de l'item del holder.
     */
    inner class ViewHolder(val binding: ItemFontListAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(font: FontAdmin) {
            binding.txtFont.text = font.name.trim()

            //Depenent del fontType canviem l'imatge del tipus de font
            when (font.type) {
                1 -> binding.imgFontType.setImageResource(R.drawable.gota_1)
                2 -> binding.imgFontType.setImageResource(R.drawable.gota_2)
                3 -> binding.imgFontType.setImageResource(R.drawable.gota_3)
                4 -> binding.imgFontType.setImageResource(R.drawable.gota_4)
                5 -> binding.imgFontType.setImageResource(R.drawable.gota_5)
            }
        }

        /**
         * Creem l'instancia de l'StorageRef
         */
        private val storageRef: StorageReference = FirebaseStorage.getInstance().reference

        /**
         * Per agafar i descarregar les imatges de FirebaseStorage.
         * Passem com a parametre:
         *      - El context, tipus view
         *      - Id de la font, tipus String
         */
        fun descarregarImatgeGlide(view: Context, fontId: String) {
            val imgPath = "images/" + fontId + ".jpg"
            val imageRef = storageRef.child(imgPath)
            imageRef.downloadUrl.addOnSuccessListener { url ->

                Glide.with(view)
                    .load(url.toString())
                    .centerInside()
                    .error(R.drawable.ic_noimage)
                    .into(binding.imgFontItem)

            }.addOnFailureListener {
                binding.imgFontItem.setImageResource(R.drawable.ic_noimage)
            }
        }

        /**
         * Per borrar imatges de FirebaseStorage.
         * Passem com a parametre
         *      - Id de la font, tipus String
         */
        fun deleteImage(fontId: String) {
            val imgPath = "images/" + fontId + ".jpg"
            val imageRef = storageRef.child(imgPath)
            imageRef.delete().addOnSuccessListener {
                // File deleted successfully
            }.addOnFailureListener {
                // Filed to remove the image
            }
        }
    }
}