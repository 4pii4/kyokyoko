package me.pie.models

import kotlinx.serialization.Serializable
import me.pie.Domain
import me.pie.utils.Utils
import me.pie.utils.defaultHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

@Serializable
class Doc(val name: String, val url: String, val cover: String, val tags: List<Tag>, val domain: String) {
    private val id
        get() = this.url.removePrefix("/").split("-")[0].toInt()

    private val idName // actually idlinkanime
        get() = Utils.unlink(this.url.replace(Regex("^[0-9]*-doc-truyen-"), ""))

    fun getChapters(): List<Chapter> {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://${Domain.getDomain()}/list-showchapter.php?idchapshow=${this.id}&idlinkanime=${this.idName}")
            .defaultHeaders()
            .build()

        client.newCall(request).execute().use { response ->
            val parser = Jsoup.parse(response.body!!.string())
            val tds = parser.select("tr > td")
            val mains = tds.filterIndexed { index, _ -> index % 2 == 0 }
            val metas = tds.filterIndexed { index, _ -> index % 2 == 1 }

            val chapters = (mains zip metas).map { (main, meta) ->
                val date = Utils.dmyDate.parse(meta.text()).time
                val title = main.selectFirst("td > a > h2")!!.text()
                val link = main.selectFirst("td > a")!!.attr("href")
                Chapter(title, link, date, Domain.getDomain())
            }

            return chapters
        }
    }

    fun getMetadata(): DocInfo {
        val listOfLinks: (Element) -> List<Link> = { e -> e.parent()!!.select("a").map { Link(it) } }

        val handlers: Map<String, (DocInfo, Element, Element) -> Unit> = mapOf(
            "Tên Khác" to { di, _, ep -> di.otherNames = ep.select("a").map { it.text() } },
            "Thể Loại" to { di, _, ep -> di.tags = ep.select("a").map { Tag(it.text(), it.attr("title"), Utils.unlink(it.attr("href"))) } },
            "Nhân vật" to { di, e, _ -> di.characters = listOfLinks(e) },
            "Nhóm dịch" to { di, e, _ -> di.translators = listOfLinks(e) },
            "Tác giả" to { di, e, _ -> di.authors = listOfLinks(e) },
            "Thực hiện" to { di, _, ep -> di.uploader = ep.text().removePrefix("Thực hiện: ") },
            "Tình Trạng" to { di, _, ep -> di.status = ep.selectFirst("a")!!.text() },
            "Nội dung" to { di, e, _ ->
                val lst = e.parent()!!.parent()!!.select("p")
                di.desc = lst[lst.indexOfFirst { it.text().startsWith("Nội dung") } + 1].html()
            },
            "Theo dõi tại" to f@{ di, _, ep ->
                val e = ep.selectFirst("a") ?: return@f
                di.followAt = Link(e.text(), Utils.unlink(e.attr("href")))
            },
            "Doujinshi" to f@{ di, e, ep -> di.doujinshi = (ep.selectFirst("a") ?: return@f).text() }
        )

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://${Domain.getDomain()}/${this.url}")
            .defaultHeaders()
            .build()

        client.newCall(request).execute().use { response ->
            val parser = Jsoup.parse(response.body!!.string())
            val pageInfo = parser.selectFirst("div .page-info")!!
            val doc = DocInfo()



            doc.url = "https://${Domain.getDomain()}" + parser.selectFirst("#unzoom > li:nth-child(3) > a:nth-child(1)")!!.attr("href")
            doc.cover = parser.selectFirst(".page-ava > img")!!.attr("src")
            doc.id = parser.selectFirst("#myInputxx")!!.attr("value").toInt()
            doc.name = parser.selectFirst("#unzoom > li:nth-child(3) > a:nth-child(1) > span:nth-child(1)")!!.text().removePrefix("\n").removeSuffix("\n")
            doc.likes = parser.selectFirst("div .but_like")!!.text().toInt()
            doc.dislikes = parser.selectFirst("div .but_unlike")!!.text().toInt()
            doc.lastUpdated = Utils.hmdmyDate.parse(pageInfo.selectFirst("i")!!.text()).time / 1000L
            doc.views = Utils.getViewCount(pageInfo)

            pageInfo.select("p").map { pe ->
                val span = pe.selectFirst("span")

                if (span != null)
                    handlers.forEach { (match, func) ->
                        if (span.text().startsWith(match)) func(doc, span, span.parent()!!)
                    }
            }

            return doc
        }
    }
}