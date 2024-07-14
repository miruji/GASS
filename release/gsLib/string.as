# # # # # # # # #
# gass.string.s
#
.section .data
intBuffer:
  .space 10
# # # # # # # # # # # #
# Description
#   Get string length
# Input
#   %ecx <-- string
# Output
#   number --> %edx
# Dependencies
#   no
.section .text
.global strlen
strlen:
  pushl %eax
  pushl %ecx
  xorl %edx, %edx

  strlen_loop:
    movb (%ecx), %al  # byte to %al
    test %al, %al     # no null ?
    jz strlen_done    # yes -> done

    inc %edx          # circle link
    inc %ecx          # symbol link
    jmp strlen_loop

  strlen_done:
    popl %ecx
    popl %eax
ret
# # # # # # # # # # #
# Description
#   int --> string
# Input
#   %edx <-- int
# Output
#   string --> %edi
# Dependencies
#   intSize()
#   intBuffer
.section .text
.global intToString
intToString:
  pushl %eax
  pushl %ebx
  pushl %ecx
  pushl %edx

  #
  xorl %edi, %edi
  movl %edx, %eax        # save input int

  call intSize
  decl %edx

  movl $intBuffer, %edi  # buffer end link
  addl %edx, %edi        # + intSize len of link

  movl %eax, %edx        # back input int

  #
  movl $10, %ecx   # %ecx = 10 (radix)
  movl %edx, %eax  # %eax = current digit
  movl $0, %ebx    # %ebx = 0 (array index)
  movb $0, (%edi)  # null terminator

  intToStringLoop:
    xor %edx, %edx       # reset the remainder of the division
    div %ecx             # divide %eax by 10, result in %eax, remainder in %edx
    add $'0', %dl        # convert the remainder to an ASCII character
    dec %edi             # -- pointer to a string
    movb %dl, (%edi)     # storing a character in a string
    inc %ebx             # ++
    test %eax, %eax      # checking if processing is complete
    jnz intToStringLoop

  intToStringDone:
    popl %edx
    popl %ecx
    popl %ebx
    popl %eax
ret
# # # # # # # # # # # #
# Description
#   get need int size
# Input
#   %edx <-- int
# Output
#   int --> %edx
# Dependencies
#   no
.section .text
.global intSize
intSize:
  pushl %eax
  pushl %ebx
  pushl %ecx

  movl $10, %ecx   # %ecx = 10 (radix)
  movl %edx, %eax  # %eax = number to be processed
  xorl %ebx, %ebx  # %ebx = digit counter

  intSizeLoop:
    xor %edx, %edx    # reset the remainder of the division
    div %ecx          # divide %eax by 10, result in %eax, remainder in %edx
    inc %ebx          # ++
    test %eax, %eax   # checking if processing is complete
    jnz intSizeLoop

  intSizeDone:
    #imull $4, %ebx, %edx
    movl %ebx, %edx
    popl %ecx
    popl %ebx
    popl %eax
ret
