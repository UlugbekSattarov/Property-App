package com.example.marsrealestate.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentSettingsBinding
import com.example.marsrealestate.util.helpers.PreferencesHelper
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.transition.MaterialFadeThrough

class SettingsFragment : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentSettingsBinding.inflate(inflater)

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,binding.toolbar)

        return binding.root
    }



    class PreferenceFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)


            findPreference<ListPreference>(
                resources.getString(R.string.preference_key_dark_mode)
            )?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { pref ->
                    pref.entry
                }


            findPreference<ListPreference>(
                resources.getString(R.string.preference_key_font_size)
            )?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { pref ->
                    pref.entry
                }

        }

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


        override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
            if (key == resources.getString(R.string.preference_key_dark_mode)) {
                //No need to call requireActivity().recreate()
                PreferencesHelper.setDarkMode(requireContext())
            }
            else if (key == resources.getString(R.string.preference_key_font_size)) {
                requireActivity().recreate()
            }
        }
    }




}