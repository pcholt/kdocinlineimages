package com.pcholt.kdocinlineimages.core

import java.nio.file.Path

fun interface FileExistenceChecker {
    fun exists(path: Path): Boolean
}
