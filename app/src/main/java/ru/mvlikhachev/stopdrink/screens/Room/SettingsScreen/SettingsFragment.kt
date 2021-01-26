package ru.mvlikhachev.stopdrink.screens.Room.SettingsScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_settings.*

import ru.mvlikhachev.stopdrink.R
import ru.mvlikhachev.stopdrink.Utils.APP_ACTIVITY
import ru.mvlikhachev.stopdrink.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mViewModel: SettingsFragmentViewModel

    private lateinit var nameText: String
    private lateinit var aboutText: String
    private lateinit var dateText: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = APP_ACTIVITY?.getPreferences(Context.MODE_PRIVATE) ?: return
        val name = sharedPref.getString("userDataName", "")
        if (name!!.isEmpty()) {
            initialization()
        } else {
            APP_ACTIVITY.mNavController.navigate(R.id.action_settingsFragment_to_mainRoomFragment)
        }

    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(SettingsFragmentViewModel::class.java)


            roomSaveDataButton.setOnClickListener {
                nameText = roomNameTextInput?.text.toString().trim()
                aboutText = roomAboutMeTextInput?.text.toString().trim()
                dateText = roomDrinkDateTextInput?.text.toString().trim()
                if (nameText.isNotEmpty() && aboutText.isNotEmpty() && dateText.isNotEmpty()) {
                    mViewModel.saveData(name = nameText, about = aboutText, date = dateText)
//                    mViewModel.testLoadData()
                    APP_ACTIVITY.mNavController.navigate(R.id.action_settingsFragment_to_mainRoomFragment)
                } else {
                    Toast.makeText(APP_ACTIVITY, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                }

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}