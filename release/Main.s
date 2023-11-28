.section .data
# println_0
println_0_0:
  .string "Hello 0"

# println_1
println_1_0:
  .string "i == 0!"

# println_2
println_2_0:
  .string "Hello 1"

# println_3
println_3_0:
  .string "Hello 2"

# println_4
println_4_0:
  .string "Hello 3"

# println_5
println_5_0:
  .string "Hello 4"

# println_6
println_6_0:
  .string "Hello 5"

# println_7
println_7_0:
  .string "Hello 6"

# println_8
println_8_0:
  .string "Hello 7"

# println_9
println_9_0:
  .string "Hello 8"

# println_10
println_10_0:
  .string "Hello 9"

.section .text
.globl _start
_start:
  # println_0
  movl $println_0_0, %ecx
  call println

  # println_1
  movl $println_1_0, %ecx
  call println

  # println_2
  movl $println_2_0, %ecx
  call println

  # println_3
  movl $println_3_0, %ecx
  call println

  # println_4
  movl $println_4_0, %ecx
  call println

  # println_5
  movl $println_5_0, %ecx
  call println

  # println_6
  movl $println_6_0, %ecx
  call println

  # println_7
  movl $println_7_0, %ecx
  call println

  # println_8
  movl $println_8_0, %ecx
  call println

  # println_9
  movl $println_9_0, %ecx
  call println

  # println_10
  movl $println_10_0, %ecx
  call println

  #
  movl $1, %eax
  xorl %ebx, %ebx
  int $0x80
