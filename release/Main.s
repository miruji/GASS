.section .data
# println_0
println_0_0:
  .string "Hello, how are you?"
println_0_1:
  .string "10"

# println_1
println_1_0:
  .string "It seems to work ..."

# println_2
println_2_0:
  .string "30"

.section .text
.globl _start
_start:
  # println_0
  movl $println_0_0, %ecx
  call println

  movl $println_0_1, %ecx
  call println

  # println_1
  movl $println_1_0, %ecx
  call println

  # println_2
  movl $println_2_0, %ecx
  call println

  #
  movl $1, %eax
  xorl %ebx, %ebx
  int $0x80
