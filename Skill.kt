
package com.setouta.assistant.privacy

enum class Policy { ALLOW, ASK, DENY }

data class Skill(
    val id: String,
    val title: String,
    val description: String,
    var policy: Policy = Policy.ASK
)

object SkillsCatalog {
    val OPEN_APP = "open_app"
    val MAPS_NAV = "maps_nav"
    // You can extend later (call_phone, accessibility_click, ...)

    fun defaults(): List<Skill> = listOf(
        Skill(OPEN_APP, "فتح التطبيقات", "تشغيل أي تطبيق على الهاتف", Policy.ASK),
        Skill(MAPS_NAV, "الملاحة والخرائط", "فتح الخرائط والانتقال إلى موقع", Policy.ALLOW)
    )
}
