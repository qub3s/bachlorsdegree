# include <stdio.h>
# include <stdlib.h>
# include <signal.h>
# include <sys/types.h>
# include <unistd.h>

void sig_handle(int sig){
    printf("END OF PROGRAM!\n");
    exit(EXIT_SUCCESS);
}

void main(){
    pid_t pidsave = getpid();
    fork();
    pid_t pid = getpid();
    pid_t ppid = getppid();

    signal(SIGINT,sig_handle);
    
    while(1==1){
        if(pid == pidsave){
            printf("pid: %ld \n",pid);
        }
        else{
            printf("pid: %ld - ppid: %ld \n",pid,ppid);
        }
    }
}
