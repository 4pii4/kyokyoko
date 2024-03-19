package me.pie.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.pie.Domain
import me.pie.utils.Utils

fun Route.reloadRouting() {
    route("/reload") {
        get {
            if (Utils.lastReload.hasTimePassed(30000)) {
                Domain.updateDomain()
                Utils.lastReload.reset()
                call.respondText("reloaded, new domain = ${Domain.getDomain()}")
            } else {
                call.respondText("reloading too quickly", status = HttpStatusCode.Forbidden)
            }
        }
    }
}