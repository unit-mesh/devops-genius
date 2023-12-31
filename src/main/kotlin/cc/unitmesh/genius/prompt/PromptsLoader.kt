package cc.unitmesh.genius.prompt

import java.nio.charset.Charset

class PromptsLoader(prefix: String) {
    private val classLoader: ClassLoader = this.javaClass.classLoader
    private val defaultPrefix: String = prefix.trimEnd('/')

    fun getTemplate(path: String): String {
        val resourceUrl = classLoader.getResource("$defaultPrefix/$path") ?: throw PromptNotFoundError(path)
        val bytes = resourceUrl.readBytes()
        return String(bytes, Charset.forName("UTF-8"))
    }
}

class PromptNotFoundError(path: String) : Exception("Prompt not found at path: $path")
