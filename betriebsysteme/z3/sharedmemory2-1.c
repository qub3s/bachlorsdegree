#include <stdio.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>

char* line;

void handle_sigint(int sig)
{
    for(int x = 0; x < 1024; x++){
        printf("%c",line[x]);
    }
    line[1025] = 0;
    printf("\n");
}

void main(){
    int shmid = -1;
    int key;

    for(int x = 100; shmid < 0; x++){
        shmid = shmget(x,1025,0666|IPC_CREAT);
        key = x;
    }

    signal(SIGUSR1, handle_sigint);
    
    printf("pid:    %d\n",getpid());
    printf("shmid:  %d\n",shmid);

    line = (char *) shmat(shmid, NULL, 0);

    if (line == (void *) -1) {
        printf("error shmat");
        exit(EXIT_FAILURE);
    }

    while(1==1){
        pause();
    }
    shmctl(shmid,IPC_RMID,NULL);
}
