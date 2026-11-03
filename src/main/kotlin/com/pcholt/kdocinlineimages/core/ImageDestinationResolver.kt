package com.pcholt.kdocinlineimages.core

import java.net.URI
import java.nio.file.InvalidPathException
import java.nio.file.Path

class ImageDestinationResolver(private val fileExistenceChecker: FileExistenceChecker) {

    fun resolveToFileUrlOrNull(destination: String, sourceDirectory: Path?): String? {
        if (isWebUrl(destination)) return null

        val imagePath = toPathOrNull(destination, sourceDirectory) ?: return null
        if (!fileExistenceChecker.exists(imagePath)) return null

        return imagePath.toUri().toString()
    }

    private fun isWebUrl(destination: String): Boolean =
        destination.startsWith("http://", ignoreCase = true) ||
            destination.startsWith("https://", ignoreCase = true)

    private fun toPathOrNull(destination: String, sourceDirectory: Path?): Path? {
        if (destination.startsWith("file:", ignoreCase = true)) return fileUrlToPathOrNull(destination)

        val path = parsePathOrNull(destination) ?: return null
        if (path.isAbsolute) return path
        return sourceDirectory?.resolve(path)?.normalize()
    }

    private fun fileUrlToPathOrNull(fileUrl: String): Path? =
        runCatching { Path.of(URI(fileUrl)) }.getOrNull()

    private fun parsePathOrNull(text: String): Path? =
        try {
            Path.of(text)
        } catch (invalidPath: InvalidPathException) {
            null
        }
}
