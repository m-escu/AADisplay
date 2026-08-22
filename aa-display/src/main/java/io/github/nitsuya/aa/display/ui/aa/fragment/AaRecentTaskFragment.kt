package io.github.nitsuya.aa.display.ui.aa.fragment

import android.content.pm.LauncherApps
import android.os.Process
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.duzhaokun123.template.bases.BaseFragment
import io.github.nitsuya.aa.display.CoreApi
import io.github.nitsuya.aa.display.R
import io.github.nitsuya.aa.display.databinding.FragmentAaRecentTaskBinding
import io.github.nitsuya.aa.display.ui.aa.AaDisplayActivityKt
import io.github.nitsuya.aa.display.ui.window.DisplayRecyclerViewAdapter
import io.github.nitsuya.template.bases.runIO
import io.github.nitsuya.template.bases.runMain
import kotlin.math.abs

class AaRecentTaskFragment: BaseFragment<FragmentAaRecentTaskBinding>(FragmentAaRecentTaskBinding::class.java){
    companion object {
        const val TAG = "AADisplay_AaRecentTaskFragment"
    }

    override fun initViews() {
        baseBinding.btnHome.setOnClickListener {
            CoreApi.startLauncher()
            AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
        }
        baseBinding.btnClose.setOnClickListener {
            AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
        }
        baseBinding.rvAllApps.apply {
            layoutManager = GridLayoutManager(context, 6)
            adapter = AllAppsAdapter()
        }
        baseBinding.rvRecentTaskLeft.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = DisplayRecyclerViewAdapter(this){
                AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
            }
        }
        baseBinding.rvRecentTaskRight.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = DisplayRecyclerViewAdapter(this){
                AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
            }.apply {
                otherAdapter = (baseBinding.rvRecentTaskLeft.adapter as DisplayRecyclerViewAdapter).also {
                    it.otherAdapter = this@apply
                }
            }
        }

        arrayOf(baseBinding.rvRecentTaskLeft, baseBinding.rvRecentTaskRight).forEach {
            it.setOnTouchListener { v, event ->
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.setTag(R.id.drag_last_x, event.x)
                        v.setTag(R.id.drag_last_y, event.y)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (v.id != 0
                            && abs((v.getTag(R.id.drag_last_x) as? Float ?: 0f) - event.x) <= 5
                            && abs((v.getTag(R.id.drag_last_y) as? Float ?: 0f) - event.y) <= 5) {
                            AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
                        }
                    }
                }
                return@setOnTouchListener false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        runIO {
            CoreApi.recentTask?.also { recentTask ->
                runMain {
                    (baseBinding.rvRecentTaskLeft.adapter as DisplayRecyclerViewAdapter)?.setItems(recentTask.virtualDisplay)
                    (baseBinding.rvRecentTaskRight.adapter as DisplayRecyclerViewAdapter)?.setItems(recentTask.mainDisplay)
                }
            }
            runMain {
                (baseBinding.rvAllApps.adapter as AllAppsAdapter)?.refresh()
            }
        }
    }

    private inner class AllAppsAdapter : RecyclerView.Adapter<AllAppsAdapter.ViewHolder>() {
        private val apps = mutableListOf<android.content.pm.LauncherApps.ActivityInfo>()

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(android.R.id.icon)
            val text: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val ctx = parent.context
            val view = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                val pad = (4 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
                addView(ImageView(ctx).apply {
                    id = android.R.id.icon
                    layoutParams = LinearLayout.LayoutParams(
                        (48 * resources.displayMetrics.density).toInt(),
                        (48 * resources.displayMetrics.density).toInt()
                    )
                })
                addView(TextView(ctx).apply {
                    id = android.R.id.text1
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setTextColor(android.graphics.Color.WHITE)
                    textSize = 11f
                    maxLines = 1
                })
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val activity = apps[position]
            runCatching { holder.icon.setImageDrawable(activity.getIcon(0)) }
            holder.text.text = activity.label
            holder.itemView.setOnClickListener {
                CoreApi.startActivity(activity.componentName.packageName, 0)
                AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
            }
        }

        override fun getItemCount(): Int = apps.size

        fun refresh() {
            apps.clear()
            runCatching {
                val la = requireContext().getSystemService(android.content.pm.LauncherApps::class.java)
                apps.addAll(
                    la.getActivityList(null, Process.myUserHandle())
                        .sortedBy { it.label.toString().lowercase() }
                )
            }
            notifyDataSetChanged()
        }
    }
}
