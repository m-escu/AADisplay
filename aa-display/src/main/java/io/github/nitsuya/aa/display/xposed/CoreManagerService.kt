package io.github.nitsuya.aa.display.xposed

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import com.github.kyuubiran.ezxhelper.utils.*
import de.robv.android.xposed.XSharedPreferences
import io.github.nitsuya.aa.display.BuildConfig
import io.github.nitsuya.aa.display.model.RecentTask
import io.github.nitsuya.aa.display.ui.aa.AaVirtualDisplayAdapter
import io.github.nitsuya.aa.display.ui.window.DisplayWindow
import io.github.nitsuya.aa.display.util.AADisplayConfig
import io.github.nitsuya.aa.display.xposed.util.Instances
import io.github.nitsuya.template.bases.runIO
import io.github.nitsuya.template.bases.runMain
import io.github.qauxv.ui.CommonContextWrapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CoreManagerService private constructor(): ICoreManager.Stub() {
    companion object {
        const val TAG = "CoreManagerService"

        val instance: CoreManagerService by lazy {
            CoreManagerService().apply {
                log(TAG, "AADisplay service initialized")
            }
        }

        @SuppressLint("StaticFieldLeak")
        private lateinit var systemContextHost: Context
        var systemContext: Context
            get() = systemContextHost
            set(value) {
                log(TAG, "SystemContext.params is null: ${value.params}")
                systemContextHost = value.createContext(value.params ?: ContextParams.Builder().build())
            }

        val config: XSharedPreferences? by lazy {
            XSharedPreferences(BuildConfig.APPLICATION_ID, AADisplayConfig.ConfigName).let { config ->
                if(!config.file.canRead())
                    null
                else
                    config
            }
        }

        private var mDisplayWindow: DisplayWindow? = null
        private var mAaVirtualDisplayAdapter: AaVirtualDisplayAdapter? = null

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun systemReady() {

            TipUtil.init(systemContext, "[AADisplay] ")
            Instances.init(systemContext)

        }

        fun getDisplayId(): Int{
            return mAaVirtualDisplayAdapter?.mDisplayId ?: Display.INVALID_DISPLAY
        }

        fun getDensityDpi(): Int{
            return mAaVirtualDisplayAdapter?.mDensityDpi ?: 0
        }
    }

    override fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getVersionCode(): Int {
        return BuildConfig.VERSION_CODE
    }

    override fun getUid(): Int {
        return Process.myUid()
    }

    override fun getBuildTime(): Long {
        return BuildConfig.BUILD_TIME
    }

    override fun onCreateDisplay(width: Int, height: Int, densityDpi: Int, listener: IVirtualDisplayCreatedListener){
        runMain {
            mAaVirtualDisplayAdapter?.apply {
                onReconnected(width, height, densityDpi)
                mDisplayWindow?.onResume(width, height)
                listener.onAvailableDisplay(this.mDisplayId, false)
                return@runMain
            }
            log(TAG, "LauncherPackage=${AADisplayConfig.LauncherPackage.get(config)}")
            config?.apply {
                reload()
                log(TAG, "LauncherPackage=${AADisplayConfig.LauncherPackage.get(config)}")
                if (BuildConfig.DEBUG) log(TAG, "config: ${this.all.map { "${it.key}=${it.value}[${it.value?.javaClass?.name}]" }.joinToString() }")
            }
            AaVirtualDisplayAdapter(systemContext, config){
                mAaVirtualDisplayAdapter = this
                onConnected(width, height, densityDpi){ displayId ->
                    listener.onAvailableDisplay(displayId, true)
                }
                mDisplayWindow?.onDestroyPromptly()
                mDisplayWindow = DisplayWindow(CommonContextWrapper.createAppCompatContext(systemContext), this, width, height, densityDpi)
            }
        }
    }

    override fun setDisplaySurface(surface: Surface?){
        runMain {
            mAaVirtualDisplayAdapter?.setSurface(surface)
        }
    }

    override fun onDestroyDisplay(){
        runMain {
            mDisplayWindow?.onDestroy {
                mAaVirtualDisplayAdapter?.onDestroy()
                mDisplayWindow = null
                mAaVirtualDisplayAdapter = null
            }
        }
    }

    override fun startLauncher() {
        runIO {
            mAaVirtualDisplayAdapter?.run {
                startLauncher()
            }
        }
    }

    override fun startActivity(packageName: String, userId: Int) {
        runIO {
            mAaVirtualDisplayAdapter?.run {
                startActivity(packageName, userId)
            }
        }
    }

    override fun startTaskId(taskId: Int, packageName: String, userId: Int) {
        runIO {
            mAaVirtualDisplayAdapter?.startTaskId(taskId, packageName, userId)
        }
    }

    override fun moveTaskId(taskId: Int, isVirtualDisplay: Boolean) {
        runIO {
            mAaVirtualDisplayAdapter?.moveTaskId(taskId, isVirtualDisplay)
        }
    }

    override fun moveTaskToFront(taskId: Int) {
        runIO {
            mAaVirtualDisplayAdapter?.moveTaskToFront(taskId)
        }
    }

    override fun moveSecondTaskToFront() {
        runIO {
            mAaVirtualDisplayAdapter?.moveSecondTaskToFront()
        }
    }

    @SuppressLint("MissingPermission")
    override fun removeTask(taskId: Int){
        runIO {
            mAaVirtualDisplayAdapter?.removeTask(taskId)
        }
    }

    override fun pressKey(action: Int) {
        runIO {
            mAaVirtualDisplayAdapter?.onPressKey(action)
        }
    }

    override fun touch(event: MotionEvent) {
        // A15+: INJECT_EVENTS check uses the binder calling uid, so run as system
        val ident = Binder.clearCallingIdentity()
        try {
            mAaVirtualDisplayAdapter?.onTouch(event)
        } catch (e: Throwable) {
            log(TAG, "touch failed", e)
        } finally {
            Binder.restoreCallingIdentity(ident)
        }
    }


    override fun toggleDisplayPower() {
        runIO {
            mDisplayWindow?.toggleDisplayPower()
        }
    }

    override fun displayPower(displayPower: Boolean) {
        runIO {
            mDisplayWindow?.toggleDisplayPower(displayPower)
        }
    }

    override fun addMirror(surfaceControl: SurfaceControl) {
        runIO {
            mAaVirtualDisplayAdapter?.addMirror(surfaceControl)
        }
    }

    override fun removeMirror(surfaceControl: SurfaceControl){
        runIO {
            mAaVirtualDisplayAdapter?.removeMirror(surfaceControl)
        }
    }

    override fun getRecentTask(): RecentTask {
        return runBlocking(Dispatchers.IO){
            mAaVirtualDisplayAdapter?.getRecentTask() ?: RecentTask(emptyList(), emptyList())
        }
    }

    @SuppressLint("RestrictedApi")
    override fun testCode(action: String){
    }

    override fun toast(msg: String){
        runMain {
            TipUtil.showToast(msg)
        }
    }

    override fun printLog(tag: String, msg: String){
        runIO {
            log(tag, msg)
        }
    }
}