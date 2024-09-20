import numpy as np 
import seaborn
import matplotlib
import pandas
import sys

def real_test(array):
    n = np.array(array)
    unique, counts = np.unique(n, return_counts=True)
    print(unique[counts == 1])

inp = sys.argv[1:]
int_inp = []
for x in inp:
    int_inp.append(int(x))

real_test(int_inp)
