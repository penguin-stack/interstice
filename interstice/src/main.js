import { Soldier } from "./soldier.js";
import { Demon } from "./demon.js";

const board = Array.from({ length: 10 }, () => Array(10).fill(null));
const DEMON_EMOJI = "👹";
const SOLDIER_EMOJI = "♞";
const EMPTY_CELL = " ";
const seenStates = new Set();

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

function demonsWin(board) {
  let foundSoldier = false;
  for (let c = 0; c < 10; c++) {
    for (let r = 0; r < 10; r++) {
      if (board[r][c] instanceof Soldier) {
        foundSoldier = true;
      }
    }
  }
  return !foundSoldier;
}

function soldiersWin(board) {
  let foundDemon = false;
  for (let c = 0; c < 10; c++) {
    for (let r = 0; r < 10; r++) {
      if (board[r][c] instanceof Demon) {
        foundDemon = true;
      }
    }
  }
  return !foundDemon;
}

function encode(board) {
  let s = "";
  for (let c = 0; c < 10; c++) {
    for (let r = 0; r < 10; r++) {
      if (board[r][c] instanceof Demon) {
        s = s + '*';
      } else if (board[r][c] instanceof Soldier) {
        s = s + 'S';
      } else {
        s = s + 'x';
      }
    }
  }
  return s;
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

// Start the auto-play process
function startAutoPlay() {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval); // Stop auto-play if already running
  }

  autoPlayInterval = setInterval(() => {
    playOneTurn(); // Play a turn every `playSpeed` milliseconds
  }, playSpeed);

  // Disable the "Start Auto Play" button and enable "Pause Auto Play"
  document.getElementById("autoPlayBtn").disabled = true;
  document.getElementById("pauseAutoPlayBtn").disabled = false;
}

// Pause the auto-play process
function pauseAutoPlay() {
  clearInterval(autoPlayInterval);
  autoPlayInterval = null;

  // Enable the "Start Auto Play" button and disable "Pause Auto Play"
  document.getElementById("autoPlayBtn").disabled = false;
  document.getElementById("pauseAutoPlayBtn").disabled = true;
}

// Update the speed based on the slider value in real-time
function updateSpeed() {
  const speedSlider = document.getElementById("speedSlider");

  // Invert the slider value: calculate the delay as max - current value
  const maxSpeed = parseInt(speedSlider.max, 10); // Get the maximum slider value
  const minSpeed = parseInt(speedSlider.min, 10); // Get the minimum slider value
  playSpeed = maxSpeed - parseInt(speedSlider.value, 10) + minSpeed;

  document.getElementById("speedValue").textContent = `Speed: ${playSpeed}ms`;

  // If auto-play is already running, update the interval speed immediately
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval); // Clear the old interval
    startAutoPlay(); // Start auto-play again with the new speed
  }
}

let currentTurn = 1;
let gameStarted = false; // Flag to track if the game has started
let autoPlayInterval;
let playSpeed = 500; // Default play speed in milliseconds
makeBoard();
updateBoardOnPage();

function playOneTurn() {
  if (!gameStarted) {
    gameStarted = true; // Mark the game as started
    document.getElementById("grid").classList.add("disabled"); // Gray out the grid
  }

  // Check for win conditions before playing a turn
  if (demonsWin(board)) {
    endGame("Demons Win");
    return;
  }

  if (soldiersWin(board)) {
    endGame("Soldiers Win");
    return;
  }

  const encodedString = encode(board);
  if (seenStates.has(encodedString)) {
    const winMessageElement = document.getElementById("winMessage");
    winMessageElement.textContent = "Interstice!";
  } else {
    seenStates.add(encodedString);
  }

  play(board, currentTurn);
  updateBoardOnPage();

  // Increment and update the turn counter
  currentTurn += 1;
  updateTurnCounter();
}

function updateTurnCounter() {
  const turnCounterElement = document.getElementById("turnCounter");
  turnCounterElement.textContent = `Turn: ${currentTurn}`;
}

function endGame(message) {
  const winMessageElement = document.getElementById("winMessage");

  // Set the message and color based on the winner
  if (message === "Demons Win") {
    winMessageElement.style.color = "#8B0000"; // Dark red for Demons
  } else if (message === "Soldiers Win") {
    winMessageElement.style.color = "#A9A9A9"; // Light grey for Soldiers
  }

  winMessageElement.textContent = message; // Display the win message

  // Stop the auto-play if it's running
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval);
    autoPlayInterval = null;
  }

  // Disable further interactions
  document.getElementById("autoPlayBtn").disabled = true;
  document.getElementById("playOneTurnBtn").disabled = true;
}

// Event listener for the Enter key
document.addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    playOneTurn();
  }
});

window.playOneTurn = playOneTurn;
window.startAutoPlay = startAutoPlay;
window.pauseAutoPlay = pauseAutoPlay;
window.updateSpeed = updateSpeed;

window.openModal = function () {
  const modal = document.getElementById("inputModal");
  modal.style.display = "flex"; // Show the modal
};

window.closeModal = function () {
  const modal = document.getElementById("inputModal");
  modal.style.display = "none"; // Hide the modal
};

window.applyEncodedString = function () {
  const input = document.getElementById("encodedString").value;

  // Validate the input string
  if (input.length !== 100 || !/^[x*S]*$/.test(input)) {
    alert("Invalid input! Please enter a 100-character string containing only 'x', '*', or 'S'.");
    return;
  }

  // Reset the contents of the board and initial arrays without reassigning the variables
  for (let r = 0; r < 10; r++) {
    for (let c = 0; c < 10; c++) {
      const index = r * 10 + c;
      const char = input[index];

      if (char === "*") {
        board[r][c] = new Demon(r, c);
        initial[r][c] = 2;
      } else if (char === "S") {
        board[r][c] = new Soldier(r, c);
        initial[r][c] = 1;
      } else {
        board[r][c] = null; // Empty
        initial[r][c] = 0;
      }
    }
  }

  // Update the grid on the page
  updateBoardOnPage();

  // Close the modal
  window.closeModal();
};

window.randomizeGrid = function () {
  // Reset the board and initial state
  for (let r = 0; r < 10; r++) {
      for (let c = 0; c < 10; c++) {
          const randomValue = Math.random();
          if (randomValue < 0.33) {
              board[r][c] = new Demon(r, c);
              initial[r][c] = 2;
          } else if (randomValue < 0.66) {
              board[r][c] = new Soldier(r, c);
              initial[r][c] = 1;
          } else {
              board[r][c] = null; // Empty
              initial[r][c] = 0;
          }
      }
  }

  // Update the grid on the page
  updateBoardOnPage();

  // Reset the game state
  currentTurn = 1;
  document.getElementById("turnCounter").textContent = "Turn: 1";
  document.getElementById("winMessage").textContent = ""; // Clear any win message
  gameStarted = false;
  document.getElementById("grid").classList.remove("disabled"); // Re-enable grid interactions
};