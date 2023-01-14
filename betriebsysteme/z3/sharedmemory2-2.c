#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <string.h>

// argv[1] pid argv[2] shmid argv[3] nameorfileofpath 
void main(int argc, char* argv[]){
    FILE *file;
    file = fopen(argv[3],"r");
    
    int pid = atoi(argv[1]);
    int key = atoi(argv[2]);
    
    char buffer[1025];
    
    int shmid = shmget(IPC_PRIVATE, 1025, 0666);
    printf("%d  \n",shmid);

    if (shmid == -1) {
        printf("error shared memory");
        return;
   }
    
    char* line = (char*) shmat(key, NULL, 0);
    if (line == (void*)-1) {
        printf("error shared memory attach");
        return;
    }
   
    int eof = 1;

    while(eof){
        for(int x = 0; x < 1024; x++){
            char c = fgetc(file);
            if(eof && c != EOF){
                line[x] = c;
            }
            else{
                eof = 0;
                line[x] = 0;
            }
        }
        line[1025] = 1;
        kill(pid, SIGUSR1);
        while(line[1025] == 1){}            // diese part sollte eigentlich mit semaphoren gelöst werden, stand aber nicht in der aufabe
    }
    
    shmctl(shmid,IPC_RMID,NULL);

    fclose(file);
}
