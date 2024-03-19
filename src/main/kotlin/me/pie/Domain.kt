package me.pie

import me.pie.utils.Utils

object Domain {
    private var domain = ""
    val remoteSources = mutableListOf<String>()

    private fun testRemoteSource(source: String): String {
        var validUrl = ""
        try {
            Utils.logger.info("about to resolve $source")
            Utils.quickRequest(source) { response1 ->
                val newUrl = response1.body!!.string().removePrefix("\n").removeSuffix("\n")
                Utils.quickRequest("https://$newUrl") { response ->
                    if (response.body!!.string().contains("Trang Chủ HentaiVN")) {
                        validUrl = newUrl
                        Utils.logger.info("$source -> $validUrl")
                    }
                }
            }
        } catch (_: Exception) {
        }
        return validUrl
    }

    private fun getFirstValidRemoteSource(): String {
        for (remoteSource in remoteSources) {
            val newDomain = testRemoteSource(remoteSource)
            if (newDomain.isNotEmpty())
                return newDomain
        }
        return ""
    }

    fun updateDomain() {
        this.domain = getFirstValidRemoteSource()
    }

    fun getDomain(): String {
        if (domain.isEmpty())
            updateDomain()
        return domain
    }
}