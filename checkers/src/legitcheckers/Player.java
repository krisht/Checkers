/*
 * Krishna Thiyagarajan
 * ECE-469: Artificial Intelligence
 * Professor Sable
 * November 6, 2017
 */

package legitcheckers;

import java.util.Date;
import java.util.Random;

class Player {
    private final int playerNumber;
    private int bestMove, maxDepth;
    private long startTimeMS, currTimeMS;
    private boolean outOfTime;

    Player(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    int chooseAIMove(Game game) {
        long bestValue = -1;
        int completedBestMove = 1;
        int startingDepth = 5;

        if (Constants.timeLimit > 4000)
            startingDepth = 7;
        if (game.pieces[0].size() + game.pieces[1].size() <= 10) {
            startingDepth += 1;
        }
        if (game.pieces[0].size() + game.pieces[1].size() <= 5) {
            startingDepth += 1;
        }

        outOfTime = false;
        startTimeMS = (new Date()).getTime();

        for (int depth = startingDepth; depth < Constants.maxDepth; depth++) {
            maxDepth = depth;
            bestValue = alphaBetaPrune(game, depth, Long.MIN_VALUE/*Constants.minVal*/, Long.MAX_VALUE/*Constants.maxVal*/);
            if (!outOfTime)
                completedBestMove = bestMove;
            else maxDepth--;

            currTimeMS = (new Date()).getTime();

            if (outOfTime)
                break;
        }


        Constants.clearScreen();

        if (maxDepth == Constants.maxDepth - 1)
            System.out.println(String.format("Previous Move:\nReached the maximum allowable depth, %d, in %d milliseconds.", Constants.maxDepth, (currTimeMS - startTimeMS)));
        else
            System.out.println(String.format("Previous Move:\nReached %s depth in %d milliseconds.", Constants.ordinal(maxDepth), (currTimeMS - startTimeMS)));

        if (outOfTime)
            System.out.println(String.format("Time ran out while searching %s depth.", Constants.ordinal(maxDepth + 1)));
        else System.out.print("\n");


        System.out.println(String.format("The AI for %s Player chose the %s move.", game.currTurn == 0 ? "Red" : "Black", Constants.ordinal(completedBestMove)));

        game.getNextMoves();
        return completedBestMove;
    }

    private boolean isTerminalState(Game game) {
        return (game.availableMoves.isEmpty() || (game.numWalkMoves == Constants.movesToDraw));
    }

    private long utilityFunction(Game game, int depth) {
        if (game.numWalkMoves == Constants.movesToDraw)
            return 0;
        if (game.currTurn == 1) { // Player 1's turn and has 0 moves available, higher depth = closer to winning
            return Long.MIN_VALUE; //Constants.minVal*(depth+1);
        } else { // Player 2's turn and has 0 moves available
            return Long.MAX_VALUE;//Constants.maxVal * (depth+1);
        }
    }

    private long evaluateState(Game game) {
        int score;
        int piecesValue = 0, positionValue = 0, differenceValue, advantageValue = 0;
        int playerAdvantage = -1;

        if (game.pieces[1].size() > game.pieces[0].size())
            playerAdvantage = 1;
        else if (game.pieces[1].size() < game.pieces[0].size())
            playerAdvantage = 0;

        for (Location loc : game.pieces[1]) {
            if (game.board[loc.row][loc.col] > 2) {
                piecesValue += 5; //Kings

                if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) { //About to loose
                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0)) {
                        advantageValue += 9;
                    }
                } else if ((playerAdvantage == 1) && (game.pieces[0].size() < 3)) { // If Player 2 occupying top left corner
                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 0)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 0))) {
                        if ((loc.row < 2) && (loc.col == 0)) {
                            advantageValue += 7;
                        }
                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
                            advantageValue += 5;
                        } else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
                            advantageValue += 3;
                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
                            advantageValue += 3;
                        }

                    } else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 0)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 0))) { //Player two occupies bottom corner
                        if ((loc.row > 5) && (loc.col == 3)) {
                            advantageValue += 7;
                        }
                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
                            advantageValue += 5;
                        } else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
                            advantageValue += 3;
                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
                            advantageValue += 3;
                        }
                    }


                }
            } else { //Pawns
                piecesValue += 3;
                if (loc.row == 7) {
                    positionValue += 9; //Stay in back row to defend
                } else {
                    positionValue += (7 - loc.row); //Closer to king means better
                }
            }
        }

        for (Location loc : game.pieces[0]) {
            if (game.board[loc.row][loc.col] > 2) {
                // Kings
                piecesValue -= 5;
                if ((playerAdvantage == 1) && (game.pieces[0].size() < 3)) {
                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0)) {
                        advantageValue -= 9;
                    }
                } else if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) {
                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 1)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 1))) {
                        if ((loc.row < 2) && (loc.col == 0)) {
                            advantageValue -= 7;
                        }

                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
                            advantageValue -= 5;
                        } else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
                            advantageValue -= 3;
                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
                            advantageValue -= 3;
                        }

                    } else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 1)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 1))) {
                        if ((loc.row > 5) && (loc.col == 3)) {
                            advantageValue -= 7;
                        }
                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
                            advantageValue -= 5;
                        } else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
                            advantageValue -= 3;
                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
                            advantageValue -= 3;
                        }
                    }
                }
            } else {
                piecesValue -= 3;

                if (loc.row == 0) {
                    positionValue -= 9;
                } else {
                    // Closer to being king, row = 7, the better
                    positionValue -= loc.row;
                }
            }
        }

        piecesValue = piecesValue * 1000000;
        positionValue = positionValue * 100000;
        // Difference in number of pieces
        differenceValue = (game.pieces[1].size() - game.pieces[0].size()) * 1000;
        advantageValue = advantageValue * 10;
        score = piecesValue + positionValue + differenceValue + advantageValue;
        return score;
    }

    private long alphaBetaPrune(Game gameNode, int depth, long alpha, long beta) {

        if (!outOfTime) {

            if (((new Date()).getTime() - startTimeMS) > 0.998 * Constants.timeLimit) {
                outOfTime = true;
                return 0;
            }

            gameNode.getNextMoves();

            if (isTerminalState(gameNode)) { // Check for terminal state: if either player has 0 moves = someone wins, or if tie, and return that utility score
                return utilityFunction(gameNode, depth);
            }
            if (depth == 0) {
                return (evaluateState(gameNode));
            }

            if (gameNode.currTurn == 1) {
                long bestValue = alpha;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    //System.out.println("hello");
                    Game childGame = gameNode.cloneGame(child);
                    childGame.chooseMove(1, 0);
                    long ret = alphaBetaPrune(childGame, (depth - 1), bestValue, beta);
                    if (ret > bestValue) {
                        bestValue = ret;
                        if (depth == maxDepth) {
                            bestMove = child;
                        }
                    } else if ((ret == bestValue) && (depth == maxDepth)) {
                        if ((new Random()).nextBoolean()) {
                            bestMove = child;
                            //System.out.println(childGame);
                        }
                    }
                    if (beta <= bestValue) { // Beta cut-off = prune remaining branches
                        break;
                    }
                }
                return bestValue;
            } else {
                long bestValue = beta;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    //System.out.println("hello1");
                    Game childGame = gameNode.cloneGame(child);
                    childGame.chooseMove(1, 0);

                    long ret = alphaBetaPrune(childGame, (depth - 1), alpha, bestValue);
                    if (ret < bestValue) {
                        bestValue = ret;
                        if (depth == maxDepth) {
                            bestMove = child;
                        }
                    } else if ((ret == bestValue) && (depth == maxDepth)) {
                        if ((new Random()).nextBoolean()) {
                            bestMove = child;
                        }
                    }
                    if (bestValue <= alpha) { // Alpha cut-off = prune remaining branches
                        break;
                    }
                }
                return bestValue;
            }
        } else {
            return 0;
        }
    }

}
