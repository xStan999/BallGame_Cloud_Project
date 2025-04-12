package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;




public class SinglePlayerBallGame extends JPanel implements ActionListener, KeyListener {
    private static final int MENU = 0;
    private static final int SINGLE_PLAYER = 1;
    private static final int GAME_OVER = 2;

    private int gameState = MENU;
    private boolean ballInMotion = false;

    private int ballX = 250, ballY = 0, ballSpeedX = 2, ballSpeedY = 2;
    private final int ballSize = 20;
    private int paddleX = 250, paddleWidth = 100, paddleHeight = 20;
    private int lives = 3, score = 0;

    private String playerName = "";
    private int playerId = -1;

    private final Timer timer;
    private final Random random = new Random();

    public SinglePlayerBallGame() {
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setBackground(Color.BLACK);
        timer = new Timer(5, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (gameState) {
            case MENU -> drawMainMenu(g);
            case SINGLE_PLAYER -> drawGame(g);
            case GAME_OVER -> drawGameOver(g);
        }
    }

    private void drawMainMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Ball Game", getWidth() / 2 - 100, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("1. Single Player", getWidth() / 2 - 100, 200);
        g.drawString("2. Multiplayer (not ready)", getWidth() / 2 - 100, 250);
        g.drawString("3. Exit", getWidth() / 2 - 100, 300);
    }

    private void drawGame(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        g.setColor(Color.BLUE);
        g.fillRect(paddleX, getHeight() - paddleHeight - 10, paddleWidth, paddleHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Lives: " + lives, getWidth() - 100, 30);

        if (!ballInMotion) {
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press SPACE to start ball", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Game Over! Score: " + score, getWidth() / 2 - 150, getHeight() / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Press R to Restart", getWidth() / 2 - 100, getHeight() / 2 + 50);
        g.drawString("Press M for Menu", getWidth() / 2 - 100, getHeight() / 2 + 80);
        g.drawString("Press Q to Quit", getWidth() / 2 - 100, getHeight() / 2 + 110);
        saveToCSV();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == SINGLE_PLAYER && ballInMotion) {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            handleBallCollisions();
            repaint();
        }
        if (gameState == GAME_OVER) {
            return;  // Nie ruszaj piłki
        }
    }

    private void handleBallCollisions() {
        if (gameState == GAME_OVER) {
            return;  // Jeśli gra jest zakończona, nie aktualizujemy pozycji piłki
        }
        // odbicia od ścian
        if (ballX <= 0 || ballX >= getWidth() - ballSize) {
            ballSpeedX = -ballSpeedX;
        }
        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY;
        }
        // kolizja z platofrmą
        if (ballY + ballSize >= getHeight() - paddleHeight - 10 &&
                ballX + ballSize >= paddleX &&
                ballX <= paddleX + paddleWidth) {
            ballSpeedY = -ballSpeedY;
            score++;
            // Przyspieszanie piłki po odbiciu
            ballSpeedX += (ballSpeedX > 0) ? 1 : -1;
            ballSpeedY += (ballSpeedY > 0) ? 1 : -1;

            changeColors();
        }
        // utrata życia
        if (ballY > getHeight()) {
            lives--;
            resetBall();
        }

        if (lives <= 0) {
            gameState = GAME_OVER;
            ballInMotion = false;
            repaint();
        }
    }

    private void changeColors() {
        // Losowa zmiana kolorów
        Color ballColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color paddleColor1 = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        setBackground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }

    private void resetBall() {
        ballX = random.nextInt(getWidth() - ballSize);
        ballY = getHeight() / 2;
        ballSpeedX = 2;
        ballSpeedY = 2;
        ballInMotion = false;
    }

    private void resetGame() {
        resetBall();
        lives = 3;
        score = 0;
        ballInMotion = false;
        gameState = SINGLE_PLAYER;
    }

