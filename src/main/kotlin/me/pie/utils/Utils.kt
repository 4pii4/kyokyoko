package me.pie.utils

import io.ktor.http.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.logging.ConsoleHandler
import java.util.logging.Logger

object Utils {
    val HEADERS = mapOf(
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0",
        "Accept-Language" to "en-US,en;q=0.5",
        "Connection" to "keep-alive",
    )

    val logger: Logger = Logger.getLogger("kyokyoko")

    val dmyDate = SimpleDateFormat("dd/MM/yy")
    val hmdmyDate = SimpleDateFormat("hh:mm - dd/MM/yy")
    val lastReload = MSTimer()

    init {
        val handler = ConsoleHandler()
        handler.formatter = LogFormatter()
        logger.handlers.forEach { logger.removeHandler(it) }
        logger.addHandler(handler)
        logger.useParentHandlers = false
    }

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

    fun quickRequest(url: String, func: (t: Response) -> Unit) {
        val request = Request.Builder().url(url).defaultHeaders().build()
        OkHttpClient().newCall(request).execute().use(func)
    }
}