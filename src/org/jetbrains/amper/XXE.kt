package org.jetbrains.amper

import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class XXE {
    // The given XML processing code is vulnerable to XXE (XML External Entity) attack
    fun getUsernameFromXml(xml: String): String {
        val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val db: DocumentBuilder = dbf.newDocumentBuilder()
        val inputStream: ByteArrayInputStream = xml.byteInputStream()
        val document: Document = db.parse(inputStream)
        val username: String = document.getElementsByTagName("username").item(0).textContent
        return username
    }

    // Disabled:
    // 1. support for DTD (Document Type Definitions) - the main source of XXE vulnerabilities
    // 2. processing of external general and parametric entities
    // 3. loading of external DTDs
    // 4. XInclude and expansion of entity references
    fun fixedGetUsernameFromXml(xml: String): String {
        val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)

        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false)
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false)

        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

        dbf.isXIncludeAware = false
        dbf.isExpandEntityReferences = false

        val db: DocumentBuilder = dbf.newDocumentBuilder()
        val inputStream: ByteArrayInputStream = xml.byteInputStream()
        val document: Document = db.parse(inputStream)
        val username: String = document.getElementsByTagName("username").item(0).textContent
        return username
    }
}
