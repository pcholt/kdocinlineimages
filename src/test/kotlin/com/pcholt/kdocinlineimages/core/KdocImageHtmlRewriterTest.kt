package com.pcholt.kdocinlineimages.core

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KdocImageHtmlRewriterTest {

    private val sourceDirectory = Path.of("/project/src")
    private val existingFiles = mutableSetOf<Path>()
    private val rewriter = KdocImageHtmlRewriter(ImageDestinationResolver { it in existingFiles })

    @Test
    fun `replaces the broken image link with an image tag`() {
        existingFiles.add(Path.of("/project/src/logo.png"))

        val html = rewriter.rewrite(
            kdocText = "![the logo](logo.png)",
            renderedHtml = """<p>!<a href="logo.png">the logo</a></p>""",
            sourceDirectory = sourceDirectory,
        )

        assertEquals("""<p><img src="file:///project/src/logo.png" alt="the logo"></p>""", html)
    }

    @Test
    fun `replaces several images in order`() {
        existingFiles.add(Path.of("/project/src/one.png"))
        existingFiles.add(Path.of("/project/src/two.png"))

        val html = rewriter.rewrite(
            kdocText = "![a](one.png) and ![b](two.png)",
            renderedHtml = """!<a href="x">a</a> and !<a href="y">b</a>""",
            sourceDirectory = sourceDirectory,
        )

        assertEquals(
            """<img src="file:///project/src/one.png" alt="a"> and <img src="file:///project/src/two.png" alt="b">""",
            html,
        )
    }

    @Test
    fun `keeps the original link when the image file is missing`() {
        val renderedHtml = """<p>!<a href="gone.png">gone</a></p>"""

        val html = rewriter.rewrite("![gone](gone.png)", renderedHtml, sourceDirectory)

        assertEquals(renderedHtml, html)
    }

    @Test
    fun `replaces only the images that resolve`() {
        existingFiles.add(Path.of("/project/src/there.png"))

        val html = rewriter.rewrite(
            kdocText = "![a](gone.png) ![b](there.png)",
            renderedHtml = """!<a href="x">a</a> !<a href="y">b</a>""",
            sourceDirectory = sourceDirectory,
        )

        assertEquals("""!<a href="x">a</a> <img src="file:///project/src/there.png" alt="b">""", html)
    }

    @Test
    fun `escapes html characters in the alt text`() {
        existingFiles.add(Path.of("/project/src/logo.png"))

        val html = rewriter.rewrite(
            kdocText = "![a \"b\" & <c>](logo.png)",
            renderedHtml = """!<a href="x">a</a>""",
            sourceDirectory = sourceDirectory,
        )

        assertTrue(html.contains("""alt="a &quot;b&quot; &amp; &lt;c&gt;""""))
    }

    @Test
    fun `leaves the html unchanged when the number of broken links differs from the number of images`() {
        existingFiles.add(Path.of("/project/src/logo.png"))
        val renderedHtml = """<p>no broken link here</p>"""

        val html = rewriter.rewrite("![a](logo.png)", renderedHtml, sourceDirectory)

        assertEquals(renderedHtml, html)
    }

    @Test
    fun `does not touch ordinary links`() {
        val renderedHtml = """<p><a href="x">plain link</a></p>"""

        val html = rewriter.rewrite("[plain link](x)", renderedHtml, sourceDirectory)

        assertEquals(renderedHtml, html)
    }

    @Test
    fun `reports that kdoc text with an image contains images`() {
        assertTrue(rewriter.containsImages("Text ![a](logo.png)"))
    }

    @Test
    fun `reports that kdoc text without an image contains no images`() {
        assertFalse(rewriter.containsImages("Text [a](logo.png)"))
    }
}
