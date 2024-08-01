import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


//public class com.example.Main {
//    public static void main(String[] args) throws IOException {
////        com.example.Entity[][] board = new com.example.Entity[10][10];
////
////        makeBoard(board);
////
////
////        System.out.print("how many turns do you want to see? ");
//////        Scanner input = new Scanner(System.in);
////        int turns = Integer.MAX_VALUE;
////
//////        printBoard(board);
////        for (int currentTurn = 1; currentTurn <= turns; currentTurn++) {
////            play(board, currentTurn);
////
////            System.out.println("Turn " + currentTurn);
////            printBoard(board);
////            System.out.println();
////            if (demonsWin(board)) {
////                System.out.println("Demons win");
////                return;
////            }
////            if (soldiersWin(board)) {
////                System.out.println("Soldiers win");
////                return;
////            }
////        }
////        input.close();
//
//        com.example.Entity[][] board = new com.example.Entity[10][10];
//        makeBoard(board);
//
//        int turns = Integer.MAX_VALUE;
//
//        JFrame frame = new JFrame("Board Game");
//        BoardPanel boardPanel = new BoardPanel(board);
//        frame.add(boardPanel);
//        frame.setSize(400, 400);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//
//        Timer timer = new Timer(500, e -> {
//            play(board, turns);
//
//            boardPanel.repaint();
//
//            if (demonsWin(board)) {
//                JOptionPane.showMessageDialog(frame, "Demons win");
//                ((Timer) e.getSource()).stop();
//            } else if (soldiersWin(board)) {
//                JOptionPane.showMessageDialog(frame, "Soldiers win");
//                ((Timer) e.getSource()).stop();
//            }
//        });
//
//        timer.start();
//
//    }


//public class Main {
//    private static Entity[][] board;
//    private static int currentTurn = 1;
//    private boolean gameStarted = false;
//
//    public static void main(String[] args) throws IOException {
//
//        while (true) {
//            board = new Entity[10][10];
//            generateRandomGrid(board);
//            printBoard(board);
//            while (true) {
//                play(board, currentTurn);
//
//                if (demonsWin(board) || soldiersWin(board))
//                    break;
//                currentTurn += 1;
//            }
//            System.out.println(currentTurn);
//            if (currentTurn > 500) {
//                break;
//            }
//            currentTurn = 1;
//        }
//    }

public class Main {
    private Entity[][] board;
    private int currentTurn = 1;
    private boolean gameStarted = false;

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            try {
                new Main().startGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void startGame() throws IOException {
        board = new Entity[10][10];
        makeBoard(board);

        JFrame frame = new JFrame("Interstice");
        BoardPanel boardPanel = new BoardPanel(board);
        frame.add(boardPanel);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameStarted = true;
                startGameLoop(boardPanel);
            }
        });

        frame.add(startButton, BorderLayout.SOUTH);
    }

    private void startGameLoop(BoardPanel boardPanel) {
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play(board, currentTurn);
                boardPanel.repaint();

                if (demonsWin(board)) {
                    JOptionPane.showMessageDialog(boardPanel, "Demons win " + currentTurn);
                    ((Timer) e.getSource()).stop();
                } else if (soldiersWin(board)) {
                    JOptionPane.showMessageDialog(boardPanel, "Soldiers win " + currentTurn);
                    ((Timer) e.getSource()).stop();
                }
                currentTurn++;
            }
        });

        timer.start();
    }


//    private static void clearScreen() {
//        // Clear screen by printing a large number of new lines
//        System.out.print("\033[H\033[2J");
//        System.out.flush();
//    }


