package com.pcholt.kdocinlineimages.core

import java.nio.file.Path

class KdocImageHtmlRewriter(private val destinationResolver: ImageDestinationResolver) {

    fun containsImages(kdocText: String): Boolean =
        MarkdownImageParser.parse(kdocText).isNotEmpty()

    fun rewrite(kdocText: String, renderedHtml: String, sourceDirectory: Path?): String {
        val images = MarkdownImageParser.parse(kdocText)
        val brokenImageCount = BROKEN_IMAGE_LINK.findAll(renderedHtml).count()
        if (images.size != brokenImageCount) return renderedHtml

        var nextImageIndex = 0
        return BROKEN_IMAGE_LINK.replace(renderedHtml) { brokenImage ->
            val image = images[nextImageIndex++]
            imageTagOrNull(image, sourceDirectory) ?: brokenImage.value
        }
    }

    private fun imageTagOrNull(image: MarkdownImageReference, sourceDirectory: Path?): String? {
        val fileUrl = destinationResolver.resolveToFileUrlOrNull(image.destination, sourceDirectory)
            ?: return null
        return """<img src="${escapeHtml(fileUrl)}" alt="${escapeHtml(image.altText)}">"""
    }

    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")

    private companion object {
        val BROKEN_IMAGE_LINK = Regex("""!<a\b[^>]*>.*?</a>""", RegexOption.DOT_MATCHES_ALL)
    }
}
