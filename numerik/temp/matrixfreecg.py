import numpy as np
from numpy.random import rand
from numpy.linalg import norm
from time import perf_counter as tcount

# Aufgabe b
def calc(mat,x):
    matrans = np.transpose(mat)

    return x + matrans.dot(mat.dot(x))

def cg(b, x, eps, mat):
    r = b - calc(mat, x)
    d = r
    counter = 0

    while norm(r) > eps:
        Ad = calc(mat,d)
        rsq = r.dot(r)

        alpha = rsq / d.dot(Ad)
        x   = x + alpha * d
        nr  = r - alpha * Ad
        beta = nr.dot(nr) / rsq
        r = nr
        d = r + beta*d

        counter = counter + 1
        print(norm(r))

n = 10**7

b = np.ones(n)

start = tcount()
cg(b, rand(n), 10**-5, rand(20,n) )
stop = tcount()
print("time :   ",stop-start)
