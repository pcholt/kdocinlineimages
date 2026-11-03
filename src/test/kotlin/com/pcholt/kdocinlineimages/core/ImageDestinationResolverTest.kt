package com.pcholt.kdocinlineimages.core

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageDestinationResolverTest {

    private val sourceDirectory = Path.of("/project/src/main/kotlin")
    private val existingFiles = mutableSetOf<Path>()
    private val resolver = ImageDestinationResolver { path -> path in existingFiles }

    @Test
    fun `resolves a relative destination against the source directory`() {
        existingFiles.add(Path.of("/project/src/main/kotlin/images/logo.png"))

        val url = resolver.resolveToFileUrlOrNull("images/logo.png", sourceDirectory)

        assertEquals("file:///project/src/main/kotlin/images/logo.png", url)
    }

    @Test
    fun `resolves a relative destination that climbs to a parent directory`() {
        existingFiles.add(Path.of("/project/docs/logo.png"))

        val url = resolver.resolveToFileUrlOrNull("../../../docs/logo.png", sourceDirectory)

        assertEquals("file:///project/docs/logo.png", url)
    }

    @Test
    fun `resolves a destination starting with dot slash`() {
        existingFiles.add(Path.of("/project/src/main/kotlin/logo.png"))

        val url = resolver.resolveToFileUrlOrNull("./logo.png", sourceDirectory)

        assertEquals("file:///project/src/main/kotlin/logo.png", url)
    }

    @Test
    fun `resolves an absolute destination without using the source directory`() {
        existingFiles.add(Path.of("/elsewhere/logo.png"))

        val url = resolver.resolveToFileUrlOrNull("/elsewhere/logo.png", sourceDirectory)

        assertEquals("file:///elsewhere/logo.png", url)
    }

    @Test
    fun `resolves a file url destination`() {
        existingFiles.add(Path.of("/elsewhere/logo.png"))

        val url = resolver.resolveToFileUrlOrNull("file:///elsewhere/logo.png", sourceDirectory)

        assertEquals("file:///elsewhere/logo.png", url)
    }

    @Test
    fun `percent encodes spaces in the resulting url`() {
        existingFiles.add(Path.of("/project/src/main/kotlin/my images/logo.png"))

        val url = resolver.resolveToFileUrlOrNull("my images/logo.png", sourceDirectory)

        assertEquals("file:///project/src/main/kotlin/my%20images/logo.png", url)
    }

    @Test
    fun `returns null when the image file does not exist`() {
        assertNull(resolver.resolveToFileUrlOrNull("missing.png", sourceDirectory))
    }

    @Test
    fun `returns null for a relative destination when the source directory is unknown`() {
        existingFiles.add(Path.of("/project/src/main/kotlin/logo.png"))

        assertNull(resolver.resolveToFileUrlOrNull("logo.png", sourceDirectory = null))
    }

    @Test
    fun `returns null for an https destination`() {
        assertNull(resolver.resolveToFileUrlOrNull("https://example.com/logo.png", sourceDirectory))
    }

    @Test
    fun `returns null for an http destination`() {
        assertNull(resolver.resolveToFileUrlOrNull("HTTP://example.com/logo.png", sourceDirectory))
    }

    @Test
    fun `returns null for a destination that is not a valid path`() {
        assertNull(resolver.resolveToFileUrlOrNull("bad\u0000name.png", sourceDirectory))
    }

    @Test
    fun `returns null for a malformed file url`() {
        assertNull(resolver.resolveToFileUrlOrNull("file://[bad", sourceDirectory))
    }
}
