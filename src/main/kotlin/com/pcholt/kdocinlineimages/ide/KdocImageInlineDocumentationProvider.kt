package com.pcholt.kdocinlineimages.ide

import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.InlineDocumentation
import com.intellij.platform.backend.documentation.InlineDocumentationProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtDeclaration

class KdocImageInlineDocumentationProvider(
    private val htmlRenderer: KdocImageHtmlRenderer,
) : InlineDocumentationProvider {

    constructor() : this(KdocImageHtmlRenderer(createKdocImageHtmlRewriter()))

    override fun inlineDocumentationItems(file: PsiFile?): Collection<InlineDocumentation> =
        PsiTreeUtil.findChildrenOfType(file, KDoc::class.java).mapNotNull(::inlineDocumentationWithImagesOrNull)

    override fun findInlineDocumentation(file: PsiFile, textRange: TextRange): InlineDocumentation? =
        PsiTreeUtil.findChildrenOfType(file, KDoc::class.java)
            .firstOrNull { it.textRange == textRange }
            ?.let(::inlineDocumentationWithImagesOrNull)

    private fun inlineDocumentationWithImagesOrNull(kdoc: KDoc): InlineDocumentation? {
        val declaration = kdoc.owner as? KtDeclaration ?: return null
        if (!htmlRenderer.containsImages(kdoc)) return null
        return KdocImageInlineDocumentation(kdoc, declaration, htmlRenderer)
    }
}

private class KdocImageInlineDocumentation(
    private val kdoc: KDoc,
    private val declaration: KtDeclaration,
    private val htmlRenderer: KdocImageHtmlRenderer,
) : InlineDocumentation {

    override fun getDocumentationRange(): TextRange = kdoc.textRange

    override fun getDocumentationOwnerRange(): TextRange = declaration.textRange

    override fun renderText(): String = htmlRenderer.renderBodyHtml(kdoc)

    override fun getOwnerTarget(): DocumentationTarget = KdocImageDocumentationTarget(declaration, htmlRenderer)
}
