package com.pcholt.kdocinlineimages.ide;

import org.jetbrains.kotlin.idea.highlighting.kdoc.KDocRenderer;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;

final class BuiltInKdocRenderer {
    private BuiltInKdocRenderer() {
    }

    static String renderToHtml(KDoc kdoc) {
        StringBuilder html = new StringBuilder();
        KDocRenderer.INSTANCE.renderKDoc(html, kdoc.getDefaultSection(), kdoc.getAllSections());
        return html.toString();
    }
}
