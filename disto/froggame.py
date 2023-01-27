from random import randint

def printchess(chess):
    for x in range(0,8):
        print(chess[x]);
    print("\n")

def move(chess,p):
    original = list(map(list, chess))
   
    wakefrogs = False
    sleepfrogs = False

    for x in range(0,8):
        for y in range(0,8):
            valorig = original[x][y]
            while chess[x][y] < 0 and original[x][y] < 0:
                if p >= randint(0,1000000):
                    a = 0
                    b = 0
                    while True:
                        r = randint(0, 3)
                        if r == 0:
                            a = 1
                            b = 0
                        if r == 1:
                            a = -1
                            b = 0
                        if r == 2:
                            b = 1
                            a = 0
                        if r == 3:
                            b = -1
                            a = 0

                        if not( x+a < 0 or x+a > 7 or y+b < 0 or y+b > 7 ) :
                            break;
                    
                    if chess[x+a][y+b] == 1:
                        chess[x+a][y+b] = -2
                    else:
                        chess[x+a][y+b] = chess[x+a][y+b] - 1
                    
                original[x][y] = original[x][y] + 1
                chess[x][y] = chess[x][y] + 1
            original[x][y] = valorig

    for x in range(0,8):
        for y in range(0,8):
            if chess[x][y] < 0:
                wakefrogs = True
            if chess[x][y] == 1:
                sleepfrogs = True

    return (chess, sleepfrogs, wakefrogs )


def main():
    p = 942000
    size = 10000
    for p in range(p,p+10):
        counter = 0
        for x in range(size): 
            chess = [ [0, 1, 0, 1, 0, 1, 0, 1], [1, 0, 1, 0, 1, 0, 1, 0], [0, 1, 0, 1, 0, 1, 0, 1], [1, 0, 1, 0, 1, 0, 1, 0], [0, 1, 0, 1, 0, 1, 0, 1], [1, 0, 1, 0, 1, 0, 1, 0], [0, 1, 0, 1, 0, 1, 0, 1], [1, 0, 1, 0, 1, 0, 1, 1] ]
            chess[3][3] = -1
            
            sleepfrogs = True
            wakefrogs = True
            while wakefrogs:
                chess, sleepfrogs, wakefrogs = move(chess,p)
                #printchess(chess)
                
                if not sleepfrogs:
                    counter = counter + 1
                    wakefrogs = False

        print("P: ",p,"  counter: ",counter/size*100)
main()
