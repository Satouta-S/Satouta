
package com.setouta.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.setouta.assistant.privacy.Policy
import com.setouta.assistant.privacy.Skill

class SkillsAdapter(
    private val data: List<Skill>,
    private val onChange: (String, Policy) -> Unit
) : RecyclerView.Adapter<SkillsAdapter.VH>() {

    class VH(v: View): RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.skillTitle)
        val desc: TextView = v.findViewById(R.id.skillDesc)
        val allow: RadioButton = v.findViewById(R.id.rbAllow)
        val ask: RadioButton = v.findViewById(R.id.rbAsk)
        val deny: RadioButton = v.findViewById(R.id.rbDeny)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skill, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = data[position]
        holder.title.text = s.title
        holder.desc.text = s.description
        holder.allow.isChecked = s.policy == Policy.ALLOW
        holder.ask.isChecked = s.policy == Policy.ASK
        holder.deny.isChecked = s.policy == Policy.DENY

        fun update(p: Policy) {
            s.policy = p
            onChange(s.id, p)
            notifyItemChanged(position)
        }

        holder.allow.setOnClickListener { update(Policy.ALLOW) }
        holder.ask.setOnClickListener { update(Policy.ASK) }
        holder.deny.setOnClickListener { update(Policy.DENY) }
    }
}
