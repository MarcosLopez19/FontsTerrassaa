
package cat.copernic.projecte.fonts_terrassa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cat.copernic.projecte.fonts_terrassa.databinding.ItemUserListBinding
import com.google.firebase.firestore.FirebaseFirestore


class ItemUserList : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: ItemUserListBinding = DataBindingUtil.inflate(
            inflater, R.layout.item_user_list, container, false)

        binding.btnDelete.setOnClickListener{
            db.collection("users").document(binding.txtUser.text.toString()).delete()
        }

        return binding.root
    }
}
