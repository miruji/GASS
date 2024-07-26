.section .data
#a
a:
  .string "10"

# if_0
if_0_1:
  .string "10"

# println_0
println_0_0:
  .string "[1m[48;2;255;0;0mwhite bold text in red bg![m"

# if_1
if_1_0:
  .string "1"

if_1_1:
  .string "10"

# println_1
println_1_0:
  .string "1"

# println_2
println_2_0:
  .string "[1m[48;2;255;255;255m[38;2;255;0;0mred bold text in white bg![m"

.section .text
.globl _start
_start:
  # if_0
  movl $a, %eax
  cmp $if_0_1, %eax
  jne endif_0

    # println_0
    movl $println_0_0, %ecx
    call println

      # if_1
      movl $if_1_0, %eax
      cmp $if_1_1, %eax
      jne endif_1

        # println_1
        movl $println_1_0, %ecx
        call println

      endif_1:
  endif_0:
  # else
    # println_2
    movl $println_2_0, %ecx
    call println

exit:
  movl $1, %eax  # todo: exit func
  xorl %ebx, %ebx  # code 0
  int $0x80
