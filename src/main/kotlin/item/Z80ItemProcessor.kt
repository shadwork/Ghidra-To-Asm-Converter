package item

import ItemProcessorInterface
import WordTypes

public class Z80ItemProcessor(): ItemProcessorInterface {

    override fun detectType(word: String): WordTypes {
        return detect(word)
    }

    private fun detect(word: String): WordTypes {
        if (isRamAddress(word)) {
            return WordTypes.RAM_ADDRESS
        }else if (isAsmData(word)) {
            return WordTypes.ASM_DATA
        } else if (isHexValue(word)) {
            return WordTypes.HEX_VALUE
        } else if (isHexValueCropped(word)) {
            return WordTypes.HEX_CROPPED
        } else if (isRamStruct(word)) {
            return WordTypes.RAM_STRUCT
        } else if (isDataIndex(word)) {
            return WordTypes.DATA_INDEX
        } else if (isDataArray(word)) {
            return WordTypes.DATA_ARRAY
        } else if (isCharArray(word)) {
            return WordTypes.CHAR_ARRAY
        } else if (isComment(word)) {
            return WordTypes.COMMENT
        }else if (isAsmCommand(word)) {
            return WordTypes.ASM_COMMAND
        }else if (isLabel(word)) {
            return WordTypes.LABEL
        }
        return WordTypes.UNDEFINED
    }

    private fun isRamAddress(word: String): Boolean {
        val matcher = ("^ram:ram:[0-9A-Fa-f]{4}$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isHexValue(word: String): Boolean {
        val matcher = ("^([0-9A-Fa-f]{2})+$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isHexValueCropped(word: String): Boolean {
        val matcher = ("^[0-9A-Fa-f]{9}(\\.){3}$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isRamStruct(word: String): Boolean {
        val matcher = ("^\\|_ram:ram:[0-9A-Fa-f]{4}$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isDataIndex(word: String): Boolean {
        val matcher = ("^\\[(\\d)+\\]$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isDataArray(word: String): Boolean {
        val matcher = ("^db\\[(\\d)+\\]$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isCharArray(word: String): Boolean {
        val matcher = ("^char\\[(\\d)+\\]$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isComment(word: String): Boolean {
        val matcher = ("^;.*$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isAsmCommand(word: String): Boolean {
        return z80Command.contains(word)
    }

    private fun isLabel(word: String): Boolean {
        val matcher = (".\\w:\$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    private fun isAsmData(word: String): Boolean {
        return asmData.contains(word)
    }

    private fun isStructIndex(word: String): Boolean {
        val matcher = ("^([0-9A-Fa-f]{2})+$").toRegex()
        return matcher.findAll(word).count() > 0
    }

    companion object {
        val z80Command = arrayListOf("ADC","ADD","AND","BIT","CALL","CCF","CP","CPD","CPDR","CPI","CPIR","CPL","DAA","DEC","DI","DJNZ","EI","EX","EXX","HALT","IM","IN","INC","IND","INDR","INI","INIR","JP","JR","LD","LDD","LDDR","LDI","LDIR","MULUB","MULUW","NEG","NOP","OR","OTDR","OTIR","OUT","OUTD","OUTI","POP","PUSH","RES","RET","RETI","RETN","RL","RLA","RLC","RLCA","RLD","RR","RRA","RRC","RRCA","RRD","RST","SBC","SCF","SET","SLA","SRA","SRL","SUB","XOR"
        )

        val asmData = arrayListOf(
            "ds", "db", "dw"
        )
    }

}
