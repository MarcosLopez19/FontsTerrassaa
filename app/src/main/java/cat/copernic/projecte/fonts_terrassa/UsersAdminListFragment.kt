package cat.copernic.projecte.fonts_terrassa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cat.copernic.projecte.fonts_terrassa.ViewModel.UsersViewModel
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentUsersAdminListBinding

class UsersAdminListFragment : Fragment() {

    private lateinit var binding: FragmentUsersAdminListBinding
    private val ViewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_users_admin_list, container, false
        )

        //Crida a una funció del ViewModel amb la finalitat d'obtenir la llista d'usuaris.
        ViewModel.getUsers(binding, requireContext())

        return binding.root
    }
}