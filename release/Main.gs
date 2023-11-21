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
func a():
  a = 10+5
  return a+5 \\ <- return 20
end
func main():
  a = 5
  a = func:
    b = a+5
    c = func:
      c = b+5
      c = c+5
      return c+5+a() \\ <- return 45
    end
    return c
  end
  return a-10 \\ <- return 35
end