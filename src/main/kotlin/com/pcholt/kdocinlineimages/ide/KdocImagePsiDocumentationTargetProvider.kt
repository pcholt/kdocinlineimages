package com.pcholt.kdocinlineimages.ide

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDeclaration

class KdocImagePsiDocumentationTargetProvider(
    private val htmlRenderer: KdocImageHtmlRenderer,
) : PsiDocumentationTargetProvider {

    constructor() : this(KdocImageHtmlRenderer(createKdocImageHtmlRewriter()))

    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        val declaration = element as? KtDeclaration ?: return null
        val kdoc = declaration.docComment ?: return null
        if (!htmlRenderer.containsImages(kdoc)) return null
        return KdocImageDocumentationTarget(declaration, htmlRenderer)
    }
}
