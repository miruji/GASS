# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# gass.util.s
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# desc:
#   print test
# input:
#   no
# return:
#   no
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .data
printTestString:
  .string "test"
.section .text
.global printTest
printTest:
  pushl %ecx
  movl $printTestString, %ecx
  call println
  popl %ecx
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
# input:
#   1
# return:
#   1
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
.section .text
emptyLoop:
  pushl %eax
  pushl %ebx

  movl $0, %eax  # begin
  movl $10, %ebx # end

  emptyLoop_start:
    cmp %ebx, %eax     #
    jge emptyLoop_end  # begin >= end -> exit

    #
    call println

    inc %eax             # ++
    jmp emptyLoop_start  # to start

  emptyLoop_end:
    popl %ebx
    popl %eax
ret
# = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
