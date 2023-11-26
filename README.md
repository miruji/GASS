# gass (GNU Assembler Script)
High-level script language for GAS

## Introduction

*So, this is my long-standing idea to write my own programming language. This is probably 5 or 7, I don’t know which attempt. However, most of all I thought about the concept and made repeated attempts to write something working in this regard. The gass concept does not involve the creation or use of byte and bit code machines or ready-made solutions. This is too dependent and expensive. In addition, I do not expect any performance gains using this development approach. During my time on archlinux, I really liked it and I thought it would be cool to have something productive for writing programs. Previously I would have used C++, now I would use Java, however, I don't think they fit my requirements. So I thought it would be a good idea to write a compiler for the language that would produce pure assembly code as output. This is the most productive work option in my opinion. Previously, I made such an attempt and wrote a compiler in Pascal, the language syntax was C-similar and gave nasm code. I got pretty discouraged after one Internet user told me about LLVM, but now I think that my path was really the right one. Over the past few years, I have only strengthened my programming skills. That's why this gass project exists, which involves creating a high-level scripting language for compiling and obtaining code in gas (GNU Assembler).*

Java is used to develop the compiler because it is cross-platform, simple, and better suited for the task. Where possible, comments will be left on the code and the same syntax will be used.
The working process is as follows:
  1. Getting code from a .gs file, 
  2. Tokenization and code parsing
  3. Getting AST and optimizing the code
  4. Obtaining gas code from AST
  5. Building a program or running part of a program in real time.
     > GAS in this case is an intermediate representation between gass and machine code
  7. * Writing code in gas/C/gass and implementing it in point 4 to get more gass functionality
     > Thus, the initial core of libraries and functionality will be written in GAS, with the possibility of extending the gass language itself. I don't think it's impossible to write anything in GAS, so the plan is to simply add more functionality over time.
  8. * Writing a JIT to run GAS code at runtime -> second alternative way to work gass

Therefore, assistance to the project is welcome; when modifying, take into account the GPL3 license under which both GAS itself and this gass project are distributed. Compiled programs and code created with and for gass are licensed at your discretion.

## Syntax template
Typing: 
> static, loose, implicit

Block structure:
> The structure is built on blocks of code. Blocks of code can be functions, procedures, or just blocks. Functions are required to return values, procedures do not return values, and blocks of code will determine their type at build time.
```
func main():
  \\ BLOCK
end
```
> In case of implicit type specification for func or proc compiler will check the expected return and operations that  are addressed to the code block name
```
proc test:
  \\ BLOCK
end

\\ or
test:
  \\ BLOCK
end
```
> Exception: the main function will automatically turn return 0 if return was not specified
```
func main:
  \\ BLOCK
  return 0;
end

\\ or
main:
  \\ BLOCK
end
```
> Parameters can be omitted for global func and proc
```
func main():
  \\ BLOCK
end

\\ or
func main:
  \\ BLOCK
end

\\ in local func & proc -> no parameters
```
> Local or otherwise temporary func or proc are used as areas for stubs, or code for variables, etc. if no need to go beyond a certain section of code
```
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
> Local variables will be searched first in the local block, then higher up to the global function itself. The very last instance of search is the parameters of the global function. In addition, all changes will be saved only at the local block level.
```
proc test:
  a = 10
  proc:
    b = 15
    a += b \\ a = 25
  end
  \\ in this place no b variable
  println(a) \\ a = 10
end
```
Block declaration rule:
```
global in global -> none
local in global -> yes
local in local -> yes
```
Other types of blocks:
> loops, classes, enums ...

if & switch
> As before, we use the usual temporary sections of code : end
```
a = 10
if (a == 10):
  \\ BLOCK
end

a = 10
switch (a):
  case 9:
    \\ BLOCK
  end
  default:
    \\ go this
  end
end
```
cycles
```
for (i = 0, i < 10, i++):
 \\ BLOCK
end

while(true):
 \\ BLOCK
end

dowhile(true): \\ template to:do:
 \\ BLOCK
end
```
classes
> Thus classes are designed for OOP and wrapping global functions and variables into objects. Access is regulated by private and public flags both when declaring a class and internally for specific elements
```
MyFunction:
  \\ BLOCK
end

public MyClass:
  \\ BLOCK
  private test = "test text"
end

private MyClass2:
  \\ BLOCK
  test = "test text"
end
```
enum
```
enum testList:
  TEST1 = "123"
  TEST2 = "321"
end
```
variables
> Variables have a name and assigned value on the right.
```
a = "Hello!"
a = func:
  return 10
end
```
gas code
```
asm:
  # ASM BLOCK
end
```