//    private static void printBoard() {
//        for (int row = 0; row < board.length; row++) {
//            for (int col = 0; col < board[row].length; col++) {
//                if (board[row][col] == null) {
//                    System.out.print(". ");
//                } else if (board[row][col] instanceof Demon) {
//                    System.out.print("* ");
//                } else if (board[row][col] instanceof Soldier) {
//                    System.out.print("S ");
//                }
//            }
//            System.out.println();
//        }
//    }

    public static void generateRandomGrid(Entity[][] board) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int randInt = random.nextInt(100 - 1 + 1) + 1;
                if (randInt <= 47) {
                    board[i][j] = null;
                }
                else if (randInt <= 59){
                    board[i][j] = new Demon(i,j);
                } else {
                    board[i][j] = new Soldier(i,j);
                }
            }
        }

    }





    public static void makeBoard(Entity[][] board) throws FileNotFoundException {
        File file = new File("world2.txt");
        Scanner scanner = new Scanner(file);
        int row = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == 'S') {
                    board[row][i] = new Soldier(row,i);
                } else if (line.charAt(i) == '*') {
                    board[row][i] = new Demon(row, i);
                } else {
                    board[row][i] = null;
                }
            }
            row += 1;
        }

    }

    class BoardPanel extends JPanel {
        private final Entity[][] board;
        private final Image demonImage;
        private final Image knightImage;

        public BoardPanel(Entity[][] board) {
            this.board = board;
            this.demonImage = loadImage("demon.png");
            this.knightImage = loadImage("knight.png");

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!gameStarted) {
                        int cellSize = Math.min(getWidth() / board.length, getHeight() / board[0].length);
                        int row = e.getY() / cellSize;
                        int col = e.getX() / cellSize;
                        toggleEntity(row, col);
                        repaint();
                    }
                }
            });
        }

        private Image loadImage(String path) {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() == -1) {
                System.err.println("Error: Could not load image " + path);
                return null;
            }
            return icon.getImage();
        }

        private void toggleEntity(int row, int col) {
            if (board[row][col] == null) {
                board[row][col] = new Demon(row, col);
            } else if (board[row][col] instanceof Demon) {
                board[row][col] = new Soldier(row, col);
            } else if (board[row][col] instanceof Soldier) {
                board[row][col] = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
        }

        private void drawBoard(Graphics g) {
            int cellSize = Math.min(getWidth() / board.length, getHeight() / board[0].length);
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    drawEntity(g, board[row][col], col * cellSize, row * cellSize, cellSize);
                }
            }
        }

        private void drawEntity(Graphics g, Entity entity, int x, int y, int size) {
            if (entity == null) {
                g.setColor(Color.WHITE);
                g.fillRect(x, y, size, size);
            } else if (entity instanceof Demon && demonImage != null) {
                g.drawImage(demonImage, x, y, size, size, this);
            } else if (entity instanceof Soldier && knightImage != null) {
                g.drawImage(knightImage, x, y, size, size, this);
            }
            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size);
        }
    }

    public static void printBoard(Entity[][] board) {
        for (Entity[] entities : board) {
            for (int j = 0; j < board[0].length; j++) {
                if (entities[j] instanceof Soldier)
                    System.out.print("S");
                else if (entities[j] instanceof Demon)
                    System.out.print("*");
                else
                    System.out.print(" ");
            }

            System.out.println();
        }
    }

    public static void play(Entity[][] board, int turn) {
        moveDemons(board, turn);
        moveSoldiers(board, turn);
        starveDemons(board);
        multSoldiers(board);
        multDemons(board);
    }

    private static void multDemons(Entity[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (Entity[] entities : board) {
                if (entities[col] instanceof Demon) {
                    entities[col].mult(board);
                }
            }
        }
    }

    private static void multSoldiers(Entity[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (Entity[] entities : board) {
                if (entities[col] instanceof Soldier) {
                    entities[col].mult(board);
                }
            }
        }
    }

    private static void starveDemons(Entity[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (Entity[] entities : board) {
                if (entities[col] instanceof Demon) {
                    ((Demon) entities[col]).starve(board);
                }
            }
        }
    }

    private static void moveSoldiers(Entity[][] board, int currentTurn) {
        for (int col = 0; col < board[0].length; col++) {
            for (Entity[] entities : board) {
                if (entities[col] instanceof Soldier) {
                    entities[col].move(board, currentTurn);
                }
            }
        }
    }

    private static void moveDemons(Entity[][] board, int currentTurn) {
        for (int col = 0; col < board[0].length; col++) {
            for (Entity[] entities : board) {
                if (entities[col] instanceof Demon) {
                    entities[col].move(board, currentTurn);
                }
            }
        }
    }

    private static boolean soldiersWin(Entity[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] instanceof Demon) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean demonsWin(Entity[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] instanceof Soldier) {
                    return false;
                }
            }
        }
        return true;
    }
}