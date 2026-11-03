package com.pcholt.kdocinlineimages.core

object MarkdownImageParser {

    private val IMAGE_SYNTAX = Regex(
        """!\[([^\]]*)]\(\s*(?:<([^>]*)>|([^)\s]*))(?:\s+(?:"[^"]*"|'[^']*'))?\s*\)"""
    )

    fun parse(text: String): List<MarkdownImageReference> =
        IMAGE_SYNTAX.findAll(text).map(::toReference).toList()

    private fun toReference(match: MatchResult): MarkdownImageReference {
        val (altText, angleBracketDestination, plainDestination) = match.destructured
        return MarkdownImageReference(
            altText = altText,
            destination = angleBracketDestination.ifEmpty { plainDestination },
        )
    }
}
