package org.jetbrains.amper

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class XXETest {
    val xxe = XXE()

    val safeXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <user>
            <username>john_doe</username>
        </user>
    """.trimIndent()

    val maliciousXmlReadPasswdOnUnix = """
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE foo [ <!ENTITY xxe SYSTEM "file:///etc/passwd"> ]>
        <user>
            <username>&xxe;</username>
        </user>
    """.trimIndent()

    @Test
    fun parseSafeXml() {
        val username = xxe.getUsernameFromXml(safeXml)
        assert(username == "john_doe")
    }

    @Test
    fun fixedParseSafeXml() {
        val username = xxe.fixedGetUsernameFromXml(safeXml)
        assert(username == "john_doe")
    }

    @Test
    fun parseMaliciousXml() {
        // On Unix, if the application has permissions, it read the file
        // In either cases, throw some kind of: IOException (if file doesn't exist or no permissions), XXE was attempted;
        val password = xxe.getUsernameFromXml(maliciousXmlReadPasswdOnUnix)
        println(password)
    }

    @Test
    fun fixedParseMaliciousXml() {
        // fixed version throw an exception about: [Fatal Error] DOCTYPE is disallowed
        assertThrows<Exception> {
            xxe.fixedGetUsernameFromXml(maliciousXmlReadPasswdOnUnix)
        }
    }
}
