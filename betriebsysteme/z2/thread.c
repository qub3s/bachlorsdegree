#include <stdio.h>
#include <pthread.h>
#include<unistd.h>

void *print_hello(void * threadid){
    printf("Hallo von %d\n",threadid);
}

void main(){
    int n = 10;
    pthread_t threads[n];
    for( long i = 0; i < n; i++ ) {
      pthread_create(&threads[i], NULL, print_hello,(void *) i);
    }
    sleep(1);
}
