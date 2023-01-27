from scipy.sparse.linalg import cg
import numpy as np
from scipy.sparse.linalg import LinearOperator
from scipy.sparse import csc_matrix
from numpy.linalg import norm
import matplotlib.pyplot as plt

n = 33
valuesa = []
valuesb = []
# erzeugt die triagonalmatrix
def tridiag(a,b,c,n):
    A = np.zeros([n,n]) 
    for i in range(n):
        if i==0:
            A[i,i]=b 
            A[i,i+1]=c
        elif i ==n-1:
            A[i,i]=b
            A[i,i-1]=a
        else:
            A[i,i]=b
            A[i,i-1]=a
            A[i,i+1]=c
    return A

# erzeugt die possiongleichung
def poisson2d_matrix(n):
    A = np.zeros([(n-1)**2,(n-1)**2])
    B = tridiag(-1,4,-1,n-1)
    I = tridiag(0,-1,0,n-1)

    for y in range(0,len(A),(n-1)):
        for x in range(0,len(B)):
            A.itemset((y+x,y+x), B.item(x,x))
            
            if x != 0:
                A.itemset((y+x-1,y+x), B.item(x,x-1))
                A.itemset((y+x,y+x-1), B.item(x-1,x))
            if y != 0:
                A.itemset((y+x-(n-1),y+x), I.item(x,x))
                A.itemset((y+x,y+x-(n-1)), I.item(x,x))
    return (n*n)*A    

def mv(x):
    a = poisson2d_matrix(n)
    b = np.ones(a.shape[0])
    value = norm(x - np.linalg.solve(a,b) )
    print("Not Modified Matrix: ", value, "    ")
    valuesa.append(value)
    return a.dot(x) 

def cgown(b, x, eps, A):
    r = b - A.dot(x)
    
    Ad = np.triu( np.tril( A ) )
    Au = np.triu( A, 1)
    Al = np.tril( A, -1)
    Adi = np.linalg.inv(Ad)

    m = ( Ad+Al ).dot( Adi ).dot( Ad + Au )
    M = np.linalg.inv(m)
    d = M.dot(r)
    s = d

    while s.dot(r) > eps:
        Ad = A.dot(d)

        alpha = s.dot(r) / d.dot(Ad)
        x   = x + alpha * d
        nr  = r - alpha * Ad
        ns   = M.dot(nr)
        beta = ns.dot(nr) / s.dot(r)
        r = nr
        s = ns
        d = s + beta*d

        value = norm(x-np.linalg.solve(A,b) )
        print("Modified Matrix: ", value, "    " )
        valuesb.append(value)
    return x

def main():
    eps = 10**-12
    a = poisson2d_matrix(n)
    b = np.ones(a.shape[0])
    x0 = np.zeros(a.shape[0])

    mysol = cgown(b,x0,eps,a)
    #print(mysol)
    print("________________________________________________")

    A = LinearOperator( a.shape, matvec=mv )

    x, e = cg(A, b, tol=eps)
    #print("realsolution:",x)

    print()
    print(np.allclose(A.dot(x),b) )
    print(np.allclose(a.dot(mysol),b ) )
    plt.plot(valuesa)
    plt.plot(valuesb)
    plt.show()

main()
