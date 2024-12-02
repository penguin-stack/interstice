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

def multDemons(board):
    for c in range(0, 10):
        for r in range(0, 10):
            if type(board[r][c]) is demon.Demon:
                board[r][c].mult(board)

def multSoldiers(board):
    for c in range(0, 10):
        for r in range(0, 10):
            if type(board[r][c]) is soldier.Soldier:
                board[r][c].mult(board)

def starveDemons(board):
    for c in range(0, 10):
        for r in range(0, 10):
            if type(board[r][c]) is demon.Demon:
                board[r][c].starve(board)

def moveSoldiers(board, currentTurn):
    for c in range(0, 10):
        for r in range(0, 10):
            if type(board[r][c]) is soldier.Soldier:
                board[r][c].move(board, currentTurn)

def moveDemons(board, currentTurn):
    for c in range(0, 10):
        for r in range(0, 10):
            if type(board[r][c]) is demon.Demon:
                board[r][c].move(board, currentTurn)


def play(board, turn):
    moveDemons(board, turn)
    moveSoldiers(board, turn)
    starveDemons(board)
    multSoldiers(board)
    multDemons(board)

def main():

    currentTurn = 1
    makeBoard(board)
    printBoard(board)

    while(True):
        input("Press Enter: ")
        play(board, currentTurn)
        printBoard(board)
        print(currentTurn)
        currentTurn += 1
        
        

if __name__ == '__main__':
    main()