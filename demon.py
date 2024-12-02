import math
from soldier import Soldier

class Demon:
    def __init__(self, r, c):
        self.r = r
        self.c = c
        self.hasNotEaten = 0
        self.sinceMult = 0
        self.turns = 0

    def move(self, board, turn):
        if self.turns >= turn:
            return
        
        direction = self.findNearestOrthogonalSoldier(board)
        if direction == -1:
            direction = self.findFarthestEdge()
        
        newRow = self.r + direction[0]
        newCol = self.c + direction[1]
        self.sinceMult += 1
        self.turns += 1
        if 0 <= newRow < 10 and 0 <= newCol < 10:
            entity = board[newRow][newCol]
            if type(entity) is Demon:
                self.hasNotEaten += 1
                return
            if type(entity) is Soldier:
                self.hasNotEaten = 0

                board[newRow][newCol] = self
                oldRow = self.r
                oldCol = self.c
                self.c = newCol
                self.r = newRow
                board[oldRow][oldCol] = None
            if not entity:
                self.hasNotEaten += 1

                board[newRow][newCol] = self
                oldRow = self.r
                oldCol = self.c
                self.c = newCol
                self.r = newRow

                board[oldRow][oldCol] = None

        else:
            self.hasNotEaten += 1


    def findNearestOrthogonalSoldier(self, board):
        directions = [(-1, 0), (0, 1), (1, 0), (0, -1)]  # North, East, South, West
        nearest_ants = []
        min_distance = float('inf')

        for dr, dc in directions:
            r, c = self.r, self.c
            distance = 0

            # Move in the current direction until boundary or ant is found
            while 0 <= r < 10 and 0 <= c < 10:
                distance += 1
                r += dr
                c += dc

                if 0 <= r < 10 and 0 <= c < 10 and type(board[r][c]) is Soldier:  # Assuming 'ant' represents an ant
                    if distance <= min_distance:
                        min_distance = distance
                        num_neighbors = board[r][c].numberOfNeighbors(board)
                        nearest_ants.append((dr,dc,distance,num_neighbors))
                    break  # Stop searching in this direction

        nearest_ants = [tup for tup in nearest_ants if tup[2] == min_distance]
        nearest_ants = sorted(nearest_ants, key=lambda tup: tup[3], reverse=True)
        if nearest_ants:
            nearest_ant = nearest_ants[0]
            return (nearest_ant[0], nearest_ant[1])
        else:
            return -1
        
    def findFarthestEdge(self):
        farthestEdges = []
        farthestEdges.append((-1, 0, self.r))
        farthestEdges.append((0, 1, 9 - self.c))
        farthestEdges.append((1, 0, 9 - self.r))
        farthestEdges.append((0, -1, self.c))
        farthestEdges = sorted(farthestEdges, key=lambda tup: tup[2], reverse=True)
        farthestEdge = farthestEdges[0]
        return (farthestEdge[0], farthestEdge[1])
    
    # def getDirection(self, r, c, ri, ci):
    #     if ri == r and ci < c: return 'West'
    #     if ri == r and ci > c: return 'East'
    #     if ci == c and ri > r: return 'South'
    #     if ci == c and ri < r: return 'North'
        