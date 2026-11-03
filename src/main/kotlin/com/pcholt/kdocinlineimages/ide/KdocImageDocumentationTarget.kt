package com.pcholt.kdocinlineimages.ide

import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationContent
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.SmartPointerManager
import org.jetbrains.kotlin.psi.KtDeclaration

class KdocImageDocumentationTarget(
    private val declaration: KtDeclaration,
    private val htmlRenderer: KdocImageHtmlRenderer,
) : DocumentationTarget {

    override fun createPointer(): Pointer<out DocumentationTarget> {
        val declarationPointer = SmartPointerManager.createPointer(declaration)
        val renderer = htmlRenderer
        return Pointer {
            declarationPointer.element?.let { KdocImageDocumentationTarget(it, renderer) }
        }
    }

    override fun computePresentation(): TargetPresentation =
        TargetPresentation.builder(declaration.name ?: declaration.text.lineSequence().first()).presentation()

    override fun computeDocumentation(): DocumentationResult? {
        val kdoc = declaration.docComment ?: return null
        return DocumentationResult.documentation(
            DocumentationContent.content(htmlRenderer.renderPopupHtml(declaration, kdoc))
        )
    }
}
