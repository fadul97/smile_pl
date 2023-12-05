#include <stdio.h>

int main(int argc, char* argv[]){
    int var = 10;
    int x = var + 10;
    char* _var = "Hello, World!\n";
    if (var <  x )
    {
        int b = 5;
        printf("Valor para b: ");
        scanf("%d", &b);
        printf("%s", _var);
    }
    else if (var >  x )
    {
    }
    else
    {
    }
    printf("oi\n");
    printf("%d", var);
    printf("\nx = ");
    printf("%d", x);
    printf("\n");
    printf("%s", _var);
    for (int a = 0; a <= 100; a++) {
    }
    for (int b = 10; b >= 0; b--) {
    }
    while (x >  10 )
    {
        printf("%s", _var);
        x = x - 5;
    }
    printf("\nx = ");
    printf("%d", x);
    printf("\n");
    printf("Valor para var: ");
    scanf("%d", &var);
    printf("\n");
    return 0;
}