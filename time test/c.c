#include <stdio.h>

int ab(int a, int b) {
  return a + b;
}

int main() {
  int i = 0;
  while (i < 10) {
    if (i == 0) {
      printf("i == 0!\n");
    }
    printf("Hello %d!\n", i);
    i++;
  }
  return 0;
}
