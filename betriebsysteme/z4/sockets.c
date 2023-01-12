#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/un.h>

void main(){
    
    struct sockaddr_un addr;
    strcpy(addr.sun_path,"mysocket");
    addr.sun_family = AF_UNIX;
    int addrlen = sizeof(addr);
    
    int sk = socket(AF_UNIX, SOCK_STREAM, 0);

    if (sk < 0){
        printf("create failed\n");
        exit(EXIT_FAILURE);
    }

    if (bind(sk, (struct sockaddr*) &addr, addrlen) < 0){
        printf("bind failed\n");
        exit(EXIT_FAILURE);
    }
    

    while(1){
        char buffer[80] = {'\0'};

        if (listen(sk, 3) < 0){
            printf("listen failed\n");
            exit(EXIT_FAILURE);
        };

        int new_sk = accept(sk, (struct sockaddr*)&addr, (socklen_t*) &addrlen);
        if (new_sk < 0){
            printf("accept failed\n");
            exit(EXIT_FAILURE);
        }

        int valread = read(new_sk, buffer, 79);
        printf("%s\n",buffer);
        close(new_sk);
    }
}

