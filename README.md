# Ghidra-To-Asm-Converter
Convert result of Ghidra disassembled binary into Assembler format. 
Platform supporting 
 - x86 [nasm](https://www.nasm.us)
 - z80 [sjasm](https://github.com/Konamiman/Sjasm)

Mainly targeted to x86 16 bit real mode code but should works for any x86 binary and even for z80.

You should export disassembled code from Ghidra in Ascii with following options
<figure><img src="images/ghidra_export_options.png"></figure>
Download GhidraToAsmConverter from releases and pass exported file with processor type as arguments
 
- **-i** input file name
- **-o** output file name
- **-t** processor architecture (x86 or z80)

## Usage
```
java -jar GhidraToAsmConverter-1.0-SNAPSHOT.jar -i ghidra.com.txt -o source.com.asm -t x86
```

## Limitations
 - labels for function should add manually
 - structure definition not works correctly
