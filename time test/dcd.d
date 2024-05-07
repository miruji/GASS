import std.stdio;

int ab(int a, int b) {
    return a + b;
}

void main() {
    int i = 0;
    while (i < 10) {
        if (i == 0) {
            writeln("i == 0!");
        }
        writeln("Hello ", i, "!");
        i++;
    }
}
