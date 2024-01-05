import item.X86ItemProcessor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Z80ItemProcessorTest {

    private var VALID_RAM_ADDRESS = "ram:0000:26bc"
    private var INVALID_RAM_ADDRESS =arrayOf("ram:00z0:26bc", "ram-00z0:26bc", "ram:00z0a26bc", "rOm:00z0:26bc","ram:0000","ram:0000:26bcaar")

    private var VALID_HEX_VALUE = "e9ed1a"
    private var INVALID_HEX_VALUE = arrayOf("e","e9ed1","e9ed1Z","aa12d","-aa12","add")

    private var VALID_HEX_CROPPED = "2a2a2a202..."
    private var INVALID_HEX_CROPPED = arrayOf("2a2a2a202....","2a2a2a202..","2a2a2a202.")

    private var VALID_RAM_STRUCT = "|_ram:0000:035d"
    private var INVALID_RAM_STRUCT =arrayOf("12ram:00z0:26bc","||_ram:00z0:26bc","_||_raO:00z0:26bc","|_ram:00z0:26vc","|_ram:00z0:26vc323")

    private var VALID_DATA_INDEX = "[1]"
    private var INVALID_DATA_INDEX = arrayOf("[d]","[]","[32","3234]","[[2]]")

    private var VALID_DATA_ARRAY = "db[128]"
    private var INVALID_DATA_ARRAY = arrayOf("1db[128]","[][]db[128]","sdf[23s]")

    private var VALID_COMMENT= ";comment"
    private var INVALID_COMMENT = arrayOf(":")

    private var VALID_ASM_COMMAND= "ADD"
    private var INVALID_ASM_COMMAND = arrayOf("MADD","ADDR","A_DD","%ADA","X~LAT")

    private var VALID_LABEL= "LABEL:"
    private var INVALID_LABEL = arrayOf("LABEL_")

    private var VALID_ASM_DATA= "ds"
    private var INVALID_ASM_DATA = arrayOf("dk")

    private var VALID_ITEMS = arrayOf(VALID_RAM_ADDRESS,VALID_HEX_VALUE,VALID_HEX_CROPPED,VALID_RAM_STRUCT,VALID_DATA_INDEX,VALID_DATA_ARRAY,VALID_COMMENT,VALID_ASM_COMMAND,VALID_ASM_DATA)
    private var INVALID_ITEMS = arrayOf("",*INVALID_RAM_ADDRESS,*INVALID_HEX_VALUE,*INVALID_HEX_CROPPED,*INVALID_RAM_STRUCT,*INVALID_DATA_INDEX,*INVALID_DATA_ARRAY,*INVALID_COMMENT,*INVALID_ASM_COMMAND,*INVALID_LABEL,*INVALID_ASM_DATA)

    private fun validateElement(item:String, type:WordTypes){
        VALID_ITEMS.forEach {
            assertEquals(it == item, X86ItemProcessor().detectType(it) == type)
        }
    }

    @Test
    fun testUndefinedItems() {
        INVALID_ITEMS.forEach {
            assertEquals(WordTypes.UNDEFINED, X86ItemProcessor().detectType(it))
        }
    }

    @Test
    fun testRamAddressValid() {
        validateElement(VALID_RAM_ADDRESS,WordTypes.RAM_ADDRESS)
    }

    @Test
    fun testHexValueValid() {
        validateElement(VALID_HEX_VALUE,WordTypes.HEX_VALUE)
    }

    @Test
    fun testHexCroppedValid() {
        validateElement(VALID_HEX_CROPPED,WordTypes.HEX_CROPPED)
    }

    @Test
    fun testRamStructValid() {
        validateElement(VALID_RAM_STRUCT,WordTypes.RAM_STRUCT)
    }

    @Test
    fun testDataIndexValid() {
        validateElement(VALID_DATA_INDEX,WordTypes.DATA_INDEX)
    }

    @Test
    fun testDataArrayValid() {
        validateElement(VALID_DATA_ARRAY,WordTypes.DATA_ARRAY)
    }

    @Test
    fun testComment() {
        validateElement(VALID_COMMENT,WordTypes.COMMENT)
    }

    @Test
    fun testAsmCommand() {
        validateElement(VALID_ASM_COMMAND,WordTypes.ASM_COMMAND)
    }

    @Test
    fun testLabel() {
        validateElement(VALID_LABEL,WordTypes.LABEL)
    }

    @Test
    fun testAsmData() {
        validateElement(VALID_ASM_DATA,WordTypes.ASM_DATA)
    }
}