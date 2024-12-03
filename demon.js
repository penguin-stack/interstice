import { Soldier } from './soldier.js';



export class Demon {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        this.hasNotEaten = 0;
        this.sinceMult = 0;
        this.turns = 0;
    }

    move(board, turn) {
        if (this.turns >= turn) {
            return;
        }

        let direction = this.findNearestOrthogonalSoldier(board);
        if (direction === -1) {
            direction = this.findFarthestEdge();
        }

        
        
        let newRow = this.r + direction[0];
        let newCol = this.c + direction[1];
        
        this.sinceMult += 1;
        this.turns += 1;
        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
            let entity = board[newRow][newCol];
            if (entity instanceof Demon) {
                this.hasNotEaten += 1;
                return;
            }
            if (entity instanceof Soldier) {
                this.hasNotEaten = 0;

                board[newRow][newCol] = this;
                let oldRow = this.r;
                let oldCol = this.c;
                this.c = newCol;
                this.r = newRow;
                board[oldRow][oldCol] = null;
            }
            if (entity === null) {
                this.hasNotEaten += 1;

                board[newRow][newCol] = this;
                let oldRow = this.r;
                let oldCol = this.c;
                this.c = newCol;
                this.r = newRow;
                board[oldRow][oldCol] = null;
            }
        } else {
            this.hasNotEaten += 1;
        }



    }

    findNearestOrthogonalSoldier(board) {
        let directions = [[-1,0], [0,1], [1,0], [0,-1]];
        let nearest_ants = [];
        let min_distance = Infinity;

        directions.forEach(([dr, dc]) => {
            let r = this.r;
            let c = this.c;
            let distance = 0;

            while (r >= 0 && r < 10 && c >= 0 && c < 10) {
                distance += 1;
                r += dr;
                c += dc;

                if (r >= 0 && r < 10 && c >= 0 && c < 10 && board[r][c] instanceof Soldier) {
                    if (distance <= min_distance) {
                        min_distance = distance;
                        let num_neighbors = board[r][c].numberOfNeighbors(board);
                        nearest_ants.push({ dr, dc, distance, num_neighbors});
                    }
                    break;
                }
            }
        });

        nearest_ants = nearest_ants.filter(tup => tup.distance === min_distance);
        nearest_ants.sort((a, b) => b.num_neighbors - a.num_neighbors);
        
        if (nearest_ants.length > 0) {
            let nearest_ant = nearest_ants[0];
            return [nearest_ant.dr, nearest_ant.dc];
        } else {
            return -1;
        }

        
    }

    findFarthestEdge() {
        const farthestEdges = [];

        farthestEdges.push({ dr: -1, dc: 0, distance: this.r });
        farthestEdges.push({ dr: 0, dc: 1, distance: 9 - this.c });
        farthestEdges.push({ dr: 1, dc: 0, distance: 9 - this.r });
        farthestEdges.push({ dr: 0, dc: -1, distance: this.c });

        // Sort by distance in descending order
        farthestEdges.sort((a, b) => b.distance - a.distance);

        const farthestEdge = farthestEdges[0];
        return [farthestEdge.dr, farthestEdge.dc];
    }

    mult(board) {
        if (this.sinceMult === 8) {
            const directions = [
                [-1, 0], // North
                [0, 1],  // East
                [1, 0],  // South
                [0, -1]  // West
            ];
    
            for (const [dr, dc] of directions) {
                const newRow = this.r + dr;
                const newCol = this.c + dc;
    
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10 && !board[newRow][newCol]) {
                    const newDemon = new Demon(newRow, newCol);
                    newDemon.turns = this.turns;
                    board[newRow][newCol] = newDemon;
                    this.sinceMult = 0;
                    return;
                }
            }
    
            this.sinceMult = 0;
        }
    }

    starve(board) {
        if (this.hasNotEaten === 5) {
            board[this.r][this.c] = null;
        }
    }



}

