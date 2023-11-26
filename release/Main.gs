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
func ab(a, b):
  return a+b
end
func ab2(a, b):
  return a-b
end
func main():
  a = ab(10,5)
  a = func:
    return ab2(a,15)
  end
  return a \\ = 0
end