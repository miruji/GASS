#!/bin/bash

# libs
as --32 -o ./gsLib/string.o ./gsLib/string.as
as --32 -o ./gsLib/io.o     ./gsLib/io.as
as --32 -o ./gsLib/printt.o ./gsLib/printt.as
as --32 -o builded.o builded.as
# build all
ld -m elf_i386 -s -o builded builded.o ./gsLib/string.o ./gsLib/io.o ./gsLib/printt.o

# optimize
strip builded
upx -9 -q -q -q builded

#
echo "asmBuild: Everything is fine"
