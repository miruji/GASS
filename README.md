# gass (GNU Assembler Script)
High-level script language for GAS

## Introduction

So, this is my long-standing idea to write my own programming language. This is probably 5 or 7, I don’t know which attempt. However, most of all I thought about the concept and made repeated attempts to write something working in this regard. The gass concept does not involve the creation or use of byte and bit code machines or ready-made solutions. This is too dependent and expensive. In addition, I do not expect any performance gains using this development approach. During my time on archlinux, I really liked it and I thought it would be cool to have something productive for writing programs. Previously I would have used C++, now I would use Java, however, I don't think they fit my requirements. So I thought it would be a good idea to write a compiler for the language that would produce pure assembly code as output. This is the most productive work option in my opinion. Previously, I made such an attempt and wrote a compiler in Pascal, the language syntax was C-similar and gave nasm code. I got pretty discouraged after one Internet user told me about LLVM, but now I think that my path was really the right one. Over the past few years, I have only strengthened my programming skills. That's why this gass project exists, which involves creating a high-level scripting language for compiling and obtaining code in gas (GNU Assembler).

Java will be used to develop the compiler, since it is cross-platform, low-cost and better suited for this task.
The working process is as follows:
  1. Getting code from a .gs file
  2. Tokenization and code parsing
  3. Getting ast and optimizing the code
  4. Obtaining gas code from ast
  5. Writing code in gas/C/gass and implementing it in point 4 to get more gass functionality

Thus, the initial core of libraries and functionality will be written in gas, with the possibility of expanding the language itself. I don't think it's impossible to write anything in gas, so over time I plan to just add more functionality. However, I don’t have much experience in this at all, so there will be mistakes, there will be a lot of them, but the process will be exciting.

Therefore, assistance to the project is welcome; when modifying, take into account the GPL3 license under which both GAS itself and this gass project are distributed.

## Syntax template
global function
```
\\ in case of implicit type specification for func || proc
\\ compiler will check the expected return and operations that are addressed to the code block name

proc test:
end
\\ or
test:
end
```
global procedure
```
\\ compiler exception: the main function will automatically turn return 0 if return was not specified.

func test:
  return 0;
end
\\ or
test:
  return 0;
end
```
local func & proc rule
```
\\ local or otherwise temporary func || proc are used as areas for stubs, or code for variables, etc. 
\\ if no need to go beyond a certain section of code

proc test:
  a = :
    return 10
  end
  \\ or
  a = func:
    return 10
  end
  \\ or
  a = 10

  b = :
    println(10)
    \* no return and b wait =
       then return exception *\
  end
end
```
local func & proc place
```
proc test:
  a = 10
  proc:
    b = 15
    a += b
  end
  \\ in this place no b variable
  println(a) \\ println 25
end
```
func & proc parameters
```
\\ parameters can be omitted for global func & proc

func main()
\\ or
func main

\\ in local func & proc ->> no parameters
```
func & proc declaration rule
```
global in global ->> none
local in global ->> yes
local in local ->> yes
```
if & switch
```
\\ as before, we use the usual temporary sections of code : end

a = 10
if (a == 10):
end

a = 10
switch (a):
  case 9:
  end
  default:
    \\ go this
  end
end
```
cycles
```
for (i = 0, i < 10, i++):
end

while(true):
end

dowhile(true): \\ template to:do:
end
```
class & struct
```
MyFunction:
  \\ this is func or proc
end

public MyClass:
  \\ this is class
  private test = "test text"
end

private MyClass2:
  \\ this is class
  test = "test text"
end

\*
thus classes are designed for OOP and wrapping global functions and variables into objects.
access is regulated by private and public flags both when declaring a class and internally for specific elements.
*\
```
types & variables
```
\\ all types are expected to be used as in gas

a = "Hello!"
string a = "Hello!"

\\ this should give more control when needed
```
gas code
```
asm:
  # assembler here
end
```
