import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StreamProcessorTest {

    @Test
    fun testDryStringWithDelimiter() {
        assertEquals("TEXT_PASS:", StreamProcessor.dryStringWithDelimiter("      TEXT_PASS:     "))
        assertEquals("TEXT_PASS:\"var\"", StreamProcessor.dryStringWithDelimiter("      TEXT_PASS:\"var\""))
        assertEquals("TEXT_PASS:\" \"", StreamProcessor.dryStringWithDelimiter("TEXT_PASS:\" \""))
        assertEquals("TEXT_PASS:\"     \"", StreamProcessor.dryStringWithDelimiter("TEXT_PASS:\"     \""))
        assertEquals("\" \"\" \"\"   \"", StreamProcessor.dryStringWithDelimiter("\" \"\" \"\"   \""))
    }
}