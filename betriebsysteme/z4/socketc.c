#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/un.h>

void main(int argc, char* argv[]){
    
    struct sockaddr_un addr;
    strcpy(addr.sun_path,"mysocket");
    addr.sun_family = AF_UNIX;

    int sk = socket(AF_UNIX, SOCK_STREAM, 0);

    if (sk < 0){
        printf("create failed\n");
        exit(EXIT_FAILURE);
    }

    int clsk = connect(sk, (struct sockaddr*) &addr, sizeof(addr));

    if (clsk < 0){
        printf("connect failed\n");
        exit(EXIT_FAILURE);
    }

    printf("connected\n");
    printf("%s\n",argv[1]);
    send(sk, argv[1], strlen(argv[1]), 0);
    close(clsk);
}
