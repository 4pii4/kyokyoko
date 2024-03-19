package me.pie.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.pie.routes.docRoutings
import me.pie.routes.reloadRouting
import me.pie.routes.searchRouting

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                """
                kyokyoko: experimental HentaiVN api in kotlin by pie
                try these:
                /search?query=furina
                /get-{chapters,metadata}?url=36559-doc-truyen-furina-genshin-impact.html
            """.trimIndent()
            )
        }

        searchRouting()
        docRoutings()
        reloadRouting()
    }
}
