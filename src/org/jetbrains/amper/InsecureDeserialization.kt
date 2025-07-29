package org.jetbrains.amper

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Optional

class InsecureDeserialization {
    // The given YAML processing code is vulnerable to Insecure Deserialization in one of the mechanisms used.
    fun loadDataFromYaml(filePath: String): Optional<HashMap<String, Object>> {
        try {
            val yaml = Yaml()
            val file = File(filePath)
            val inputStream: InputStream = FileInputStream(file)
            val data: HashMap<String, Object>? = yaml.load(inputStream)
            return Optional.ofNullable(data)
        } catch (e: Exception) {
            println(e.message)
        }
        return Optional.empty()
    }

    // using SafeConstructor to block parsing into potentially dangerous: custom Java classes, constructors that could execute code, URL, ProcessBuilder, etc...
    fun fixedLoadDataFromYaml(filePath: String): Optional<HashMap<String, Object>> {
        try {
            val constructor = SafeConstructor(LoaderOptions())
            val yaml = Yaml(constructor)
            val file = File(filePath)
            val inputStream: InputStream = FileInputStream(file)
            val data: HashMap<String, Object>? = yaml.load(inputStream)
            return Optional.ofNullable(data)
        } catch (e: Exception) {
            println(e.message)
        }
        return Optional.empty()
    }
}
