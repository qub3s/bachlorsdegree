import matplotlib.pyplot as plt
import numpy as np
from math import cos
from math import sin
import math

# funktion
def r(phi,a,eps,phi0):
    return a / (1 + eps*cos(phi - phi0) )

# jacobimatrix
def D(phi,a,eps,phi0):
    return [ (-1*1 / (1 + eps*cos(phi - phi0) )) ,  ((a*cos(phi-phi0) ) / ((cos(phi - phi0) * eps + 1)**2)),  ((a*eps*sin(phi - phi0) / (eps * cos( phi - phi0) +1)**2))]

# funktion
def F(sol,xt):
    solt = [0]*10
    pi = 2*math.pi / 10
    for c in range(0,10):
        phi = c*pi
        solt[c] = r(phi,xt[0],xt[1],xt[2]) - sol[c]
    return solt;

# jacobimatrix
def DF(xt):
    der = [0]*10
    pi = 2*math.pi / 10
    for c in range(0,10):
        phi = c*pi
        der[c] = D(phi,xt[0],xt[1],xt[2])
    return der;

# print einer xt
def show(a):
    n = 100
    pointsx = [0]*n
    pointsy = [0]*n

    pi = 2*math.pi / (n-1)

    for c in range(0,n-1):
        phi = c*pi
        pointsx[c] = cos(phi) * r(phi,a[0],a[1],a[2])
        pointsy[c] = sin(phi) * r(phi,a[0],a[1],a[2])

    pointsx[n-1] = cos(0) * r(phi,a[0],a[1],a[2])
    pointsy[n-1] = sin(0) * r(phi,a[0],a[1],a[2])
    plt.plot(pointsx,pointsy)

# gaussnewtonverfahren
def newton(xt):
    sol =  [ 0.5578, 0.4659, 0.4502, 0.5294, 0.7649, 1.3984, 3.1254, 3.5525, 1.6245, 0.8267 ]
    print(F(sol,xt))
    dx = np.linalg.lstsq(DF(xt),F(sol,xt),rcond=None)[0]
    xt[0] = xt[0] + dx[0] 
    xt[1] = xt[1] + dx[1]
    xt[2] = xt[2] + dx[2]
    show(xt)
    summe = 1
    
    while summe > 0.0011:
        dx = np.linalg.lstsq(DF(xt),F(sol,xt),rcond=None)[0]

        xt[0] = xt[0] + dx[0] 
        xt[1] = xt[1] + dx[1]
        xt[2] = xt[2] + dx[2]

        summe = 0
        tempsol = F(sol,xt)
        print(tempsol)
        print()
        for x in range(0,10):
            summe = summe + abs(tempsol[x])**2
        show(xt)

def main():
    x0 = [1,0,0]

    sol = [ 0.5578, 0.4659, 0.4502, 0.5294, 0.7649, 1.3984, 3.1254, 3.5525, 1.6245, 0.8267 ]
    sx = [0]*10
    sy = [0]*10

    pi = 2*math.pi / (10)
    for c in range(0,10):
        phi = c*pi
        sx[c] = cos(phi) * sol[c]
        sy[c] = sin(phi) * sol[c]

    plt.scatter(sx,sy)          # konvergenzpunk

    show(x0)                    # einheitskreis
    xt = x0

    newton(xt)
    plt.show()

main()
