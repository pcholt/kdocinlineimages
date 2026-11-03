package com.pcholt.kdocinlineimages.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownImageParserTest {

    @Test
    fun `parses alt text and destination of a single image`() {
        val images = MarkdownImageParser.parse("![logo](images/logo.png)")

        assertEquals(listOf(MarkdownImageReference("logo", "images/logo.png")), images)
    }

    @Test
    fun `parses several images in order of appearance`() {
        val images = MarkdownImageParser.parse("![a](one.png) text ![b](two.png)")

        assertEquals(listOf("one.png", "two.png"), images.map { it.destination })
    }

    @Test
    fun `parses an image with an empty alt text`() {
        val images = MarkdownImageParser.parse("![](one.png)")

        assertEquals("", images.single().altText)
    }

    @Test
    fun `ignores an optional double quoted title`() {
        val images = MarkdownImageParser.parse("""![a](one.png "A title")""")

        assertEquals("one.png", images.single().destination)
    }

    @Test
    fun `ignores an optional single quoted title`() {
        val images = MarkdownImageParser.parse("![a](one.png 'A title')")

        assertEquals("one.png", images.single().destination)
    }

    @Test
    fun `parses a destination in angle brackets that contains spaces`() {
        val images = MarkdownImageParser.parse("![a](<my images/one.png>)")

        assertEquals("my images/one.png", images.single().destination)
    }

    @Test
    fun `does not treat an ordinary link as an image`() {
        assertTrue(MarkdownImageParser.parse("[a](one.png)").isEmpty())
    }

    @Test
    fun `finds no images in text without markdown`() {
        assertTrue(MarkdownImageParser.parse("Just words").isEmpty())
    }

    @Test
    fun `finds images that span a kdoc comment with leading asterisks`() {
        val kdocText = "/**\n * Intro.\n *\n * ![a](one.png)\n */"

        assertEquals("one.png", MarkdownImageParser.parse(kdocText).single().destination)
    }
}
