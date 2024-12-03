import { Soldier } from './soldier.js';
import { Demon } from './demon.js';
import PromptSync from 'prompt-sync';

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
                if (entity instanceof Demon) {
                    process.stdout.write("*");
                } else if (entity instanceof Soldier) {
                    process.stdout.write("S");
                }
                
            } else {
                process.stdout.write(" ");
            } 
        });
        console.log("");
    });
}

function multDemons(board) {
    for (let c = 0; c < 10; c++) {
        for (let r = 0; r < 10; r++) {
            if (board[r][c] instanceof Demon) {
                board[r][c].mult(board);
            }
        }
    }
}

function multSoldiers(board) {
    for (let c = 0; c < 10; c++) {
        for (let r = 0; r < 10; r++) {
            if (board[r][c] instanceof Soldier) {
                board[r][c].mult(board);
            }
        }
    }
}

function starveDemons(board) {
    for (let c = 0; c < 10; c++) {
        for (let r = 0; r < 10; r++) {
            if (board[r][c] instanceof Demon) {
                board[r][c].starve(board);
            }
        }
    }
}

function moveSoldiers(board, currentTurn) {
    for (let c = 0; c < 10; c++) {
        for (let r = 0; r < 10; r++) {
            if (board[r][c] instanceof Soldier) {
                board[r][c].move(board, currentTurn);
            }
        }
    }
}

function moveDemons(board, currentTurn) {
    for (let c = 0; c < 10; c++) {
        for (let r = 0; r < 10; r++) {
            if (board[r][c] instanceof Demon) {
                board[r][c].move(board, currentTurn);
            }
        }
    }
}

function play(board, turn) {
    moveDemons(board, turn);
    moveSoldiers(board, turn);
    starveDemons(board);
    multSoldiers(board);
    multDemons(board);
}

const prompt = PromptSync();

function main() {
    let currentTurn = 1;
    makeBoard();
    printBoard(board);

    while (true) {
        const userInput = prompt('Press enter: ');
        play(board, currentTurn);
        printBoard(board);
        console.log(currentTurn);
        currentTurn += 1;
    }
}

main()