    private void showLoginWindow() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setLayout(new FlowLayout());
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameLabel = new JLabel("Enter your Nickname: ");
        JTextField nameField = new JTextField(15);
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            playerName = nameField.getText();
            if (!playerName.isEmpty()) {
                playerId = random.nextInt(1000); // Generate unique ID
                gameState = MENU;  // Set the game mode to main menu after login
                loginFrame.dispose();
                repaint();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Please enter a nickname.");
            }
        });

        loginFrame.add(nameLabel);
        loginFrame.add(nameField);
        loginFrame.add(loginButton);
        loginFrame.setVisible(true);
    }

    private void saveToCSV() {
        try {

            // Log przed zapisaniem
            System.out.println("Saving to CSV...");

            // Otwieranie pliku w trybie dopisywania
            FileWriter fw = new FileWriter("game_results.csv", true);
            PrintWriter writer = new PrintWriter(fw);

            // Format: nickname, playerId, score
            String result = playerName + "," + playerId + "," + score;
            writer.println(result);

            // Log po zapisaniu
            System.out.println("Saved result: " + result);

            writer.close(); // Zamknięcie pliku

            sendScoreToServer(playerName, score);

        } catch (IOException e) {
            // Log błędu
            System.err.println("Error while saving: " + e.getMessage());

        }
    }



    private boolean dataSaved = false;


    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE -> {
                if (gameState == SINGLE_PLAYER) {
                    ballInMotion = true;
                }
            }
            case KeyEvent.VK_LEFT -> paddleX = Math.max(paddleX - 15, 0);
            case KeyEvent.VK_RIGHT -> paddleX = Math.min(paddleX + 15, getWidth() - paddleWidth);
            case KeyEvent.VK_1 -> {
                if (gameState == MENU) {
                    gameState = SINGLE_PLAYER;
                    resetGame();
                }
            }
            case KeyEvent.VK_2 -> JOptionPane.showMessageDialog(this, "Multiplayer not ready!");
            case KeyEvent.VK_3 -> System.exit(0);
            case KeyEvent.VK_R -> {
                if (gameState == GAME_OVER) {
                    resetGame();
                    dataSaved = false;
                }
            }
            case KeyEvent.VK_M -> gameState = MENU;
            case KeyEvent.VK_Q -> System.exit(0);

        }
        if (gameState == GAME_OVER && !dataSaved) {
            saveToCSV();
            saveToDatabase();
            dataSaved = true;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void sendScoreToServer(String nickname, int score) {
        try {
            URL url = new URL("http://3.122.179.150:5000/submit_score");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000); // czas oczekiwania na połączenie w ms
            conn.setReadTimeout(5000); // czas oczekiwania na odpowiedź w ms


            // Tworzenie JSON-a z wynikiem gry
            String jsonInputString = String.format("{\"nickname\": \"%s\", \"score\": %d, \"player_id\": %d}", nickname, score, playerId);

            // Wysyłanie danych
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Sprawdzanie odpowiedzi serwera
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Score sent successfully!");
            } else {
                System.out.println("Error sending score: " + responseCode);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server error response: " + response.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class DatabaseConnection {
        private static final String URL = "jdbc:mysql://game-results-db.c7y4ckcqgz5j.eu-central-1.rds.amazonaws.com:3306/game-results-db";
        private static final String USER = "admin";
        private static final String PASSWORD = "z";

        public static Connection getConnection() throws SQLException {
            try {
                // Ładowanie sterownika MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC driver not found", e);
            }
        }
    }

    public class GameResultDAO {
        public static void insertGameResult(int playerId, String nickname, int score) {
            String query = "INSERT INTO game_scores (player_id, nickname, score) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                // Przypisanie wartości do zapytania
                stmt.setInt(1, playerId);    // player_id
                stmt.setString(2, nickname); // nickname
                stmt.setInt(3, score);       // score

                // Wykonanie zapytania
                stmt.executeUpdate();
                System.out.println("Game result inserted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveToDatabase() {
        try {


            // Wywołanie metody z DAO, aby wstawić dane do bazy
            GameResultDAO.insertGameResult(playerId, playerName, score);

            // Dodatkowe działania, np. zapis do pliku CSV, jeśli jest potrzebne
            saveToCSV();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        JFrame gameFrame = new JFrame("Ball Game");
        SinglePlayerBallGame gamePanel = new SinglePlayerBallGame();
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setUndecorated(true);  // Usunięcie paska tytułowego
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(gamePanel);
        gameFrame.setVisible(true);
        gamePanel.showLoginWindow();
    }
}
