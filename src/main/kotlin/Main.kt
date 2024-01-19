import item.X86ItemProcessor
import item.Z80ItemProcessor
import kotlinx.cli.*
import line.X86LineProcessor
import line.Z80LineProcessor
import java.io.*
enum class CPUType {
    X86,
    Z80
}

fun main(args: Array<String>) {
    println("Ghidra to Assembler parser 1.1a")
    println("Convert result of Ghidra disassembling into assembler format")
    println("Processors supported: `${CPUType.values().joinToString(",")}`")
    val parser = ArgParser("")
    val nameInput by parser.option(ArgType.String, fullName = "input", shortName = "i", description = "Input file name").required()
    val nameOutput by parser.option(ArgType.String,fullName = "output", shortName = "o", description = "Output file name").required()
    val printEcho by parser.option(ArgType.Boolean,fullName = "echo", shortName = "e", description = "Mirror output text to screen").default(false)
    val targetProcessor by parser.option(
        ArgType.Choice( CPUType.values().toList(),{it}),
        shortName = "c",
        fullName = "cpu",
        description = "Target processor type",
    ).required()
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

    lateinit var itemProcessorInterface: ItemProcessorInterface
    lateinit var lineProcessorInterface: LineProcessorInterface

    when (targetProcessor.toString().lowercase()) {
        CPUType.X86.toString().lowercase() -> {
            itemProcessorInterface = X86ItemProcessor()
            lineProcessorInterface = X86LineProcessor()
        }
        CPUType.Z80.toString().lowercase() -> {
            itemProcessorInterface = Z80ItemProcessor()
            lineProcessorInterface = Z80LineProcessor()
        }
        else -> {
            println("Error: Unknown target processor")
            kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_WRONG_CPU)
        }
    }

    val processor = StreamProcessor(lineProcessorInterface,itemProcessorInterface, inputStream, outputStream,printEcho)
    processor.process()

    inputStream.close()
    outputStream.flush()
    outputStream.close()
    kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_SUCCESS)
}

object SystemConstants {
    const val SYSTEM_STATUS_CODE_WRONG_CPU = 2
    const val SYSTEM_STATUS_CODE_FILE_NOT_FOUND = 1
    const val SYSTEM_STATUS_CODE_SUCCESS = 0
}