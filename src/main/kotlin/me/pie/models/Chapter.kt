package me.pie.models

import kotlinx.serialization.Serializable

@Serializable
class Chapter(val title: String, val url: String, val time: Long, val domain: String) {
    fun ID(): Int {
        val a = this.url.split('-')
        return a[a.size - 1].toInt()
    }
}