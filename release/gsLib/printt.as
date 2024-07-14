# # # # # # # #
# gass.util.s
#
# # # # # # # # #
# Description
#   print test
# Input
#   no
# Output
#   no
# Dependencies
#   printt
.section .data
printtString:
  .string "test"
.section .text
.global printt
printt:
  pushl %ecx
  movl $printtString, %ecx
  call println
  popl %ecx
ret
