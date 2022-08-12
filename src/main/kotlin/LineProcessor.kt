class LineProcessor(private val line: String) {
    fun process(): String {
        val data = line.split(StreamProcessor.delimiterSafe)
        val builder = StringBuilder()

        for (item in data) {
            val word = ItemProcessor(item)
            when (word.getType()) {
                WordTypes.RAM_ADDRESS -> {

                }
                WordTypes.HEX_VALUE -> {

                }
                WordTypes.HEX_CROPPED -> {

                }
                WordTypes.RAM_STRUCT -> {

                }
                WordTypes.DATA_INDEX -> {

                }
                WordTypes.DATA_ARRAY -> {

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
            if (item.isNotEmpty()) {
                builder.append(" ")
            }
        }
        var beforePostProcessing = builder.toString().trim()
        // Ugly hacks here
        beforePostProcessing = beforePostProcessing.replace("db db","db")
        // no ds in nasm for string
        beforePostProcessing = beforePostProcessing.replace("ds \"","db \"")
        // one char hex to zero trailed hex
        "0123456789ABCDEF".forEach {
            beforePostProcessing = beforePostProcessing.replace("db ${it}h","db 0${it}h")
        }
        // processing single words
        val indexDw = beforePostProcessing.indexOf("dw ")
        if(indexDw>=0) {
            val wordData = beforePostProcessing.substring(indexDw+3)
            val valueHex = wordData.replace("0x","").replace("h","")
            val decimal = Integer.decode("0x" + valueHex)
            val hexFormatted = "%04x".format(decimal)
            beforePostProcessing = "dw 0x${hexFormatted}"
        }
        // ptr is not needed
        beforePostProcessing = beforePostProcessing.replace(" ptr","")
        // .rep should be cutted
        val indexRep = beforePostProcessing.indexOf(".REP")
        if(indexRep>=0) {
            beforePostProcessing = beforePostProcessing.substring(0,indexRep)
        }
        // but rep prefix needed by nasm
        beforePostProcessing = beforePostProcessing.replace("MOVSB","REP MOVSB")
        beforePostProcessing = beforePostProcessing.replace("MOVSW","REP MOVSW")
        // remove ghidra links
        beforePostProcessing = beforePostProcessing.substringBefore("=>")
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
}