package com.pcholt.kdocinlineimages.ide

import com.intellij.openapi.util.text.StringUtil
import com.pcholt.kdocinlineimages.core.KdocImageHtmlRewriter
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtDeclaration

class KdocImageHtmlRenderer(private val htmlRewriter: KdocImageHtmlRewriter) {

    fun containsImages(kdoc: KDoc): Boolean = htmlRewriter.containsImages(kdoc.text)

    fun renderBodyHtml(kdoc: KDoc): String {
        val builtInHtml = BuiltInKdocRenderer.renderToHtml(kdoc)
        return htmlRewriter.rewrite(kdoc.text, builtInHtml, sourceDirectoryOf(kdoc.containingFile))
    }

    fun renderPopupHtml(declaration: KtDeclaration, kdoc: KDoc): String {
        val signature = declaration.text.removePrefix(kdoc.text).trim().lineSequence().first()
        return "<div class='definition'><pre>${StringUtil.escapeXmlEntities(signature)}</pre></div>" +
            "<div class='content'>${renderBodyHtml(kdoc)}</div>"
    }
}
