import { Soldier } from './soldier.js';
import { Demon } from './demon.js';

const board = Array.from({ length: 10 }, () => Array(10).fill(null));


let initial = [
    [0, 0, 0, 1, 1, 1, 0, 0, 0, 0],
    [0, 2, 0, 0, 1, 1, 0, 0, 0, 0],
    [0, 2, 0, 0, 0, 1, 0, 0, 0, 0],
    [0, 2, 0, 0, 0, 0, 1, 1, 1, 0],
    [0, 0, 0, 0, 0, 0, 1, 1, 1, 0],
    [1, 0, 0, 0, 0, 0, 1, 1, 1, 0],
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    [1, 0, 0, 0, 1, 1, 1, 1, 0, 0],
    [0, 0, 0, 0, 1, 1, 1, 1, 0, 0],
    [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
];


function makeBoard() {

    for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
            if (initial[i][j] === 1) {
                board[i][j] = new Soldier(i, j);
            }
            if (initial[i][j] === 2) {
                board[i][j] = new Demon(i, j);
            }
        }
    }

}

function printBoard(board) {
    board.forEach(row => {
        row.forEach(entity => {
            if (entity) {
                if (entity.name === 'Demon') {
                    process.stdout.write("*");
                } else if (entity.name === 'Soldier') {
                    process.stdout.write("S");
                }
                
            } else {
                process.stdout.write(" ");
            } 
        });
        console.log("");
    });
}

makeBoard();
printBoard(board);
