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

        let direction = findNearestOrthogonalSoldier(board);
        if (direction === -1) {
            direction = findFarthestEdge();
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



}

