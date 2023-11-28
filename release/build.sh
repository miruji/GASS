#!/bin/bash

as --32 -o gass.string.o gass.string.s
as --32 -o gass.io.o gass.io.s
as --32 -o gass.util.o gass.util.s
as --32 -o Main.o Main.s
ld -m elf_i386 -s -o Main Main.o gass.string.o gass.io.o gass.util.o
upx -9 -q -q -q Main