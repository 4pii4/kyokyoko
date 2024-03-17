package me.pie.models

import kotlinx.serialization.Serializable
import me.pie.utils.Utils
import org.jsoup.nodes.Element

@Serializable
class Link(val text: String = "", val url: String = "") {
    constructor(e: Element): this(e.text(), Utils.unlink(e.attr("href")))
}