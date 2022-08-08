import kotlinx.cli.*
import java.io.*

fun main(args: Array<String>) {
    println("Ghidra to Nasm parser 1.0")
    println("Convert result of disassembling x86 real mode code into NASM assembler format")
    val parser = ArgParser("")
    val nameInput by parser.option(ArgType.String, shortName = "i", description = "Input file name").required()
    val nameOutput by parser.option(ArgType.String, shortName = "o", description = "Output file name").required()
    parser.parse(args)

    lateinit var inputStream: InputStream
    try {
        inputStream = File(nameInput).inputStream()
    } catch (ex: Exception) {
        println("Error: Input file can't be opened")
        kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_FILE_NOT_FOUND)
    }

    lateinit var outputStream: OutputStream
    try {
        outputStream = File(nameOutput).outputStream()
    } catch (ex: Exception) {
        println("Error: Output file can't be created")
        kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_FILE_NOT_FOUND)
    }

    val processor = StreamProcessor(inputStream, outputStream)
    processor.process()

    inputStream.close()
    outputStream.flush()
    outputStream.close()
    kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_SUCCESS)
}

object SystemConstants{
    const val SYSTEM_STATUS_CODE_FILE_NOT_FOUND = 1
    const val SYSTEM_STATUS_CODE_SUCCESS = 0
}