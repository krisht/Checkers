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

    private boolean isBottomRow(Location loc) {
        return loc.row == 7;
    }

    private boolean isTopRow(Location loc) {
        return loc.row == 0;
    }

    private boolean isLeftColumn(Location loc) {
        return (loc.row % 2 == 1 && loc.col == 0);
    }

    private boolean isRightColumn(Location loc) {
        return (loc.row % 2 == 0 && loc.col == 3);
    }

    private boolean isCenterPiece(Location loc) {
        return (loc.row == 3 || loc.row == 4) && (loc.col == 1 || loc.col == 2);
    }

    private boolean isTrapped(Location loc) {
        return (loc.row == 7 && loc.col == 0) || (loc.row == 0 && loc.col == 3);
    }

    private boolean isDiagKing(Game game, Location loc) {
        if (loc.row == 7 && loc.col == 3) {
            return game.board[6][3] == 0;
        }

        if (loc.row == 6 && loc.col == 3) {
            return game.board[7][3] == 0;
        }

        if (loc.row == 1 && loc.col == 0) {
            return game.board[0][0] == 0;
        }

        return loc.row == 0 && loc.col == 0 && game.board[1][0] == 0;
    }

    private long getDistancePenalty(Game game) {

        int totalDistance = 0;

        for (Location l1 : game.pieces[0]) {
            for (Location l2 : game.pieces[1]) {
                if (game.board[l1.row][l1.col] > 2 && game.board[l2.row][l2.col] > 2)
                    totalDistance += estimateDistance(l1, l2);
            }
        }

        return totalDistance;
    }


//    private long evaluateState(Game game) {
//
//        int blackPawns, blackKings, redPawns, redKings;
//        int blackCenter, redCenter;
//        int blackLineRow, redLineRow, blackLineCol, redLineCol;
//        int blackTrapped, redTrapped;
//        int blackDiagonal, redDiagonal;
//
//
//        blackPawns = blackKings = redPawns = redKings = 0;
//        blackCenter = redCenter = 0;
//        blackLineRow = redLineRow = blackLineCol = redLineCol = 0;
//        blackTrapped = redTrapped = 0;
//        blackDiagonal = redDiagonal = 0;
//
//        for (Location loc : game.pieces[1]) {
//
//            if (isCenterPiece(loc))
//                blackCenter++;
//
//            if (isBottomRow(loc))
//                blackLineRow++;
//
//            if (isRightColumn(loc))
//                blackLineCol++;
//
//            if (game.board[loc.row][loc.col] > 2) {
//                blackKings++;
//
//                if (isTrapped(loc))
//                    blackTrapped++;
//
//                if (isDiagKing(game, loc))
//                    blackDiagonal++;
//            } else {
//                blackPawns++;
//            }
//        }
//
//        for (Location loc : game.pieces[0]) {
//
//            if (isCenterPiece(loc))
//                redCenter++;
//
//            if (isTopRow(loc))
//                redLineRow++;
//
//            if (isLeftColumn(loc))
//                redLineCol++;
//
//            if (game.board[loc.row][loc.col] > 2) {
//                redKings++;
//
//                if (isTrapped(loc))
//                    redTrapped++;
//
//                if (isDiagKing(game, loc))
//                    redDiagonal++;
//
//            } else {
//                redPawns++;
//            }
//        }
//
//
//        long mobility = Math.round(game.availableMoves.size() + Math.pow(10.0, Constants.minVal));
//
//        long distance = getDistancePenalty(game);
//
//
//        long score = blackPawns * 100 + blackKings * 140 - redPawns * 100 - redKings * 140;
//        if ((game.currTurn == 1 && (blackPawns + blackKings) > 2) || (game.currTurn == 0 && (redPawns + redKings) > 2)) {
//            score += blackCenter * 10 - redCenter * 10;
//            score += -blackLineRow * 5 - blackLineCol * 3 + redLineRow * 5 + redLineCol * 3;
//        }
//        if ((game.currTurn == 1 && (blackPawns + blackKings) <= 3) || (game.currTurn == 0 && (redPawns + redKings) <= 3))
//            score += (-blackTrapped * 10 + redTrapped * 10);
//        if ((game.currTurn == 1 && blackKings <= 2) || (game.currTurn == 0 && redKings <= 2))
//            score += (blackKings * 30 - redKings * 30);
//        score += distance * (redKings - blackKings) * 30;
//        score += mobility;
//
//        return score;
//
//    }

    private long calculateHeuristic(Game game) {
        long numPieces = numPiecesValue(game, playerNumber) - numPiecesValue(game, (playerNumber % 2) + 1);
        long avgToKing = (kingDistance(game, (playerNumber % 2) + 1) - kingDistance(game, playerNumber));  //* 99 / 7;
        long piecesLeft = piecesLeftWeight(game, playerNumber);
        long kingLoc = 0;
        long randomSafety = (new Random()).nextInt(9);
        // System.out.println(String.format("%d %d %d %d %d", numPieces, avgToKing, piecesLeft, kingLoc, randomSafety));
        return (numPieces * 10000000) + (avgToKing * 100000) + (piecesLeft * 1000) + (kingLoc * 10) + (randomSafety);
    }

    private void calculateHeuristicValues(Game game) {
        long numPieces = numPiecesValue(game, playerNumber) - numPiecesValue(game, (playerNumber % 2) + 1);
        long avgToKing = (kingDistance(game, (playerNumber % 2) + 1) - kingDistance(game, playerNumber));  //* 99 / 7;
        long piecesLeft = piecesLeftWeight(game, playerNumber);
        long kingLoc = 0;
        long randomSafety = (new Random()).nextInt(9);
        System.out.println(String.format("%d %d %d %d %d", numPieces, avgToKing, piecesLeft, kingLoc, randomSafety));
    }


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
        System.out.println("Player Number " + playerNumber);
        long retVal = 0;
        int numPawns = 0;
        if (playerNumber == 1) {
            for (Location loc : game.pieces[1]) {
                if (!(game.board[loc.row][loc.col] > 2)) {
                    retVal += (7 - loc.row);
                    System.out.println(loc + " " + (7 - loc.row) + " " + retVal);
                    numPawns++;
                }
            }
//            System.out.println(game);
//            System.out.println(retVal + "\n\n\n");
        } else {
            for (Location loc : game.pieces[0]) {
                if (!(game.board[loc.row][loc.col] > 2)) {
                    retVal += (loc.row);
                    System.out.println(loc + " " + (loc.row) + " " + retVal);
                    numPawns++;
                }
            }
//            System.out.println(game);
//            System.out.println(retVal + "\n\n\n");

        }
        if (numPawns == 0) {
            return 0;
        } else {
            System.out.println(numPawns);
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


            if (depth == second) {
                calculateHeuristicValues(gameNode);
                System.out.println(gameNode);
            }


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

                    v = Math.max(v, alphaBetaPrune(childGame, depth - 1, second, alpha, beta));

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
                    v = Math.min(v, alphaBetaPrune(childGame, depth - 1, second, alpha, beta));
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
