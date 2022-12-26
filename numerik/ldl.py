import numpy as np
from numpy import array,zeros,ogrid,broadcast_arrays
import matplotlib.pyplot as plt

# Calculates the LDL or the Cholesky decomposition of a matrix 
# This decomposition is faster than all the other decompositions but can only be use on positive-definite-matrices

# returns the lower half and the digonal part
def LDLdec(matrix):
    n = len(matrix)
    for k in range(0,n):
        summe = 0
        for j in range(0,k):
            summe = summe + matrix.item( (j,j) ) * matrix.item( (k,j) )**2
        matrix.itemset((k,k), matrix.item((k,k)) - summe )
        
        for i in range(k+1,n):
            summe = 0
            for j in range(0,k):
                summe = summe + matrix.item((i,j)) * matrix.item((j,j)) * matrix.item((k,j))
            matrix.itemset( (i,k), (matrix.item((i,k)) - summe ) / matrix.item((k,k)) )
            #matrix.itemset( (k,i), matrix.item((i,k)) )                                   # can be uncommentet if the complete matrix is needed
    return matrix

matrix = np.matrix("4 12 100; 12 37 -43; -16 -43 98")
vec = np.matrix("1 2 3",dtype="float")

print(LDLdec(matrix),"\n")

