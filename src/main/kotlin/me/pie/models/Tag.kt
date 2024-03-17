package me.pie.models

import kotlinx.serialization.Serializable

@Serializable
class Tag(val name: String, val desc: String, val link: String)