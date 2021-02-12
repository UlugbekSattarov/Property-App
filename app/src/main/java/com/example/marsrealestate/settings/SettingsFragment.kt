package com.example.marsrealestate.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentSettingsBinding
import com.example.marsrealestate.util.PreferenceDarkMode
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent

class SettingsFragment : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = FragmentSettingsBinding.inflate(inflater)
        requireActivity().setupToolbarIfDrawerLayoutPresent(this,binding.toolbar)
        return binding.root
    }



    class PreferenceFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
         override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
             setPreferencesFromResource(R.xml.preferences_screen, rootKey)
         }

        override fun onResume() {
            super.onResume()
            PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this)

        }


        override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
            if (key == resources.getString(R.string.preference_key_dark_theme)) {
                PreferenceDarkMode.setDarkModeFromPreferences(requireContext())
            }
        }
    }
}