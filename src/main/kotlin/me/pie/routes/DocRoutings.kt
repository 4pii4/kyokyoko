package me.pie.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.pie.Domain
import me.pie.models.Doc
import me.pie.utils.Utils
import me.pie.utils.missing
import me.pie.utils.respondJson

fun Route.docRoutings() {
    route("/get-chapters") {
        get("{url?}") {
            val url = call.parameters["url"] ?: return@get call.missing("url")

            call.respondJson(Doc("", url, "", emptyList(), Domain.getDomain()).getChapters())
        }
    }

    route("/get-metadata") {
        get("{url?}") {
            val url = call.parameters["url"] ?: return@get call.missing("url")

            call.respondJson(Doc("", Utils.unlink(url) + ".html", "", emptyList(), Domain.getDomain()).getMetadata())
        }
    }
}