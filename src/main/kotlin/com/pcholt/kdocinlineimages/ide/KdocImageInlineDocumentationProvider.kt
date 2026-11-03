package com.pcholt.kdocinlineimages.ide

import com.pcholt.kdocinlineimages.core.KdocImageHtmlRewriter
import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.documentation.InlineDocumentation
import com.intellij.platform.backend.documentation.InlineDocumentationProvider
import com.intellij.psi.PsiFile

class KdocImageInlineDocumentationProvider(
    private val htmlRewriter: KdocImageHtmlRewriter,
    private val builtInProviders: () -> List<InlineDocumentationProvider>,
) : InlineDocumentationProvider {

    constructor() : this(createKdocImageHtmlRewriter(), ::otherInlineDocumentationProviders)

    override fun inlineDocumentationItems(file: PsiFile?): Collection<InlineDocumentation> {
        file ?: return emptyList()
        return builtInProviders()
            .flatMap { it.inlineDocumentationItems(file) }
            .mapNotNull { withImagesOrNull(it, file) }
    }

    override fun findInlineDocumentation(file: PsiFile, textRange: TextRange): InlineDocumentation? =
        builtInProviders()
            .firstNotNullOfOrNull { it.findInlineDocumentation(file, textRange) }
            ?.let { withImagesOrNull(it, file) }

    private fun withImagesOrNull(item: InlineDocumentation, file: PsiFile): InlineDocumentation? {
        val kdocText = item.documentationRange.substring(file.text)
        if (!htmlRewriter.containsImages(kdocText)) return null

        val renderedHtml = htmlRewriter.rewrite(kdocText, item.renderText().orEmpty(), sourceDirectoryOf(file))
        return InlineDocumentationWithImages(item, renderedHtml)
    }
}

private class InlineDocumentationWithImages(
    delegate: InlineDocumentation,
    private val renderedHtml: String,
) : InlineDocumentation by delegate {
    override fun renderText(): String = renderedHtml
}
