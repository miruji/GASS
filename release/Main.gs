\*
  test gas script
*\
ab(a, b):
  return a+b
end
main():
  i = 0
  if (i < 10):
    println("Hello "+ab(i, 10)+"!")
    i++
    \\i = i+1 \\ if it exists, then we take it higher to the end, if not, then we can create
              \\ in this case, you need to remove it so that it does not create i:0
  end
  i = 1
  if (i < 2):
    println("Hello"+i)
  end
end