class Soldier:
    def __init__(self, r, c):
        self.r = r
        self.c = c

    def numberOfNeighbors(self, board):
        directions = [
            (-1,  0), (-1, +1), ( 0, +1), (+1, +1), 
            (+1,  0), (+1, -1), ( 0, -1), (-1, -1)
        ]
        count = 0
        for dc, dr in directions:
            neighbor_row = self.r + dr
            neighbor_col = self.c + dc
            if 0 <= neighbor_row < 10 and 0 <= neighbor_col < 10:
                if type(board[neighbor_row][neighbor_col]) is Soldier:
                    count += 1
        return count