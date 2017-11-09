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

    private int estimateDistance(Location one, Location two) {
        return Math.max(Math.abs(one.col - two.col), Math.abs(one.row - two.row));
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
            bestValue = alphaBetaPrune(game, depth, depth - 2,  /*Long.MIN_VALUE/*/Constants.minVal, /*Long.MAX_VALUE/*/Constants.maxVal);
            if (!outOfTime)
                completedBestMove = bestMove;
            else maxDepth--;

            currTimeMS = (new Date()).getTime();
            //System.out.println(String.format("Depth %d, Best Value %d", depth, bestValue));

            if (outOfTime)
                break;
        }

        System.out.println(game.availableMoves.size());

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

    private long utilityFunction(Game game, int depth) {
        if (game.numWalkMoves == Constants.movesToDraw)
            return 0;
        if (game.currTurn == 1) { // Player 1's turn and has 0 moves available, higher depth = closer to winning
            return Constants.minVal;//Long.MIN_VALUE; //Constants.minVal*(depth+1);
        } else { // Player 2's turn and has 0 moves available
            return Constants.maxVal; //Long.MAX_VALUE;//Constants.maxVal * (depth+1);
        }
    }

    private boolean isCenterPiece(Location loc) {
        return (loc.row < 5 && loc.row > 2 && (loc.col == 1 || loc.col == 2));
    }

    private boolean isTopRow(Location loc) {
        return loc.row == 0;
    }

    private boolean isBottomRow(Location loc) {
        return loc.row == 7;
    }

    private boolean isRightColumn(Location loc) {
        return loc.row % 2 == 0 && loc.col == 3;
    }

    private boolean isLeftColumn(Location loc) {
        return loc.row % 2 == 1 && loc.col == 0;
    }

    private boolean isTrapped(Location loc) {
        //penalize single corners  and double corners
        return (loc.row == 7 && loc.col == 0) || (loc.row == 0 && loc.col == 3);
    }

    private boolean isCornerPiece(Location loc) {
        return ((loc.row == 6 || loc.row == 7) && loc.col == 3) || ((loc.row == 0 || loc.row == 1) && loc.col == 0);
    }

    private boolean isDiagKing(Game game, Location loc) {
        if ((loc.row == 7 && loc.col == 3 && game.board[6][3] == 0) || (loc.row == 6 && loc.col == 3 && game.board[7][3] == 0))
            return true;
        if ((loc.row == 0 && loc.col == 0 && game.board[1][0] == 0) || (loc.row == 1 && loc.col == 0 && game.board[0][0] == 0))
            return true;
        return false;
    }

    private long getDistancePenalty(Game game, int blackKings, int redKings) {


        if (blackKings == 1 && redKings == 2) {
            Location b1Loc = null;
            Location r1Loc = null, r2Loc = null;
            for (Location loc : game.pieces[1]) {
                if (game.board[loc.row][loc.col] == 3) {
                    b1Loc = loc;
                    break;
                }
            }

            for (Location loc : game.pieces[0]) {
                if (game.board[loc.row][loc.col] == 4) {
                    if (r1Loc == null) {
                        r1Loc = loc;
                    } else {
                        r2Loc = loc;
                        break;
                    }
                }
            }

            return estimateDistance(b1Loc, r1Loc) + estimateDistance(b1Loc, r2Loc);
        }


        if (blackKings == 2 && redKings == 1) {
            Location b1Loc = null, b2Loc = null;
            Location r1Loc = null;
            for (Location loc : game.pieces[1]) {
                if (game.board[loc.row][loc.col] == 3) {
                    if (b1Loc == null) {
                        b1Loc = loc;
                    } else {
                        b2Loc = loc;
                        break;
                    }
                }
            }

            for (Location loc : game.pieces[0]) {
                if (game.board[loc.row][loc.col] == 4) {
                    r1Loc = loc;
                    break;
                }
            }

            return estimateDistance(b2Loc, r1Loc) + estimateDistance(b1Loc, r1Loc);
        }

        return 0;
    }


    private long evaluateA(Game game) {

        int kings = 0, pawns = 0, center = 0, sides = 0, trapped = 0;
        for (Location loc : game.pieces[1]) {
            if (game.board[loc.row][loc.col] > 2) {
                kings++;

                if (isCenterPiece(loc))
                    center++;

                if (isTrapped(loc))
                    trapped--;

            } else {
                pawns++;
            }

            if (isTopRow(loc) || isBottomRow(loc) || isLeftColumn(loc) || isRightColumn(loc))
                sides++;

            if (isTrapped(loc))
                trapped--;
        }

        for (Location loc : game.pieces[0]) {
            if (game.board[loc.row][loc.col] > 2) {
                kings--;

                if (isCenterPiece(loc))
                    center--;

                if (isTrapped(loc))
                    trapped++;

            } else {
                pawns--;
            }
            if (isTopRow(loc) || isBottomRow(loc) || isLeftColumn(loc) || isRightColumn(loc))
                sides--;

            if (isTrapped(loc))
                trapped++;
        }

        return (game.pieces[1].size() - game.pieces[0].size()) + 71 * pawns + 100 * kings + 65 * center + 50 * sides + 30 * trapped;

    }

    private long evaluateB(Game game) {

        int kings = 0, pawns = 0, center = 0, sides = 0;
        for (Location loc : game.pieces[1]) {
            if (game.board[loc.row][loc.col] > 2) {
                kings++;

                if (isCenterPiece(loc))
                    center++;

            } else {
                pawns++;
            }

            if (isTopRow(loc) || isBottomRow(loc) || isLeftColumn(loc) || isRightColumn(loc))
                sides++;
        }

        for (Location loc : game.pieces[0]) {
            if (game.board[loc.row][loc.col] > 2) {
                kings--;

                if (isCenterPiece(loc))
                    center--;

            } else {
                pawns--;
            }
            if (isTopRow(loc) || isBottomRow(loc) || isLeftColumn(loc) || isRightColumn(loc))
                sides--;
        }

        return (game.pieces[1].size() - game.pieces[0].size()) + 71 * pawns + 100 * kings + center * 65 + sides * 50;
    }

    private long evaluateState(Game game) {
//        if(playerNumber != 1)
//            return evaluateA(game);
//        else return evaluateB(game);
        return evaluateB(game);
    }


