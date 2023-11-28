# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# gass.io.s
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   print new line
# input:
#   no
# return:
#   no
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .data
lns:
  .string "\n"
.section .text
ln:
  pushl %eax
  pushl %ebx
  pushl %ecx
  pushl %edx

  movl $4, %eax
  movl $1, %ebx
  movl $lns, %ecx
  movl $1, %edx
  int $0x80

  popl %edx
  popl %ecx
  popl %ebx
  popl %eax
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   print
# input:
#   %ecx <<- string
# return:
#   no
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .text
.global print
print:
  pushl %eax
  pushl %ebx
  pushl %edx

  movl $4, %eax
  movl $1, %ebx
  call strlen
  int $0x80

  popl %edx
  popl %ebx
  popl %eax
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   print + new line
# input:
#   %ecx <-- string
# return:
#   no
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .text
.global println
println:
  call print
  call ln
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
