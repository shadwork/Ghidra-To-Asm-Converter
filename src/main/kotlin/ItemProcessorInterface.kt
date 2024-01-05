interface ItemProcessorInterface {
    fun detectType(word: String): WordTypes
}