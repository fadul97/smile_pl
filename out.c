#include <stdio.h>

int main(int argc, char* argv[]){
    int a = 10;
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
        if (var >  a )
    {
        var = 2;
    }
    }
    else
    {
        for (int a = 10; a >= 0; a--) {
        var = 3;
    }
    }
    printf("oi\n");
    printf("%d", var);
    printf("\nx = ");
    printf("%d", x);
    printf("\n");
    printf("%s", _var);
    for (int b = 0; b <= 100; b++) {
        var = 3;
    }
    for (int c = 10; c >= 0; c--) {
        var = 3;
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
    while (a )
    {
        var = 3;
    }
    return 0;
}