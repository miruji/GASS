package main

import "fmt"

func ab(a, b int) int {
    return a + b
}

func main() {
    i := 0
    for i < 10 {
        if i == 0 {
            fmt.Println("i == 0!")
        }
        fmt.Printf("Hello %d!\n", i)
        i++
    }
}
