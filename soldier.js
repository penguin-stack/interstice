export class Soldier {
    constructor(r, c) {
        this.r = r;
        this.c = c;
    }

    numberOfNeighbors(board) {
        let directions = [
            [-1, 0], [-1, 1], [0, 1], [1, 1],
            [1, 0], [1, -1], [0, -1], [-1, -1]
        ];
        let count = 0;

        directions.forEach(([dr, dc]) => {
            let neighbor_row = this.r + dr;
            let neighbor_col = this.c + dc;
            if (neighbor_row >= 0 && neighbor_row < 10 && neighbor_col >= 0 && neighbor_col < 10) {
                if (board[neighbor_row][neighbor_col] instanceof Soldier) {
                    count += 1;
                }
            }
        });
        
        return count;
    }


}