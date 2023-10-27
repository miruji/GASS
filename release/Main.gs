\*
  test gas script
*\
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