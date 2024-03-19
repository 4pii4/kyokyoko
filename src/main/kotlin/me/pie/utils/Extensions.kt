package me.pie.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Request

fun Request.Builder.addHeaders(headers: Map<String, String>): Request.Builder {
    headers.map { this.header(it.key, it.value) }
    return this
}

fun Request.Builder.defaultHeaders() = this.addHeaders(Utils.HEADERS)

suspend inline fun <reified T> ApplicationCall.respondJson(value: T) {
    this.respondText(Json.encodeToString(value), contentType = ContentType.Application.Json)
}

suspend fun ApplicationCall.missing(m: String) {
    this.respondText("Missing $m", status = HttpStatusCode.BadRequest)
}