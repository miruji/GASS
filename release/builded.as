.section .data
# println_0
println_0_0:
  .string "[1m[48;2;255;0;0mwhite bold text in red bg![m"

# println_1
println_1_0:
  .string "1"

# println_2
println_2_0:
  .string "[1m[48;2;255;255;255m[38;2;255;0;0mred bold text in white bg![m"

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

exit:
  movl $1, %eax  # todo: exit func
  xorl %ebx, %ebx  # code 0
  int $0x80
