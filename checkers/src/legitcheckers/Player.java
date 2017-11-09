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

    Player(int playerNumberber) {
        this.playerNumber = playerNumberber;
    }

    private int estimateDistance(Location one, Location two) {
        return Math.max(Math.abs(one.col - two.col), Math.abs(one.row - two.row));
    }

    int chooseAIMove(Game game) {
        long bestValue;
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
            bestValue = alphaBetaPrune(game, depth, depth,  /*Long.MIN_VALUE/*/Constants.minVal, /*Long.MAX_VALUE/*/Constants.maxVal);
            if (!outOfTime)
                completedBestMove = bestMove;
            else maxDepth--;

            currTimeMS = (new Date()).getTime();

            if (outOfTime)
                break;
        }

        if (completedBestMove <= 0 || completedBestMove >= game.availableMoves.size()) {
            System.out.println("Found value : " + bestMove);
            System.out.println("Found completed best move : " + completedBestMove);
            completedBestMove = (new Random()).nextInt(game.availableMoves.size()) + 1;
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

    private long calculateHeuristic(Game game) {
        long numPieces = numPiecesValue(game, 1) - numPiecesValue(game, (1 % 2) + 1);
        long avgToKing = (kingDistance(game, (1 % 2) + 1) - kingDistance(game, 1));  //* 99 / 7;
        long piecesLeft = piecesLeftWeight(game, 1);
        long kingLoc = 0;
        long randomSafety = (new Random()).nextInt(9);
        // System.out.println(String.format("%d %d %d %d %d", numPieces, avgToKing, piecesLeft, kingLoc, randomSafety));
        return (numPieces * 1000000) + (avgToKing * 100000) + (piecesLeft * 50000) + (kingLoc * 10) + (randomSafety);
    }
//

    private long numPiecesValue(Game game, int playerNumber) {
        long retVal = 0;
        if (playerNumber == 1) {
            for (Location loc : game.pieces[1]) {
                retVal += (game.board[loc.row][loc.col] > 2) ? 5 : 3;
            }
        } else {
            for (Location loc : game.pieces[0]) {
                retVal += (game.board[loc.row][loc.col] > 2) ? 5 : 3;
            }
        }
        return retVal;
    }

    private long kingDistance(Game game, int playerNumber) {
        //System.out.println("Player Number " + playerNumber);
        long retVal = 0;
        int numPawns = 0;
        if (playerNumber == 1) {
            for (Location loc : game.pieces[1]) {
                if (!(game.board[loc.row][loc.col] > 2)) {
                    retVal += (7 - loc.row);
                    //          System.out.println(loc + " " + (7 - loc.row) + " " + retVal);
                    numPawns++;
                }
            }
//            System.out.println(game);
//            System.out.println(retVal + "\n\n\n");
        } else {
            for (Location loc : game.pieces[0]) {
                if (!(game.board[loc.row][loc.col] > 2)) {
                    retVal += (loc.row);
                    //        System.out.println(loc + " " + (loc.row) + " " + retVal);
                    numPawns++;
                }
            }
//            System.out.println(game);
//            System.out.println(retVal + "\n\n\n");

        }
        if (numPawns == 0) {
            return 0;
        } else {
            // System.out.println(numPawns);
            return retVal / numPawns;
        }
    }

    private long piecesLeftWeight(Game game, int playerNumber) {
        int numPieces = game.pieces[1].size() + game.pieces[0].size();
        int blackPieces = game.pieces[1].size();
        int redPieces = game.pieces[0].size();
        if (playerNumber == 1) {
            if (blackPieces > redPieces) {
                return (24 - numPieces) * 99 / 24;
            } else {
                return (numPieces) * 99 / 24;
            }
        } else {
            if (redPieces > blackPieces) {
                return (24 - numPieces) * 99 / 24;
            } else {
                return (numPieces) * 99 / 24;
            }
        }
    }

    private long alphaBetaPrune(Game gameNode, int depth, int second, long alpha, long beta) {

        if (!outOfTime) {


//            if (depth == second) {
//                calculateHeuristicValues(gameNode);
//                //System.out.println(gameNode);
//            }


            if (((new Date()).getTime() - startTimeMS) > 0.998 * Constants.timeLimit) {
                outOfTime = true;
                return 0;
            }

            gameNode.getNextMoves();

            if (isTerminalState(gameNode) || depth == 0) {
                return (calculateHeuristic(gameNode));
            }


            if (gameNode.currTurn == 1) {
                long v = Constants.minVal;
                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    Game childGame = gameNode.cloneGame(child);
                    childGame.chooseMove(1);

                    long ret = alphaBetaPrune(childGame, depth - 1, second, alpha, beta);

                    if (ret > v) {
                        v = ret;
                        if (depth == maxDepth)
                            bestMove = child;
                    }

                    alpha = Math.max(alpha, v);

                    if (beta <= alpha)
                        break;

                }
                return v;
            } else {
                long v = Constants.maxVal;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    Game childGame = gameNode.cloneGame(child);
                    childGame.chooseMove(1);
                    long ret = alphaBetaPrune(childGame, depth - 1, second, alpha, beta);

                    if (ret < v) {
                        v = ret;
                        if (depth == maxDepth)
                            bestMove = child;
                    }
                    beta = Math.min(beta, v);

                    if (beta <= alpha)
                        break;
                }
                return v;
            }
        } else {
            return 0;
        }
    }
}
