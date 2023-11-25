\*
  test gas script
*\
\*
public MyClass:
  private test = "test text"
end

enum testList:
    TEST1 = "123"
    TEST2 = "321"
end

func main():
  a = 2147483647
  println(a)
  println(a-11111)
  \\ if return then auto return 0
end
*\
func a(input1):
  return input1*2
end
func main():
  a = a(5)
  b = a(10)
  c = 10
  return (a+b+c)/2 \\ <- return 20
end