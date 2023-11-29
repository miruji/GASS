.section .data
# println_0
println_0_0:
  .string "Hello 10"

# println_1
println_1_0:
  .string "Hello1"

.section .text
.globl _start
_start:
  # println_0
  movl $println_0_0, %ecx
  call println

  # println_1
  movl $println_1_0, %ecx
  call println

  #
  movl $1, %eax
  xorl %ebx, %ebx
  int $0x80
