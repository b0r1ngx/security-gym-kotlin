package org.jetbrains.amper

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpELTest {
    val spel = SpEL()

    val sum1 = "2 + 2"
    val sum2 = "#{2 + 2}"
    val answer = "4"

    val filePath = "/tmp/hacked"
    val createFile = "T(java.lang.Runtime).getRuntime().exec('touch $filePath')"

    @Test
    fun processStringTest() {
        val result1 = spel.processString(sum1)
        assert(result1 == answer)
    }

    @Test
    fun fixedProcessStringTest() {
        // result1 changes behaviour, bc using TemplateParserContext() at parseExpression() expects template-style expressions
        val result1 = spel.fixedProcessString(sum1)
        val result2 = spel.fixedProcessString(sum2)
        assert(result1 == sum1)
        assert(result2 == answer)
    }

    @Test
    fun createFile() {
        spel.processString(createFile)
        val file = File(filePath)
        assertTrue(file.exists())
        file.delete()
    }

    @Test
    fun fixedCreateFile() {
        spel.fixedProcessString(createFile)
        val file = File(filePath)
        assertFalse(file.exists())
    }
}
