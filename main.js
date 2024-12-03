import { Soldier } from './soldier.js';
import { Demon } from './demon.js';

const board = Array.from({ length: 10 }, () => Array(10).fill(null));
const DEMON_EMOJI = "👹";
const SOLDIER_EMOJI = "♘";
const EMPTY_CELL = " ";

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

function updateBoardOnPage() {
    const grid = document.getElementById("grid");
    grid.innerHTML = ""; // Clear existing grid
    for (let r = 0; r < 10; r++) {
        for (let c = 0; c < 10; c++) {
            const cellDiv = document.createElement("div");
            cellDiv.classList.add("cell");

            // Determine the content of the cell
            if (board[r][c] instanceof Demon) {
                cellDiv.textContent = DEMON_EMOJI;
            } else if (board[r][c] instanceof Soldier) {
                cellDiv.textContent = SOLDIER_EMOJI;
            } else {
                cellDiv.textContent = EMPTY_CELL;
            }

            // Add click functionality if the game hasn't started
            if (!gameStarted) {
                cellDiv.addEventListener("click", () => {
                    if (board[r][c] instanceof Demon) {
                        board[r][c] = null;
                        initial[r][c] = 0;
                    } else if (board[r][c] instanceof Soldier) {
                        board[r][c] = new Demon(r, c);
                        initial[r][c] = 2;
                    } else {
                        board[r][c] = new Soldier(r, c);
                        initial[r][c] = 1;
                    }
                    updateBoardOnPage(); // Refresh the grid
                });
            }

            grid.appendChild(cellDiv);
        }
    }

    // Disable interaction if the game has started
    if (gameStarted) {
        grid.classList.add("disabled");
    }
}

function toggleCell(row, col) {
    // Toggle between Demon, Soldier, and Empty
    if (board[row][col] instanceof Demon) {
        board[row][col] = new Soldier(row, col);
        initial[row][col] = 1; // Update initial array
    } else if (board[row][col] instanceof Soldier) {
        board[row][col] = null;
        initial[row][col] = 0; // Update initial array
    } else {
        board[row][col] = new Demon(row, col);
        initial[row][col] = 2; // Update initial array
    }
    updateBoardOnPage(); // Re-render the board
}


let currentTurn = 1;
let gameStarted = false; // Flag to track if the game has started
makeBoard();
updateBoardOnPage();


function playOneTurn() {
    if (!gameStarted) {
        gameStarted = true; // Mark the game as started
    }
    play(board, currentTurn);
    updateBoardOnPage();
    currentTurn += 1;
}

// Event listener for the Enter key
document.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        playOneTurn();
    }
});

window.playOneTurn = playOneTurn;
