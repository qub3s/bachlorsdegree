#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

// Anzahl der Threads 10
#define NUMTHRDS 10
// Feldgroesse pro Thread
#define CHUNKSIZE 10000

int *arr; 
long sum=0;
pthread_mutex_t lock;

/*************************************************************/
void *gausswasbetteratthis(void *threadid)
{
  long start = (long)threadid*CHUNKSIZE;
  long end = start+CHUNKSIZE-1;

  for (long i=start; i<=end ; i++){ 
        pthread_mutex_lock(&lock);
        sum += arr[i];
        pthread_mutex_unlock(&lock);
  }

  pthread_exit((void*) 0);
}

/*************************************************************/
int main()
{
    
    pthread_mutex_init(&lock, NULL);
    
    long i;
    void* status;
    pthread_t threads[NUMTHRDS];
    arr = (int*) malloc (CHUNKSIZE*NUMTHRDS*sizeof(int));

    for (i=0; i<CHUNKSIZE*NUMTHRDS; i++)
        arr[i] = i+1;

      //Create 10 Threads for summing up numbers.
    for(i=0; i<NUMTHRDS; i++) 
        pthread_create(&threads[i], NULL, gausswasbetteratthis, (void *)i); 


    for(i=0; i<NUMTHRDS; i++)
        pthread_join(threads[i], &status);

    printf ("Summe der ersten %d Zahlen ist %li\n", NUMTHRDS*CHUNKSIZE, sum);
    free (arr);
    pthread_exit(NULL);
}   
/*************************************************************/
