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
        bestMove = -1;

        int completedBestMove = 0;
        int startingDepth = 5;
        long bestValue;
        if (Constants.timeLimit > 4000)
            startingDepth = 7;

        if (game.pieces[0].size() + game.pieces[1].size() <= 10)
            startingDepth += 1;
        if (game.pieces[0].size() + game.pieces[1].size() <= 5)
            startingDepth += 1;

        outOfTime = false;
        startTimeMS = (new Date()).getTime();

        for (int d = startingDepth; d < Constants.maxDepth; d++) {
            maxDepth = d;

            bestValue = alphaBetaPrune(game, d, Constants.minVal, Constants.maxVal);


            if (!outOfTime)
                completedBestMove = bestMove;
            else maxDepth--;

            currTimeMS = (new Date()).getTime();

            if (outOfTime || bestValue <= Constants.minVal || bestValue >= Constants.maxVal)
                break;
        }

        if (bestMove < 1)
            completedBestMove = (new Random()).nextInt(game.availableMoves.size()) + 1;

        if (maxDepth == Constants.maxDepth - 1)
            System.out.println(String.format("Reached the maximum allowable depth, %d, in %d milliseconds.", Constants.maxDepth, (currTimeMS - startTimeMS)));
        else
            System.out.println(String.format("Reached %s depth in %d milliseconds.", Constants.ordinal(maxDepth), (currTimeMS - startTimeMS)));

        if (outOfTime)
            System.out.println(String.format("Time ran out while searching %s depth.", Constants.ordinal(maxDepth + 1)));


        System.out.println(String.format("The AI for %s Player chooses the %s move.", game.currTurn == 0 ? "Red" : "Black", Constants.ordinal(completedBestMove)));

        game.getNextMoves();
        return completedBestMove;
    }

    private boolean isTerminalState(Game game) {
        return game.availableMoves.size() == 0 || game.numWalkMoves == Constants.movesToDraw;
    }

    private long utilityFunction(Game game, int depth) {
        if (game.numWalkMoves == Constants.movesToDraw)
            return 0;
        return game.currTurn == 1 ? Constants.minVal * (depth + 1) : Constants.maxVal * (depth + 1);
    }

    private long evaluateState(Game game) {
        int score;
        int pieceDiff = 0, posDiff = 0, endGameValue = 0;
        int adv = -1;

        if (game.pieces[1].size() > game.pieces[0].size())
            adv = 1;
        else if (game.pieces[1].size() < game.pieces[0].size())
            adv = 0;

        for (Location loc : game.pieces[1]) {
            if (game.board[loc.row][loc.col] > 2) {

                pieceDiff += 5;


                if ((adv == 0) && (game.pieces[1].size() < 3)) {
                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0)) {
                        endGameValue += 9;
                    }
                } else if ((adv == 1) && (game.pieces[0].size() < 3)) {

                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 0)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 0))) {


                        if ((loc.row < 2) && (loc.col == 0)) {
                            endGameValue += 7;
                        }

                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
                            endGameValue += 5;
                        } else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
                            endGameValue += 3;
                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
                            endGameValue += 3;
                        }

                    } else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 0)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 0))) {


                        if ((loc.row > 5) && (loc.col == 3)) {
                            endGameValue += 7;
                        }

                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
                            endGameValue += 5;
                        } else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
                            endGameValue += 3;
                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
                            endGameValue += 3;
                        }
                    }


                }
            } else {

                pieceDiff += 3;


                if (loc.row == 7) {
                    posDiff += 9;
                } else {

                    posDiff += (7 - loc.row);
                }
            }
        }

        for (Location loc : game.pieces[0]) {
            if (game.board[loc.row][loc.col] > 2) {

                pieceDiff -= 5;


                if ((adv == 1) && (game.pieces[0].size() < 3)) {
                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0))
                        endGameValue -= 9;
                } else if ((adv == 0) && (game.pieces[1].size() < 3)) {

                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 1)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 1))) {

                        if ((loc.row < 2) && (loc.col == 0))
                            endGameValue -= 7;

                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1))
                            endGameValue -= 5;
                        else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1))
                            endGameValue -= 3;
                        else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1))
                            endGameValue -= 3;

                    } else if ((game.board[6][3] > 0 && game.board[6][3] % 2 == 1) || (game.board[7][3] > 0 && game.board[7][3] % 2 == 1)) {

                        if (loc.row > 5 && loc.col == 3)
                            endGameValue -= 7;

                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3))
                            endGameValue -= 5;
                        else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2))
                            endGameValue -= 3;
                        else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2))
                            endGameValue -= 3;
                    }
                }
            } else {

                pieceDiff -= 3;

                if (loc.row == 0)
                    posDiff -= 9;
                else posDiff -= loc.row;
            }
        }

        pieceDiff = pieceDiff * 1000000;
        posDiff = posDiff * 100000;

        int differenceValue = (game.pieces[1].size() - game.pieces[0].size()) * 1000;
        endGameValue = endGameValue * 10;
        score = pieceDiff + posDiff + differenceValue + endGameValue;
        return playerNumber == 1 ? score : -score;
    }


    private long alphaBetaPrune(Game gameNode, int depth, long alpha, long beta) {

        if (!outOfTime) {

            if ((new Date()).getTime() - startTimeMS > 0.998 * Constants.timeLimit) {
                outOfTime = true;
                return 0;
            }

            gameNode.getNextMoves();


            if (isTerminalState(gameNode))
                return (utilityFunction(gameNode, depth));

            if (depth == 0)
                return evaluateState(gameNode);

            if (gameNode.currTurn == 1) {
                long bestValue = alpha;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    Game childGame = gameNode.clone(child);
                    childGame.chooseMove(1);
                    long ret = alphaBetaPrune(childGame, (depth - 1), bestValue, beta);
                    if (ret > bestValue) {
                        bestValue = ret;
                        if (depth == maxDepth)
                            bestMove = child;
                    } else if ((ret == bestValue) && (depth == maxDepth))
                        if ((new Random()).nextBoolean())
                            bestMove = child;
                    if (beta <= bestValue)
                        break;
                }
                return bestValue;
            } else {
                long bestValue = beta;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {

                    Game childGame = gameNode.clone(child);
                    childGame.chooseMove(1);

                    long ret = alphaBetaPrune(childGame, (depth - 1), alpha, bestValue);
                    if (ret < bestValue) {
                        bestValue = ret;
                        if (depth == maxDepth)
                            bestMove = child;
                    } else if ((ret == bestValue) && (depth == maxDepth))
                        if ((new Random()).nextBoolean())
                            bestMove = child;

                    if (bestValue <= alpha)
                        break;
                }
                return bestValue;
            }
        } else return 0;

    }
}
