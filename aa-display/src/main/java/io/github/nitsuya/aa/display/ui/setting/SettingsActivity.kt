package io.github.nitsuya.aa.display.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.topjohnwu.superuser.Shell
import io.github.nitsuya.aa.display.BuildConfig
import io.github.duzhaokun123.template.bases.BaseActivity
import io.github.nitsuya.aa.display.R
import io.github.nitsuya.aa.display.databinding.ActivitySettingsBinding
import io.github.nitsuya.aa.display.util.AADisplayConfig


class SettingsActivity : BaseActivity<ActivitySettingsBinding>(ActivitySettingsBinding::class.java) {
    override fun onStop() {
        super.onStop()
        // module runs as system_server (uid 1000): make prefs readable so XSharedPreferences works
        Shell.cmd(
            "chmod 771 /data/data/${BuildConfig.APPLICATION_ID}/shared_prefs",
            "chmod 664 /data/data/${BuildConfig.APPLICATION_ID}/shared_prefs/${AADisplayConfig.ConfigName}.xml"
        ).submit()
    }

    override fun initViews() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_root, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            requireContext().theme.applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true)
            preferenceManager.apply {
                sharedPreferencesName = AADisplayConfig.ConfigName
                sharedPreferencesMode = MODE_PRIVATE
            }
            setPreferencesFromResource(R.xml.pref_aadisplay_config, rootKey)
        }
    }



}