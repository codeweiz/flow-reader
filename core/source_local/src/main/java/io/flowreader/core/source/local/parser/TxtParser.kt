package io.flowreader.core.source.local.parser

object TxtParser {

    private val chapterPatterns = listOf(
        Regex("""^\s*第[0-9\u4e00-\u9fa5一二三四五六七八九十百千万零]+[章节卷集部篇][\s\u3000]*(.+)?\s*${'$'}"""),
        Regex("""^\s*Chapter\s*\d+[\s\u3000]*(.+)?\s*${'$'}""", RegexOption.IGNORE_CASE),
        Regex("""^\s*\d+[\s\u3000]*[.\uff0e、][\s\u3000]*(.+)?\s*${'$'}""")
    )

    fun parseChapters(content: String): List<Pair<String, String>> {
        val lines = content.lines()
        val chapters = mutableListOf<Triple<Int, String, StringBuilder>>()
        var currentTitle = "引子"
        var currentBuilder = StringBuilder()
        var lineIndex = 0

        for (line in lines) {
            val trimmed = line.trim()
            val matchedTitle = matchChapterTitle(trimmed)
            if (matchedTitle != null && currentBuilder.isNotEmpty()) {
                chapters.add(Triple(lineIndex, currentTitle, currentBuilder))
                currentTitle = matchedTitle
                currentBuilder = StringBuilder()
            }
            currentBuilder.appendLine(line)
            lineIndex++
        }

        if (currentBuilder.isNotEmpty()) {
            chapters.add(Triple(lineIndex, currentTitle, currentBuilder))
        }

        return chapters.map { it.second to it.third.toString() }
    }

    private fun matchChapterTitle(line: String): String? {
        if (line.isBlank()) return null
        for (pattern in chapterPatterns) {
            val match = pattern.find(line)
            if (match != null) {
                val extra = match.groupValues.getOrNull(1)?.trim()
                return if (extra.isNullOrEmpty()) line.trim() else "${match.value.trim()} $extra"
            }
        }
        return null
    }
}
