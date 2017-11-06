package legitcheckers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Runner {

    private Game game;
    private Player playerOne, playerTwo;
    private boolean gameOver = false;
    private boolean staleMate = false;
    private int playerOneMode;
    private int playerTwoMode;

    public static void main(String[] args) throws IOException, IllegalArgumentException, InterruptedException {
        System.out.print("\033[1;97m\u250F");
        for (int ii = 0; ii < 32; ii++)
            System.out.print("\u2501");
        System.out.println("\u2513");

        System.out.println("\u2503   Radhakrishnan Thiyagarajan   \u2503");
        System.out.println("\u2503    Artificial Intelligence     \u2503");
        System.out.println("\u2503     Professor Carl Sable       \u2503");

        System.out.print("\u2517");
        for (int ii = 0; ii < 32; ii++)
            System.out.print("\u2501");
        System.out.println("\u251B\n\n");


        String userChoice;
        int moveChoice;
        Scanner scan = new Scanner(System.in);

        Runner runner = new Runner();
        runner.initializeGame();

        while (true) {
            System.out.print("Is Black (Player #1) going to be an AI? (Y/N): ");
            userChoice = scan.nextLine().toUpperCase();
            if (userChoice.equals("Y") || userChoice.equals("N")) {
                runner.playerOneMode = userChoice.equals("Y") || userChoice.equals("YES") ? Constants.computerMode : Constants.playerMode;
                break;
            }
        }
        while (true) {
            System.out.print("Is Red (Player #2) going to be an AI? (Y/N): ");
            userChoice = scan.nextLine().toUpperCase();
            if (userChoice.equals("Y") || userChoice.equals("N")) {
                runner.playerTwoMode = userChoice.equals("Y") || userChoice.equals("YES") ? Constants.computerMode : Constants.playerMode;
                break;
            }
        }

        if (runner.playerOneMode == Constants.computerMode || runner.playerTwoMode == Constants.computerMode) {
            while (true) {
                System.out.print("Time for AI in seconds greater than 0: ");
                userChoice = scan.nextLine();
                int temp;
                try {
                    temp = Integer.parseInt(userChoice);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (temp >= 0) {
                    Constants.timeLimit = temp * 1000;
                    break;
                }
            }
        }

        System.out.print("Specify a game board file to load or press enter to skip: ");
        userChoice = scan.nextLine();

        if (userChoice.length() != 0) {
            if (runner.loadGame(userChoice))
                System.out.println("Successfully loaded in game! Press enter to continue...");
            else {
                System.out.println("File '" + userChoice + "' was improperly structured. Resetting game to default start state. Press enter to continue...");
                runner.initializeGame();
            }
            scan.nextLine();
        }

        while (!(runner.gameOver || runner.staleMate)) {

            runner.game.printBoard();

            //Player One turn
            if (runner.game.currTurn == 1) {

                runner.game.printNextMoves();

                if (runner.game.availableMoves.size() > 0 && runner.playerOneMode > 0) {

                    while (true) {
                        System.out.print(String.format("Select move for Black player between 1 and %d or press Ctrl + C to exit: ", runner.game.availableMoves.size()));
                        userChoice = scan.nextLine();
                        try {
                            moveChoice = Integer.parseInt(userChoice);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        if (!(moveChoice > runner.game.availableMoves.size() || moveChoice < 0))
                            break;
                    }

                    runner.game.chooseMove(moveChoice);

                } else if (runner.game.availableMoves.size() > 0) {
                    //Player One is an AI
                    if (runner.game.availableMoves.size() > 1) {
                        System.out.println("Black's AI is searching for an optimal move...");
                        moveChoice = runner.playerOne.chooseAIMove(runner.game);
                        runner.game.chooseMove(moveChoice);
                    } else runner.game.chooseMove(1);

                } else {
                    runner.gameOver = true;
                    break;
                }

            } else {

                runner.game.printNextMoves();

                if (runner.game.availableMoves.size() > 0 && runner.playerTwoMode > 0) {

                    while (true) {
                        System.out.print(String.format("Select move for Red player between 1 and %d or press Ctrl + C to exit: ", runner.game.availableMoves.size()));
                        userChoice = scan.nextLine();
                        try {
                            moveChoice = Integer.parseInt(userChoice);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        if (!(moveChoice > runner.game.availableMoves.size() || moveChoice < 0))
                            break;
                    }

                    runner.game.chooseMove(moveChoice);

                } else if (runner.game.availableMoves.size() > 0) {
                    //Player Two is an AI
                    if (runner.game.availableMoves.size() > 1) {
                        System.out.println("Red's AI is searching for an optimal move...");
                        moveChoice = runner.playerTwo.chooseAIMove(runner.game);
                        runner.game.chooseMove(moveChoice);
                    } else runner.game.chooseMove(1);

                } else {
                    runner.gameOver = true;
                    break;
                }

            }

            if (runner.game.numWalkMoves == Constants.movesToDraw)
                runner.staleMate = true;
            Constants.clearScreen();
        }

        System.out.println("Game over!");

        if (runner.staleMate) {
            System.out.println("Since no piece has been captured in the last " + Constants.movesToDraw + " moves, this game ends in a draw.");
            System.out.println(Constants.gameDraw);
        } else {
            if (runner.game.currTurn % 2 == 1) {
                System.out.println("Red player won!");
                System.out.println(Constants.winPlayerTwo);
            }
            else {
                System.out.println("Black player won!");
                System.out.println(Constants.winPlayerOne);
            }
        }
    }

    private void initializeGame() {
        playerOne = new Player(1);
        playerTwo = new Player(0);
        game = new Game(Constants.startPlayer);

        game.pieces[0].clear();
        game.pieces[1].clear();
        game.availableMoves.clear();


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                game.board[i][j] = Constants.regularPlayerTwo;
                game.board[7 - i][j] = Constants.regularPlayerOne;
                game.pieces[0].add(new Location(i, j));
                game.pieces[1].add(new Location(7 - i, j));
            }
        }

        for (int j = 0; j < 4; j++) {
            game.board[3][j] = Constants.emptyPlayer;
            game.board[4][j] = Constants.emptyPlayer;
        }
    }

    private boolean loadGame(String input) {

        playerOne = new Player(1);
        playerTwo = new Player(0);
        game.pieces[0].clear();
        game.pieces[1].clear();
        game.availableMoves.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));


            for (int ii = 0; ii < 8; ii++) {
                String[] numbers = reader.readLine().trim().split("\\s+");
                for (int jj = 0; jj < 4; jj++) {
                    int val = Integer.parseInt(numbers[jj]);
                    game.board[ii][jj] = val;

                    if (val < 0 || val > 4) {
                        System.out.println("Checkers file is in an incorrect format!");
                        this.initializeGame();
                        return false;
                    }

                    if (val != 0 && val % 2 == 0)
                        game.pieces[0].add(new Location(ii, jj));
                    else if (val != 0)
                        game.pieces[1].add(new Location(ii, jj));
                }
            }

            int c;

            if ((c = Integer.parseInt(reader.readLine())) >= 0) {
                System.out.println(c);
                if (c == 2)
                    game.currTurn = 0;
                else game.currTurn = 1;
            } else throw new IllegalArgumentException("Checkers file has no current turn!");

            if ((c = Integer.parseInt(reader.readLine())) >= 0) {
                Constants.timeLimit = c * 1000;
            } else System.out.println("Using default time of 10 seconds.");
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file: " + input);
            return false;
        } catch (IOException ex) {
            System.out.println("Unable to close file " + input + " properly.");
            System.exit(-1);
        }

        game.getNextMoves();

        return true;
    }

}


