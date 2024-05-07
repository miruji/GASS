#include <iostream>

int ab(int a, int b) {
    return a + b;
}

int main() {
    int i = 0;
    while (i < 10) {
        if (i == 0) {
            std::cout << "i == 0!" << std::endl;
        }
        std::cout << "Hello " << i << "!" << std::endl;
        i++;
    }
    return 0;
}
