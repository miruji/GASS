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
func main():
  a = 10/10 \\ <- 1
  a = func:
    a = a*10 \\ <- 10
    a = a+10
    a = a+10
    return a \\ <- 30
  end
  return a \\ <- 30
end