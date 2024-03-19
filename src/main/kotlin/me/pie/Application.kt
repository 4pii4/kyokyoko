package me.pie

import io.ktor.server.application.*
import me.pie.plugins.configureHTTP
import me.pie.plugins.configureRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Domain.remoteSources.addAll(environment.config.propertyOrNull("kyokyoko.remote-sources")!!.getList())
    Domain.getDomain()

    configureHTTP()
    configureRouting()
}
