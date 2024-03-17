package me.pie.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import me.pie.Domain
import me.pie.models.Doc
import me.pie.models.Tag
import me.pie.utils.Utils
import me.pie.utils.defaultHeaders
import me.pie.utils.missing
import me.pie.utils.respondJson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup


fun Route.searchRouting() {
    route("/search") {
        get("{query?}{page?}") {
            val query = call.parameters["query"] ?: return@get call.missing("query")
            val page = Utils.ensureIntKey(call.parameters, "page", 1)


            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://${Domain.getDomain()}/tim-kiem-truyen.html?key=${query}&page=${page}")
                .defaultHeaders()
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body!!.string()
                val parser = Jsoup.parse(body)

                val docs = parser.select("li.item").map {
                    val title = it.selectFirst("div:nth-child(2) > p:nth-child(1) > a:nth-child(1)")!!.text()
                    val cover = it.selectFirst("img")!!.attr("data-src")
                    val tags = it.select("span > a").map { t -> Tag(t.text(), t.attr("title"), Utils.unlink(t.attr("href"))) }
                    val docurl = Utils.unlink(it.selectFirst("div > a")!!.attr("href"))
                    Doc(title, docurl, cover, tags, Domain.getDomain())
                }

                call.respondJson(docs)
            }
        }
    }
}