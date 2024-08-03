import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;


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


    public static void makeBoard(Entity[][] board) throws FileNotFoundException {
        InputStream textStream = Main.class.getResourceAsStream("/world2.txt");
        assert textStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(textStream));
        Scanner scanner = new Scanner(reader);
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

        public BoardPanel(Entity[][] board) throws IOException {
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

        private Image loadImage(String path) throws IOException {
            InputStream imageStream = Main.class.getResourceAsStream("/" + path);
            assert imageStream != null;
            BufferedImage image = ImageIO.read(imageStream);
            ImageIcon icon = new ImageIcon(image);
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

    public static void play(Entity[][] board, int turn) {
        moveDemons(board, turn);
        moveSoldiers(board, turn);
        starveDemons(board);
        multSoldiers(board);
        multDemons(board);
    }

    private static void multDemons(Entity[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] instanceof Demon) {
                    board[row][col].mult(board);
                }
            }
        }
    }

    private static void multSoldiers(Entity[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] instanceof Soldier) {
                    board[row][col].mult(board);
                }
            }
        }
    }

    private static void starveDemons(Entity[][] board) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] instanceof Demon) {
                    ((Demon) board[row][col]).starve(board);
                }
            }
        }
    }

    private static void moveSoldiers(Entity[][] board, int currentTurn) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] instanceof Soldier) {
                    board[row][col].move(board, currentTurn);
                }
            }
        }
    }

    private static void moveDemons(Entity[][] board, int currentTurn) {
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] instanceof Demon) {
                    board[row][col].move(board, currentTurn);
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