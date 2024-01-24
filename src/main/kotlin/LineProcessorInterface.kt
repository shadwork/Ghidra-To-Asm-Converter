interface LineProcessorInterface {
    fun process(line: String): String

    fun charPerLineLimit():Int
}