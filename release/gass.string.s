# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# gass.string.s
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .data
intBuffer:
  .space 10
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   get string length
# input:
#   %ecx <<- string
# return:
#   number ->> %edx
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
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
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   int ->> string
# input:
#   %edx <<- int
# return:
#   string ->> %edi
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
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
  movl $10, %ecx          # %ecx = 10 (основание системы счисления)
  movl %edx, %eax         # %eax = число, которое нужно преобразовать
  movl $0, %ebx           # %ebx = 0 (индекс для массива)
  movb $0, (%edi)         # Устанавливаем нуль-терминатор

  intToString_loop:
    xor %edx, %edx          # reset the remainder of the division
    div %ecx                # divide %eax by 10, result in %eax, remainder in %edx
    add $'0', %dl           # convert the remainder to an ASCII character
    dec %edi                # -- pointer to a string
    movb %dl, (%edi)        # storing a character in a string
    inc %ebx                # ++
    test %eax, %eax         # checking if processing is complete
    jnz intToString_loop

  intToString_done:
    popl %edx
    popl %ecx
    popl %ebx
    popl %eax
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   get need int size
# input:
#   %edx << int
# return:
#   int ->> %edx
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .text
.global intSize
intSize:
  pushl %eax
  pushl %ebx
  pushl %ecx

  movl $10, %ecx      # %ecx = 10 (radix)
  movl %edx, %eax     # %eax = number to be processed
  xorl %ebx, %ebx     # %ebx = digit counter

  intSize_loop:
    xor %edx, %edx      # reset the remainder of the division
    div %ecx            # divide %eax by 10, result in %eax, remainder in %edx
    inc %ebx            # ++
    test %eax, %eax     # checking if processing is complete
    jnz intSize_loop

  intSize_done:
    #imull $4, %ebx, %edx
    movl %ebx, %edx
    popl %ecx
    popl %ebx
    popl %eax
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
