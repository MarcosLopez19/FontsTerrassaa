package cat.copernic.projecte.fonts_terrassa.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.projecte.fonts_terrassa.R
import cat.copernic.projecte.fonts_terrassa.databinding.ItemFontListBinding
import cat.copernic.projecte.fonts_terrassa.models.Font
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FontRecyclerAdapter(var fonts: ArrayList<Font>) :
    RecyclerView.Adapter<FontRecyclerAdapter.ViewHolder>() {

    var context: Context? = null

    /**
     * Constructor de la classe on es passa la font de dades i el context sobre el que es mostrarà.
     */
    fun fontsRecyclerAdapter(fontsList: ArrayList<Font>, contxt: Context) {
        this.fonts = fontsList
        this.context = contxt
    }

    /**
     * És l'encarregat de retornar el ViewHolder ja configurat.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            ItemFontListBinding.inflate(
                layoutInflater, parent, false
            )
        )
    }

    /**
     * Aquest mètode s'encarrega de passar els objectes, un a un al ViewHolder personalitzat
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            fonts[position]
        )

        //Per a cada "holder" que tenim li posem l'imatge que coicideix amb l'Id de la base de dades
        with(holder) {
            with(fonts[position]) {
                binding.txtFont.text = this.fontName
                this.fontId?.let { context?.let { it1 -> descarregarImatgeGlide(it1, it) } }
            }
        }

        val item = fonts[position]
        holder.bind(item)

        //establim un listener
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("fontId", fonts[position].fontId)
            bundle.putSerializable("font_type", item.fontType)

            holder.itemView.findNavController().navigate(
                R.id.action_fragment_list_to_viewFontFragment, bundle
            )
        }
    }

    override fun getItemCount(): Int {
        return fonts.size
    }

    /**
     * Classe interna que ens permet realitzar funcions amb el binding de l'item del holder.
     */
    inner class ViewHolder(val binding: ItemFontListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //Introduim el nom de la font, carrer i distancia a la cardView que estem
        fun bind(font: Font) {
            binding.txtFont.text = font.name.trim()
            binding.txtCarrer.text = font.adreca.trim()
            binding.txtDistance.text = font.distance.toString() + " km"

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
    }
}