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
*\
func main():
  a = 2147483647
  println(a)
  a = a-11111
  println(a)
end