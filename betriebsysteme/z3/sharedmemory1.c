#include <stdio.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdlib.h>
#include <unistd.h>

struct shmseg {
   int a;
   int b;
};

void main(){
    
    struct shmseg* fields;
    int shmid = shmget(IPC_PRIVATE,sizeof(struct shmseg),0644|IPC_CREAT);

    if( shmid == -1){
        printf("error shmget");
        exit(EXIT_FAILURE);
    }

    fields = shmat(shmid, NULL, 0);
    
    if (fields == (void *) -1) {
        printf("error shmat");
        exit(EXIT_FAILURE);
    }

    pid_t save = getpid();

    fork();

    pid_t pid = getpid();

    int counter = 0;

    while(1){
        if(save == pid){
            if(fields->a == 1){
                counter = counter + 1;
                fields->b = counter;
                fields->a = 0;
            }
        }
        else{
            if(fields->a == 0){
                printf("%d\n",fields->b);
                fields->a = 1;
            }
        }
    }

    
 



}
