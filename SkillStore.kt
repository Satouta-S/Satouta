
package com.setouta.assistant.privacy

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class SkillStore(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("setouta.skills", Context.MODE_PRIVATE)

    fun load(): MutableList<Skill> {
        val json = prefs.getString("skills", null)
        if (json.isNullOrEmpty()) {
            val def = SkillsCatalog.defaults()
            save(def)
            return def.toMutableList()
        }
        val arr = JSONArray(json)
        val list = mutableListOf<Skill>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Skill(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    description = o.getString("description"),
                    policy = Policy.valueOf(o.getString("policy"))
                )
            )
        }
        return list
    }

    fun save(skills: List<Skill>) {
        val arr = JSONArray()
        for (s in skills) {
            val o = JSONObject()
            o.put("id", s.id)
            o.put("title", s.title)
            o.put("description", s.description)
            o.put("policy", s.policy.name)
            arr.put(o)
        }
        prefs.edit().putString("skills", arr.toString()).apply()
    }

    fun getPolicy(id: String): Policy {
        val skills = load()
        return skills.find { it.id == id }?.policy ?: Policy.ASK
    }

    fun setPolicy(id: String, policy: Policy) {
        val skills = load()
        skills.find { it.id == id }?.let { it.policy = policy }
        save(skills)
    }
}
