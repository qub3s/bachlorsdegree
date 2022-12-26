import numpy as np
from math import sqrt

# the Function itself 
def F(p):
    x = p[0]
    y = p[1]
    return np.array([ 4*x*x-y*y-1 , y-x*x ])

# the Jacobi Matrix of the function, or in the 1 Function, 1 Variable case simply the derivative
def DF(p):
    x = p[0]
    y = p[1]
    return np.array([[ 8*x , -2*y ],[ -2*x , 1 ]])

# the 2-vector-norm
def norm(a):
    summe = 0
    for x in a:
        summe = summe + x*x
    return sqrt(summe)

# newton calculation
def newton(p,tol):
    counter = 1
    try:
        dx = np.linalg.solve(DF(p),-1*F(p) )
    except:
        return 0
    dxm1 = dx

    p = p + dx
    q = 0.5

    while ( q / (1-q) ) * norm(dx) > tol:
        counter = counter + 1
        try:
            dx = np.linalg.solve(DF(p),-1*F(p) )
        except:
            return 0
        
        q = norm(dx) / norm(dxm1)
        dxm1 = dx
        p = p + dx

        if(q >= 1):
            #print("konvergiert nicht!\n")
            return 0
    print(counter)
    print("Punkt: ",p)
    print("Nullstelle Norm: ",norm(F(p)))
    return 1

# frozen newton method (just for comarison, not optimised with LU decomposition of the Jacobi Matrix)
def frozennewton(p,tol):
    counter = 1

    df = DF(p)

    try:
        dx = np.linalg.solve(df,-1*F(p) )
    except:
        return 0
    dxm1 = dx
    
    p = p + dxm1
    q = 0.5

    while ( q / (1-q) ) * norm(dx) > tol:
        counter = counter + 1
        try:
            dx = np.linalg.solve(df,-1*F(p) )
        except:
            return 0
        
        q = norm(dx) / norm(dxm1)
        dxm1 = dx
        p = p + dx

        if(q >= 1):
            #print("konvergiert nicht!\n")
            return 0


    print(counter)
    print("Punkt: ",p)
    print("Nullstelle Norm: ",norm(F(p)))
    return 1

tol = 0.0000000001

newton([15,3],tol)
print("\n")
frozennewton([15,3],tol)
