package com.pcholt.kdocinlineimages.ide

import com.pcholt.kdocinlineimages.core.ImageDestinationResolver
import com.pcholt.kdocinlineimages.core.KdocImageHtmlRewriter
import com.intellij.psi.PsiFile
import java.nio.file.Files
import java.nio.file.Path

fun createKdocImageHtmlRewriter(): KdocImageHtmlRewriter =
    KdocImageHtmlRewriter(ImageDestinationResolver { path -> Files.isRegularFile(path) })

fun sourceDirectoryOf(file: PsiFile): Path? =
    runCatching { file.originalFile.virtualFile?.parent?.toNioPath() }.getOrNull()
