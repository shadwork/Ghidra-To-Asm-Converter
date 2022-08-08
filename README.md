# Ghidra-To-NASM-X86-Converter
Convert result of Ghidra disassembled x86 binary into NASM assembler format. Mainly targetted to 16 bit real mode code but should works for any x86 binary.

You should export disassembled code from Ghidra in Ascii with following options
<figure><img src="images/ghidra_export_options.png"></figure>
Download GhidraToNASMx86Converter from releases and run it with input file exported from Ghidra and output file with some name, for example

java -jar GhidraToNASMx86Converter-1.0-SNAPSHOT.jar -i ghidra.com.txt -o source.com.asm

Limitation of 1.0 release
 - labels for function should add manually
 - structure definition not works correctly
