
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

class StreamProcessor(val lineProcessor:LineProcessorInterface,val itemProcessor:ItemProcessorInterface,val streamIn: InputStream, private val streamOut: OutputStream,val echo:Boolean) {

    fun process() {
        val writer = streamOut.bufferedWriter(Charset.defaultCharset())
        val labelLength = 44
        var label = " ".repeat(labelLength)
        streamIn.bufferedReader().forEachLine {
            var line = lineProcessor.process(dryStringWithDelimiter(it))
            if (itemProcessor.detectType(line) == WordTypes.LABEL) {
                label = line
                if (label.length < labelLength) {
                    label += " ".repeat(labelLength - label.length)
                }
            } else {
                line = "$label$line"
                if (line.trim().isNotEmpty()) {
                    if (line.length > lineProcessor.charPerLineLimit()) {
                        if (line.contains("db \"")) {
                            val endLine = line.substring(line.lastIndexOf("\""))
                            if (line.indexOf("db \"") + 4 < line.lastIndexOf("\"")) {
                                val text = line.substring(line.indexOf("db \"") + 4, line.lastIndexOf("\""))
                                val portions = text.chunked(lineProcessor.charPerLineLimit())
                                    portions.forEachIndexed { index, it: String ->
                                        if(index<portions.size-1) {
                                            printEcho(writer, "${label}db \"${it}\"\n") }
                                        else{
                                            printEcho(writer, "${label}db \"${it}${endLine}\n")
                                        }
                                        label = " ".repeat(labelLength)
                                    }
                            } else {
                                printEcho(writer, line)
                                printlnEcho(writer)
                                label = " ".repeat(labelLength)
                            }
                        } else {
                            printEcho(writer, line)
                            printlnEcho(writer)
                            label = " ".repeat(labelLength)
                        }
                    }else{
                        printEcho(writer, line)
                        printlnEcho(writer)
                        label = " ".repeat(labelLength)
                    }
                }
            }
        }
        writer.flush()
    }

     fun printEcho(writer: BufferedWriter, line: String){
        writer.write(line)
        if(echo){
            print(line)
        }
    }

    fun printlnEcho(writer: BufferedWriter){
        writer.newLine()
        if(echo){
            println()
        }
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