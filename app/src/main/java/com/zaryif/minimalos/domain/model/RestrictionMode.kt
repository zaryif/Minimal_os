package com.zaryif.minimalos.domain.model

enum class RestrictionMode(val title: String, val description: String) {
    MONK("monk mode", "essentials only, grayscale always"),
    FOCUS("focus", "everything, distracting apps on a clock"),
    UNRESTRICTED("unrestricted", "everything, no guardrails")
}
