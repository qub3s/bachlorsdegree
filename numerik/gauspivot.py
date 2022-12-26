import numpy as np

# Calculates the LU-Decomposition of a nxn-Matrix
def gausspivot(m):
    v = np.zeros(len(m))
    for x in range(0,len(v)):
        v[x] = x

    # search for the maximum and change the rows accordingly
    for n1 in range(0,len(m)):
        max = n1
        for n2 in range(n1,len(m)):
            if m.item((max,n1)) < m.item((n2,n1)):
                max = n2
        if max != n1:
            # change matrix
            temp = m[n1].copy()
            m[n1] = m[max]
            m[max] = temp
            
            # change vector
            temp = v[n1].copy()
            v[n1] = v[max]
            v[max] = temp

        # Use LU-Decomposition
        for n2 in range(n1+1,len(m)):
            mult = m.item((n2,n1))/m.item((n1,n1))
            for x in range(n1,len(m)):
                m.itemset((n2,x),m.item((n2,x)) - m.item(n1,x)*mult)
            m.itemset((n2, n1), mult)

    print("This is the starting matrix")
    print(m)
    print()
    print("This is the permutation vector")
    print(v)

n = 3 # size of matrix
matrix = np.random.rand(n, n)*n

print(matrix,"\n")
gausspivot(matrix)
