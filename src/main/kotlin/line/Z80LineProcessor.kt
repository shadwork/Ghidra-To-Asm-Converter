package line

import LineProcessorInterface
import StreamProcessor
import WordTypes
import item.Z80ItemProcessor

class Z80LineProcessor : LineProcessorInterface  {
    override fun process(line: String): String {
        val data = line.split(StreamProcessor.delimiterSafe)
        val builder = StringBuilder()

        var prevType = WordTypes.UNDEFINED
        for (item in data) {
            val word = Z80ItemProcessor().detectType(item)
            when (word) {
                WordTypes.RAM_ADDRESS -> {

                }
                WordTypes.HEX_VALUE -> {
                    if(prevType== WordTypes.ASM_COMMAND){
                        builder.append(item)
                    }
                }
                WordTypes.HEX_CROPPED -> {

                }
                WordTypes.RAM_STRUCT -> {

                }
                WordTypes.DATA_INDEX -> {

                }
                WordTypes.DATA_ARRAY -> {

                }
                WordTypes.CHAR_ARRAY -> {
                    builder.append(item)
                }
                WordTypes.COMMENT -> {
                    builder.append(item)
                }
                WordTypes.ASM_COMMAND -> {
                    builder.append(item)
                }
                WordTypes.LABEL -> {
                    builder.append(item)
                }
                WordTypes.ASM_DATA -> {
                    builder.append(item)
                }
                else -> builder.append(item)
            }
            prevType = word
            if (item.isNotEmpty()) {
                builder.append(" ")
            }
        }
        var beforePostProcessing = builder.toString().trim()
        // remove all single char connected to char array like char 'B'
        val matcherOfSingleChar = ("char \'.+\'").toRegex()
        matcherOfSingleChar.findAll(beforePostProcessing).forEach {
            beforePostProcessing = beforePostProcessing.replace(it.value,"")
        }
        // Ugly hacks here
        beforePostProcessing = beforePostProcessing.replace("db db","db")
        // convert addr into dw with label
        beforePostProcessing = beforePostProcessing.replace("addr","dw")
        // char array for fixed length string
        val matcherOfCharArray = ("char\\[(\\d)+\\]").toRegex()
        matcherOfCharArray.findAll(beforePostProcessing).forEach {
            beforePostProcessing = beforePostProcessing.replace(it.value,"db")
        }
        // actually no ds in sjasm for string constant but db
        beforePostProcessing = beforePostProcessing.replace("ds","db")
        // one char hex to zero trailed hex
        "0123456789ABCDEF".forEach {
            beforePostProcessing = beforePostProcessing.replace("db ${it}h","db 0${it}h")
        }
        // processing single words
        val indexDw = beforePostProcessing.indexOf("dw ")
        if(indexDw>=0) {
            val wordData = beforePostProcessing.substring(indexDw+3)
            val valueHex = wordData.replace("0x","").replace("h","")
            try {
                val decimal = Integer.decode("0x" + valueHex)
                val hexFormatted = "%04x".format(decimal)
                beforePostProcessing = "dw 0x${hexFormatted}"
            }catch (e:Exception){

            }
        }

        // remove array index in references like HL=>GAME_STATE_INIT[1]
        val matcherGhidraLinksArrayIndex = ("\\[\\d+\\]").toRegex()
        matcherGhidraLinksArrayIndex.findAll(beforePostProcessing).forEach {
            beforePostProcessing = beforePostProcessing.replace(it.value,"")
        }
        // replace links =>-> into common references
        beforePostProcessing = beforePostProcessing.replace("=>->","=>")
        // smart remove ghidra links
        val matcherGhidraLinks = ("=>[\\w\\+\\.]+").toRegex()
        matcherGhidraLinks.findAll(beforePostProcessing).forEach {
            beforePostProcessing = beforePostProcessing.replace(it.value,"")
        }
        // remove ::links
        val matcherGhidraColon = ("::[\\w\\+]+").toRegex()
        matcherGhidraColon.findAll(beforePostProcessing).forEach {
            beforePostProcessing = beforePostProcessing.replace(it.value,"")
        }
        // fix ROM addresses with EXT_ram_
        beforePostProcessing = beforePostProcessing.replace(" EXT_ram_"," 0x")
        // change unknown bytes into nop
        beforePostProcessing = beforePostProcessing.replace("?? 90h","NOP")
        // add 0x prefix into hex values
        val matcher = ("[,\\s]([0-9A-Fa-f]{2})h").toRegex()
        matcher.findAll(beforePostProcessing).forEach {
            beforePostProcessing = beforePostProcessing.replace(it.value,it.value.first() + "0x" + it.value.dropLast(1).drop(1))
        }
        // extend 16 bit values into zero trailed hex
        val indexOfHex16 = beforePostProcessing.indexOf("X,0x")
        if(indexOfHex16>=0){
            val valueHex = beforePostProcessing.substring(indexOfHex16 + 4).substringBefore(" ")
            val decimal = Integer.decode("0x" + valueHex)
            val hexFormatted = "%04x".format(decimal)
            beforePostProcessing = beforePostProcessing.replace("X,0x" + valueHex,"X,0x" + hexFormatted)
        }

        return beforePostProcessing
    }

    override fun charPerLineLimit(): Int {
        return 80
    }
}