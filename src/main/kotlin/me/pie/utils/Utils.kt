package me.pie.utils

import io.ktor.http.*
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat

object Utils {
    val HEADERS = mapOf(
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0",
        "Accept-Language" to "en-US,en;q=0.5",
        "Connection" to "keep-alive",
    )

    val dmyDate = SimpleDateFormat("dd/MM/yy")
    val hmdmyDate = SimpleDateFormat("hh:mm - dd/MM/yy")

    fun ensureIntKey(parameters: Parameters, key: String, default: Int = 1): Int {
        return try {
            parameters[key]!!.toInt()
        } catch (e: Exception) {
            default
        }
    }

    fun unlink(l: String) = l.removePrefix("/").removeSuffix(".html")

    fun getViewCount(element: Element): Int {
        val viewRegex = Regex("^[^0-9.]*(?<view>[0-9.]+).*$")
        val selectorToViews: (Element, Int) -> Int = { e, selector -> viewRegex.find(e.selectFirst("div.left-info:nth-child(2) > div:nth-child(2) > p:nth-child(${selector})")!!.text())!!.groups["view"]!!.value.replace(".", "").toInt() }

        for (i in 5..9) {
            try {
                return selectorToViews(element, i)
            } catch (e: NumberFormatException) {
                continue
            } catch (e: NullPointerException) {
                continue
            }
        }
        return 0
    }
}