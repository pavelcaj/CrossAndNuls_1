package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrossAndNuls extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isXTurn = true;
    private boolean playWithBot = false; // Режим игры: с ботом или игроком

    public CrossAndNuls() {
        setTitle("Cross and Nuls");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Выбор режима игры
        chooseGameMode();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));
        initializeGameBoard(panel);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        add(panel, BorderLayout.CENTER);
        add(resetButton, BorderLayout.NORTH); // Кнопка Reset в верхней части

        setVisible(true);
    }

    private void chooseGameMode() {
        String[] options = {"Играть с ботом", "Играть с другом"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Выберите режим игры:",
                "Режим игры",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        playWithBot = choice == 0; // Если выбрано "Играть с ботом", устанавливаем режим
    }

    private void initializeGameBoard(JPanel panel) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 50));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                panel.add(buttons[i][j]);
            }
        }
    }

    private void resetGame() {
        isXTurn = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
    }

    private boolean checkWinner(String player) {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(player) &&
                    buttons[i][1].getText().equals(player) &&
                    buttons[i][2].getText().equals(player)) {
                return true;
            }
            if (buttons[0][i].getText().equals(player) &&
                    buttons[1][i].getText().equals(player) &&
                    buttons[2][i].getText().equals(player)) {
                return true;
            }
        }
        // Проверка диагоналей
        if (buttons[0][0].getText().equals(player) &&
                buttons[1][1].getText().equals(player) &&
                buttons[2][2].getText().equals(player)) {
            return true;
        }
        if (buttons[0][2].getText().equals(player) &&
                buttons[1][1].getText().equals(player) &&
                buttons[2][0].getText().equals(player)) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void endGame(String message) {
        JOptionPane.showMessageDialog(this, message);
        resetGame();
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().isEmpty()) {
                buttons[row][col].setText(isXTurn ? "X" : "O");
                buttons[row][col].setEnabled(false);

                String currentPlayer = isXTurn ? "X" : "O";
                if (checkWinner(currentPlayer)) {
                    endGame("Игрок " + currentPlayer + " победил!");
                } else if (isBoardFull()) {
                    endGame("Ничья!");
                } else {
                    isXTurn = !isXTurn;

                    // Ход бота
                    if (playWithBot && !isXTurn) {
                        botMove();
                    }
                }
            }
        }
    }


    private void botMove() {
        int[] bestMove = findBestMove();
        buttons[bestMove[0]][bestMove[1]].setText("O");
        buttons[bestMove[0]][bestMove[1]].setEnabled(false);

        if (checkWinner("O")) {
            endGame("Бот победил!");
        } else if (isBoardFull()) {
            endGame("Ничья!");
        }
        isXTurn = true;

    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText("O");
                    int score = minimax(false);
                    buttons[i][j].setText("");

                    if (score > bestScore) {
                        bestScore = score;
                        move = new int[]{i, j};
                    }
                }
            }
        }
        return move;
    }

    private int minimax(boolean isMaximizing) {
        if (checkWinner("O")) return 1; // Бот победил
        if (checkWinner("X")) return -1; // Игрок победил
        if (isBoardFull()) return 0; // Ничья

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText(isMaximizing ? "O" : "X");
                    int score = minimax(!isMaximizing);
                    buttons[i][j].setText("");

                    if (isMaximizing) {
                        bestScore = Math.max(bestScore, score);
                    } else {
                        bestScore = Math.min(bestScore, score);
                    }
                }
            }
        }
        return bestScore;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CrossAndNuls::new);
    }
}