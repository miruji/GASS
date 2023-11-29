# gass (GNU Assembler Script)
High-level script language for GAS

## Problems that caused development to be stopped

1. The gass syntax implies the use of implicit data types, which should fall on the shoulders of the language itself, and this creates difficulties when working with GAS
2. It was never decided what gass should do in this case; it should calculate the entire program in advance as it is now and produce only the result, or leave all code blocks on pure GAS. This cannot be solved as long as the language has only one print/println as a usable function, but if there are more functions, the solution will be found intuitively
3. GCC uses GAS, which means that C code will work the same with gass and then has no meaning other than the syntax

> If you think that these problems are a trifle and you are ready to continue development, then the code is at your disposal, and everything that is stated below explains the main idea.

## Introduction

*So, I’ve been thinking about creating a project like this for a long time. The gass concept does not involve the creation or use of machines for processing byte and bit codes or the use of ready-made solutions. It's too dependent and expensive. Additionally, I don't expect any performance gains using this development approach. Besides, C-like languages, and Java in particular, are not suitable for achieving the end goal of the gass language. So I thought it would be a good idea to write a compiler for this language that would produce pure assembly code as output. In my opinion, this is the most productive way to work. In the case of JIT, it would be necessary to run such code when processing rows,
in the case of the latest version of gass, it simply issues the code to GAS. The interpreter in this case should not be implemented. The project involves the creation of a high-level scripting language for compiling and obtaining code in GAS (GNU Assembler).*

Java is used to develop the compiler because it is cross-platform, simple, and better suited for the task. Where possible, comments will be left on the code and the same syntax will be used.
The working process is as follows:
  1. ~~Getting code from a .gs file~~
  2. ~~Tokenization and code parsing~~
  3. ~~Getting AST~~ and optimizing the code
  4. ~~Obtaining gas code from AST~~
  5. ~~Building a program~~ or running part of a program in real time.
     > GAS in this case is an intermediate representation between gass and machine code
  7. * Writing code in gas/C/gass and implementing it in point 4 to get more gass functionality
     > Thus, the initial core of libraries and functionality will be written in GAS, with the possibility of extending the gass language itself. I don't think it's impossible to write anything in GAS, so the plan is to simply add more functionality over time.
  8. * Writing a JIT to run GAS code at runtime -> second alternative way to work gass

Therefore, assistance to the project is welcome; when modifying, take into account the GPL3 license under which both GAS itself and this gass project are distributed. Compiled programs and code created with and for gass are licensed at your discretion.

## Syntax template
Typing: 
> static, loose, implicit

Block structure:
> The structure is built on code blocks. Blocks of code can be functions or procedures. There is no need to specify anything explicitly, just specify the block name. If the block name is not specified, then it is a temporary block
```
main():
  \\ BLOCK
end
```
> Exception: the main function will automatically turn return 0 if return was not specified
```
main:
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
main():
  \\ BLOCK
end

\\ or
main:
  \\ BLOCK
end

\\ in local func & proc -> no parameters
```
> Local or otherwise temporary func or proc are used as areas for stubs, or code for variables, etc. if no need to go beyond a certain section of code
```
test:
  a = :
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
> Local variables will be searched first in the local block, then higher up to the global function itself. The very last instance of search is the parameters of the global function. All new variables will remain only inside the block, and changes will be applied to the found instances
```
test:
  a = 10
  :
    b = 15
    a += b \\ a = 25
  end
  \\ in this place no b variable
  println(a) \\ a = 25
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
> Variables have a name and assigned value on the right
```
a = "Hello!"
a = :
  return 10
end
```
gas code
```
asm:
  # ASM BLOCK
end
```
