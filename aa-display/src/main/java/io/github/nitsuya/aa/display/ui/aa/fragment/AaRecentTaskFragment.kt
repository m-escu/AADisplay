package io.github.nitsuya.aa.display.ui.aa.fragment

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
    }

    override fun onResume() {
        super.onResume()
        runIO {
            runMain {
                (baseBinding.rvAllApps.adapter as AllAppsAdapter)?.refresh()
            }
        }
    }

    private data class AppItem(val packageName: String, val label: String, val icon: android.graphics.drawable.Drawable?)

    private inner class AllAppsAdapter : RecyclerView.Adapter<AllAppsAdapter.ViewHolder>() {
        private val apps = mutableListOf<AppItem>()

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
            val app = apps[position]
            holder.icon.setImageDrawable(app.icon)
            holder.text.text = app.label
            holder.itemView.setOnClickListener {
                CoreApi.startActivity(app.packageName, 0)
                AaDisplayActivityKt.hideRecentTask(parentFragmentManager)
            }
        }

        override fun getItemCount(): Int = apps.size

        fun refresh() {
            apps.clear()
            runCatching {
                val pm = requireContext().packageManager
                val intent = android.content.Intent(android.content.Intent.ACTION_MAIN)
                    .addCategory(android.content.Intent.CATEGORY_LAUNCHER)
                apps.addAll(
                    pm.queryIntentActivities(intent, 0)
                        .map { AppItem(it.activityInfo.packageName, it.loadLabel(pm).toString(), runCatching { it.activityInfo.loadIcon(pm) }.getOrNull()) }
                        .sortedBy { it.label.lowercase() }
                )
            }
            notifyDataSetChanged()
        }
    }
}