//    private long evaluateState(Game game) {
//
//        long score = 0;
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
//        long distance = getDistancePenalty(game, blackKings, redKings);
//
//
//        score = blackPawns * 100 + blackKings * 140 - redPawns * 100 - redKings * 140;
//        if ((game.currTurn == 1 && (blackPawns + blackKings) > 2) || (game.currTurn == 0 && (redPawns + redKings) > 2)) {
//            score += (blackCenter * 10 - redCenter * 10);
//            score += (-blackLineRow * 5 - blackLineCol * 3 + redLineRow * 5 + redLineCol * 3);
//        }
//        if ((game.currTurn == 1 && (blackPawns + blackKings) <= 3) || (game.currTurn == 0 && (redPawns + redKings) <= 3))
//            score += (-blackTrapped * 10 + redTrapped * 10);
//        if ((game.currTurn == 1 && blackKings <= 2) || (game.currTurn == 0 && redKings <= 2))
//            score += blackKings * 30 - redKings * 30;
//        score += distance * (redKings - blackKings) * 10;
//        score += mobility;
//        return score;
//    }
//
//
//
//
//
//
//
////        int score;
////        int piecesValue = 0, positionValue = 0, differenceValue, advantageValue = 0;
////        int playerAdvantage = -1;
////
////        if (game.pieces[1].size() > game.pieces[0].size())
////            playerAdvantage = 1;
////        else if (game.pieces[1].size() < game.pieces[0].size())
////            playerAdvantage = 0;
////
////        for (Location loc : game.pieces[1]) {
////            if (game.board[loc.row][loc.col] > 2) {
////                piecesValue += 5; //Kings
////
////                if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) { //About to loose
////                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0)) {
////                        advantageValue += 9;
////                    }
////                } else if ((playerAdvantage == 1) && (game.pieces[0].size() < 3)) { // If Player 2 occupying top left corner
////                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 0)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 0))) {
////                        if ((loc.row < 2) && (loc.col == 0)) {
////                            advantageValue += 7;
////                        }
////                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
////                            advantageValue += 5;
////                        } else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
////                            advantageValue += 3;
////                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
////                            advantageValue += 3;
////                        }
////
////                    } else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 0)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 0))) { //Player two occupies bottom corner
////                        if ((loc.row > 5) && (loc.col == 3)) {
////                            advantageValue += 7;
////                        }
////                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
////                            advantageValue += 5;
////                        } else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
////                            advantageValue += 3;
////                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
////                            advantageValue += 3;
////                        }
////                    }
////
////
////                }
////            } else { //Pawns
////                piecesValue += 3;
////                if (loc.row == 7) {
////                    positionValue += 9; //Stay in back row to defend
////                } else {
////                    positionValue += (7 - loc.row); //Closer to king means better
////                }
////            }
////        }
////
////        for (Location loc : game.pieces[0]) {
////            if (game.board[loc.row][loc.col] > 2) {
////                // Kings
////                piecesValue -= 5;
////
////            } else {
////                piecesValue -= 3;
////
////                if (loc.row == 0) {
////                    positionValue -= 9;
////                } else {
////                    // Closer to being king, row = 7, the better
////                    positionValue -= loc.row;
////                }
////            }
////        }
////
////        piecesValue = piecesValue * 1000000;
////        positionValue = positionValue * 100000;
////        // Difference in number of pieces
////        differenceValue = (game.pieces[1].size() - game.pieces[0].size()) * 1000;
////        advantageValue = advantageValue * 10;
////        score = piecesValue + positionValue + differenceValue + advantageValue;
////        return score;
//    }

    private long alphaBetaPrune(Game gameNode, int depth, int second, long alpha, long beta) {

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
                    long ret = alphaBetaPrune(childGame, (depth - 1), second, bestValue, beta);
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

                    long ret = alphaBetaPrune(childGame, (depth - 1), second, alpha, bestValue);
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
