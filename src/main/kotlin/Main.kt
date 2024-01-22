migration import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import item.X86ItemProcessor
import item.Z80ItemProcessor
import line.X86LineProcessor
import line.Z80LineProcessor
import java.io.*
import java.util.*

enum class CPUType {
    X86,
    Z80
}
class ParserArgs: CliktCommand(name = "ghidra2asm",help = "Ghidra to Assembler parser ${BuildConfig.version}") {
    val input by argument().file(mustExist = true)
    val output by argument().file()
    val cpu: String by option("-c", "--cpu").choice(*(CPUType.values().toList().map{it.toString()}.toTypedArray())).prompt("Cpu").help("Target cpu")
    val echo:Boolean by option("-e","--echo").help("Enable echo processing to screen").flag(default = false)

    override fun run() {
        lateinit var inputStream: InputStream
        try {
            inputStream = input.inputStream()
        } catch (ex: Exception) {
            println("Error: Input file can't be opened")
            kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_FILE_NOT_FOUND)
        }

        lateinit var outputStream: OutputStream
        try {
            outputStream = output.outputStream()
        } catch (ex: Exception) {
            println("Error: Output file can't be created")
            kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_FILE_NOT_FOUND)
        }

        lateinit var itemProcessorInterface: ItemProcessorInterface
        lateinit var lineProcessorInterface: LineProcessorInterface


        val targetProcessor = CPUType.valueOf(cpu)

        when (targetProcessor) {
            CPUType.X86 -> {
                itemProcessorInterface = X86ItemProcessor()
                lineProcessorInterface = X86LineProcessor()
            }
            CPUType.Z80 -> {
                itemProcessorInterface = Z80ItemProcessor()
                lineProcessorInterface = Z80LineProcessor()
            }
        }

        val processor = StreamProcessor(lineProcessorInterface,itemProcessorInterface, inputStream, outputStream,echo)
        processor.process()

        inputStream.close()
        outputStream.flush()
        outputStream.close()
        kotlin.system.exitProcess(SystemConstants.SYSTEM_STATUS_CODE_SUCCESS)
    }
}
fun main(args: Array<String>) = ParserArgs().main(args)
object SystemConstants {
    const val SYSTEM_STATUS_CODE_WRONG_CPU = 2
    const val SYSTEM_STATUS_CODE_FILE_NOT_FOUND = 1
    const val SYSTEM_STATUS_CODE_SUCCESS = 0
}