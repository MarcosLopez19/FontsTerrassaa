package cat.copernic.projecte.fonts_terrassa.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.projecte.fonts_terrassa.adapters.UsersRecyclerAdapter
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentUsersAdminListBinding
import cat.copernic.projecte.fonts_terrassa.models.User
import com.google.firebase.firestore.FirebaseFirestore

class UsersViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var users: ArrayList<User> = arrayListOf()
    private var usersLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private val myAdapter: UsersRecyclerAdapter = UsersRecyclerAdapter()

    /**
     * Funció encarregada d'obtenir desde Firebase la llista d'usuaris registrats.
     */
    fun getUsers(
        binding: FragmentUsersAdminListBinding,
        context: Context
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    users.add(
                        User(
                            document.get("email").toString()
                        )
                    )
                }
                usersLiveData.value = users
                binding.rvUsers.setHasFixedSize(true)
                binding.rvUsers.layoutManager = LinearLayoutManager(context)
                context.let { myAdapter.UsersRecyclerAdapter(users, it) }
                binding.rvUsers.adapter = myAdapter
            }
    }
}
