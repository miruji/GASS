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
  return 20
end
func main():
  c = 10
  c = func:
    a = 5
    return 15+a()+a
  end
  c = c+10
  return c
end