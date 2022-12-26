from math import sqrt, pow
import numpy as np

def qrdec(a):
    (maxm,maxn) = a.shape
    u = np.zeros((maxm,maxn), dtype="double")
    q = np.zeros((maxm,maxn), dtype="double")

    for k in range(maxn):
        for i in range(k,maxm):
            u[i][k] = a[i][k]/norminf(a[:,k][k-maxm:])

        alpha = norm2(u[:,k][k-maxm:])
        beta = 1/(alpha*(alpha+abs(u[k][k])))
        u[k][k] = u[k][k] + alpha * sign(a[k][k])
        a[k][k] = sign(a[k][k])*norminf(a[:,k][k-maxm:])*alpha
        
        for i in range(k+1,maxm):
            a[i][k] = 0
        
        for j in range(k+1,maxm):
            summe = 0
            for i in range(k,maxm):
                summe = summe + u[i][k]*a[i][j]
            s = beta*summe
         
            for i in range(k,maxm):
                a[i][j] = a[i][j] - s*u[i][k]

    q = np.identity(maxn) - (2/scalar(u[:,0],u[:,0])*creatematrix(np.zeros((maxn,maxn), dtype="float") , u[:,0] , maxn))
    for i in range(1,maxn):
        q = np.dot(q,(np.identity(maxn) - ( 2/scalar( u[:,i],u[:,i] ) )*creatematrix( np.zeros( (maxn,maxn), dtype="float" ) , u[:,i] , maxn ) ) ) 
        
    q = q*-1
    return q

def creatematrix(matrix,vector,n):
    for x in range(n):
        for y in range(n):
            matrix[x][y] = vector[x]*vector[y]
    return matrix


# 2-Norm
def norm2(vector):
    summe = 0
    for i in range(0,len(vector)):
        summe = summe + vector[i]**2
    return round(sqrt(summe),2)

# gleiche Länge wird vorrausgesetzt
def scalar(vector1,vector2):
    summe = 0
    for i in range(0,len(vector1)):
        summe = summe + vector1[i]*vector2[i]
    return round(summe,2)

def norminf(vector):
    maxi = vector[0];
    for x in range(1,len(vector)):
        if maxi < vector[x]:
            maxi = abs(vector[x])
    return maxi

def sign(num):
    if num > 0:
        return 1
    if num < 0:
        return -1
    return 0


# Die einzelenen Zeilen werden Diagonalisiert
matrix = np.array([[5, 5], [5, 5.1]], dtype="double")
print("QR:")
print(qrdec(matrix))
print("Lösung:")
print("[[ 0.70631424 -0.70778267]\n [ 0.70778267  0.70631424]]")


