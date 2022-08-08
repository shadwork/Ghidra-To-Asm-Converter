import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

class StreamProcessor(private val streamIn: InputStream, private val streamOut: OutputStream) {

    fun process() {
        val writer = streamOut.bufferedWriter(Charset.defaultCharset())
        val labelLength = 24
        var label = " ".repeat(labelLength)
        streamIn.bufferedReader().forEachLine {
            var line = LineProcessor(dryStringWithDelimiter(it)).process()
            if(ItemProcessor(line).detectType() == WordTypes.LABEL){
                label = line
                if(label.length<labelLength){
                    label += " ".repeat(labelLength - label.length)
                }
            }else{
                line = "$label $line"
                label = " ".repeat(label.length)
                if(line.trim().isNotEmpty()) {
                    writer.write(line)
                    //print(line)
                    writer.newLine()
                    //println()
                }
            }
        }
        writer.flush()
    }

    companion object {
        const val delimiterSafe = '\t'

        fun dryStringWithDelimiter(string: String, delimiter: Char = delimiterSafe):String{
            val builder = StringBuilder()
            var quotesMode = false
            var spacesMode = false
            string.forEach {
                if(it == ' ' || it == '\t'){
                    if(!quotesMode) {
                        spacesMode = true
                    }
                }else if(it == '"'){
                    quotesMode = !quotesMode
                }

                if(spacesMode){
                    if(it != ' ' && it != '\t'){
                        builder.append(delimiter)
                        builder.append(it)
                        spacesMode = false
                    }
                }else{
                    builder.append(it)
                }

            }
            return builder.toString().trim()
        }
    }

}