import demon
import soldier

board = [[None for i in range(10)] for j in range(10)]

def makeBoard(board):
    with open('world2.txt', 'r') as f:
        row = 0
        
        for line in f:
            for i in range(len(line)):
                if line[i] == 'S':
                    board[row][i] = soldier.Soldier(row,i)
                if line[i] == '*':
                    board[row][i] = demon.Demon(row, i)
            row += 1

def printBoard(board):
    for x in board:
        for y in x:
            if type(y) is demon.Demon:
                print('*', end='')
            elif type(y) is soldier.Soldier:
                print('S', end='')
            else:
                print(' ', end='')
        print()


makeBoard(board)
printBoard(board)

print(board[2][5].findDirection(board))
