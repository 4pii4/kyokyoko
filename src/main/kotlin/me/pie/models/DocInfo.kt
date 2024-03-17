package me.pie.models

import kotlinx.serialization.Serializable

@Serializable
class DocInfo(
    var url: String = "",
    var cover: String = "",
    var id: Int = 0,
    var name: String = "",
    var otherNames: List<String> = emptyList(),
    var views: Int = 0,
    var tags: List<Tag> = emptyList(),
    var translators: List<Link> = emptyList(),
    var authors: List<Link> = emptyList(),
    var characters: List<Link> = emptyList(),
    var doujinshi: String = "",
    var uploader: String = "",
    var status: String = "",
    var desc: String = "",
    var lastUpdated: Long = 0,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var followAt: Link = Link()
)