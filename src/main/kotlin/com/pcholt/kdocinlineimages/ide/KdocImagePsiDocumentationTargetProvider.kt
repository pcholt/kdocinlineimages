package com.pcholt.kdocinlineimages.ide

import com.pcholt.kdocinlineimages.core.KdocImageHtmlRewriter
import com.intellij.openapi.util.text.StringUtil
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.InlineDocumentationProvider
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtDeclaration

class KdocImagePsiDocumentationTargetProvider(
    private val htmlRewriter: KdocImageHtmlRewriter,
    private val builtInTargetProviders: () -> List<PsiDocumentationTargetProvider>,
    private val builtInInlineProviders: () -> List<InlineDocumentationProvider>,
) : PsiDocumentationTargetProvider {

    constructor() : this(
        createKdocImageHtmlRewriter(),
        ::otherPsiDocumentationTargetProviders,
        ::otherInlineDocumentationProviders,
    )

    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        val declaration = element as? KtDeclaration ?: return null
        val kdoc = declaration.docComment ?: return null
        if (!htmlRewriter.containsImages(kdoc.text)) return null

        val builtInTarget = builtInTargetProviders()
            .firstNotNullOfOrNull { it.documentationTarget(element, originalElement) }
            ?: return null
        val kdocBodyHtml = renderKdocBodyOrNull(declaration, kdoc) ?: return null

        return KdocImageDocumentationTarget(builtInTarget, popupHtml(declaration, kdoc, kdocBodyHtml))
    }

    private fun renderKdocBodyOrNull(declaration: KtDeclaration, kdoc: KDoc): String? {
        val file = declaration.containingFile
        val builtInBodyHtml = builtInInlineProviders()
            .firstNotNullOfOrNull { it.findInlineDocumentation(file, kdoc.textRange) }
            ?.renderText()
            ?: return null
        return htmlRewriter.rewrite(kdoc.text, builtInBodyHtml, sourceDirectoryOf(file))
    }

    private fun popupHtml(declaration: KtDeclaration, kdoc: KDoc, kdocBodyHtml: String): String {
        val signature = declaration.text.removePrefix(kdoc.text).trim().lineSequence().first()
        return "<div class='definition'><pre>${StringUtil.escapeXmlEntities(signature)}</pre></div>" +
            "<div class='content'>$kdocBodyHtml</div>"
    }
}

private fun otherPsiDocumentationTargetProviders(): List<PsiDocumentationTargetProvider> =
    PsiDocumentationTargetProvider.EP_NAME.extensionList.filterNot { it is KdocImagePsiDocumentationTargetProvider }
