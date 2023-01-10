# include <stdio.h>
# include <stdlib.h>
# include <signal.h>
# include <sys/types.h>
# include <unistd.h>
# include <sys/wait.h>

void sig_handle(int sig){
    printf("END OF PROGRAM!\n");
    exit(EXIT_SUCCESS);
}

void createzombie(){
    int status;
    pid_t pidsave = getpid();
    fork();
    pid_t pid = getpid();
    printf("new zombie\n");

    if(pid == pidsave){
        exit(EXIT_SUCCESS);
    }
    else{
        printf("wait!\n");
        waitpid(-1,&status,0);
        exit(EXIT_SUCCESS);
    }
}

void main(){
    signal(SIGINT,sig_handle);
    
    while(1==1){
        sleep(2);
        
        pid_t pidsave = getpid();
        
        fork();
        
        pid_t pid = getpid();
        pid_t ppid = getppid();
        
        if(pid != pidsave){
            createzombie();
        }
    }
}
