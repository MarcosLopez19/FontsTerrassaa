package cat.copernic.projecte.fonts_terrassa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cat.copernic.projecte.fonts_terrassa.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentAdminBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_admin, container, false
        )

        binding.adminFonts.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToFontAdminListFragment())
        }

        binding.adminUsers.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToUsersAdminListFragment())
        }

        binding.createFonts.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToEditFontFragment())
        }

        binding.createUsers.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToRegisterUserFragment())
        }

        binding.sessionCloseButton.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToLoginFragment())
        }

        return binding.root
    }
}
