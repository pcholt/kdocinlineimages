package com.pcholt.kdocinlineimages.ide

import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationContent
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget

class KdocImageDocumentationTarget(
    private val builtInTarget: DocumentationTarget,
    private val documentationHtml: String,
) : DocumentationTarget by builtInTarget {

    override fun createPointer(): Pointer<out DocumentationTarget> {
        val builtInPointer = builtInTarget.createPointer()
        return Pointer {
            builtInPointer.dereference()?.let { KdocImageDocumentationTarget(it, documentationHtml) }
        }
    }

    override fun computeDocumentation(): DocumentationResult =
        DocumentationResult.documentation(DocumentationContent.content(documentationHtml))
}
