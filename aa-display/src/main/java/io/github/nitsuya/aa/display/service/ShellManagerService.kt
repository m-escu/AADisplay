package io.github.nitsuya.aa.display.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import com.topjohnwu.superuser.Shell
import io.github.nitsuya.aa.display.util.AADisplayConfig
import io.github.nitsuya.aa.display.xposed.IShellManager

class ShellManagerService: Service() {
    private lateinit var prefs: SharedPreferences
    private val stub: IShellManager.Stub = object: IShellManager.Stub(){
        override fun createVirtualDisplayBefore(): Boolean = execConfigShell(AADisplayConfig.CreateVirtualDisplayBefore.get(prefs))
        override fun getConfig(): Bundle = Bundle().apply {
            @Suppress("UNCHECKED_CAST")
            prefs.all.forEach { (k, v) -> putString(k, v?.toString()) }
        }
        override fun destroyVirtualDisplayAfter(): Boolean = execConfigShell(AADisplayConfig.DestroyVirtualDisplayAfter.get(prefs))

        private fun execConfigShell(commands: Array<String>): Boolean {
            if(commands.isEmpty()) return true;
            return Shell.getShell().newJob().add(*commands).exec().isSuccess
        }
    }
    override fun onCreate() {
        super.onCreate()
        prefs = this.getSharedPreferences(AADisplayConfig.ConfigName, MODE_PRIVATE)
    }
    override fun onBind(intent: Intent?): IBinder = stub
}