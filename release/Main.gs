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
proc test:
  a = 10
  proc:
    b = 15
    a += b
  end
  \\ in this place no b variable
  println(a) \\ println 25
end
*\
\*
func b1():
    a1()
end

func a2():
  return a3()
end
func a1():
  return a2()
end
func a3():
  a = 20
  return 10+a
end

func main():
  a = a1()+b1()
  a = 15
  \\println(a)
  \\proc:
  \\  a = 10
  \\end
  return a
end
*\
func a():
    a = 15
    return a
end
func main():
    a = 10
    b = 10
    a = a+b
    b = 25
    a = a+b
    return a/a+a()
end