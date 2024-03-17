package me.pie

object Domain {
    private var domain = "hentaivn.tv"
    private val remoteSources = listOf<String>()

    private fun updateDomain() {}

    fun getDomain(): String {
        if (domain.isEmpty())
            updateDomain()
        return domain
    }
}