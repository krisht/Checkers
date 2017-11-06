package legitcheckers;

import java.util.Date;
import java.util.Random;

@SuppressWarnings("unused")
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


    private Location getNorthEastLocation(int row, int col) {
        Location end = null;
        if (row > 0) {
            if (row % 2 == 1)
                end = new Location(row - 1, col);
            else if (col < 3)
                end = new Location(row - 1, col + 1);
        }
        return end;
    }

    private Location getSouthEastLocation(int row, int col) {
        Location end = null;
        if (row < 7) {
            if (row % 2 == 1)
                end = new Location(row + 1, col);
            else if (col < 3)
                end = new Location(row + 1, col + 1);
        }
        return end;
    }

    private Location getNorthWestLocation(int row, int col) {
        Location end = null;
        if (row > 0) {
            if (row % 2 == 0)
                end = new Location(row - 1, col);
            else if (col > 0)
                end = new Location(row - 1, col - 1);
        }
        return end;
    }

    private Location getSouthWestLocation(int row, int col) {
        Location end = null;
        if (row < 7) {
            if (row % 2 == 0)
                end = new Location(row + 1, col);
            else if (col > 0)
                end = new Location(row + 1, col + 1);
        }
        return end;
    }

    private boolean isSafe(Location loc) {
        if (loc.row % 2 == 0)
            return loc.row == 0 || loc.row == 7 || 2 * loc.col + 1 == 0 || 2 * loc.col + 1 == 7;
        return loc.row == 0 || loc.row == 7 || 2 * loc.col == 0 || 2 * loc.col == 7;
    }

// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private boolean isMovable(Location loc) {
//        return getSouthWestLocation(loc.row, loc.col) != null
//                || getSouthEastLocation(loc.row, loc.col) != null
//                || getNorthWestLocation(loc.row, loc.col) != null
//                || getNorthEastLocation(loc.row, loc.col) != null;
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)

    private boolean onMainDiagonal(Location loc) {
        return (loc.col == 0 && (loc.row == 7 || loc.row == 6))
                || (loc.col == 1 && (loc.row == 5 || loc.row == 4))
                || (loc.col == 2 && (loc.row == 3 || loc.row == 2))
                || (loc.col == 3 && (loc.row == 1 || loc.row == 0));
    }

    private boolean onDoubleDiagonal(Location loc) {
        return (loc.row == 1 && loc.col == 0)
                || (loc.row == 2 && loc.col == 0)
                || (loc.row == 3 && loc.col == 1)
                || (loc.row == 4 && loc.col == 1)
                || (loc.row == 5 && loc.col == 2)
                || (loc.row == 6 && loc.col == 2)
                || (loc.row == 7 && loc.col == 3)
                || (loc.row == 0 && loc.col == 0)
                || (loc.row == 1 && loc.col == 1)
                || (loc.row == 2 && loc.col == 1)
                || (loc.row == 3 && loc.col == 2)
                || (loc.row == 4 && loc.col == 2)
                || (loc.row == 5 && loc.col == 3)
                || (loc.row == 6 && loc.col == 3);
    }

// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private boolean isLoner(Game game, Location loc) {
//        return (getNorthWestLocation(loc.row, loc.col) == null || game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) == 0)
//                && (getSouthEastLocation(loc.row, loc.col) == null || game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) == 0)
//                && (getSouthWestLocation(loc.row, loc.col) == null || game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) == 0)
//                && (getNorthEastLocation(loc.row, loc.col) == null || game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) == 0);
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)

// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private int isHole(Game game, int row, int col) {
//
//        int ne = game.getPieceAtLocation(getNorthEastLocation(row, col)) % 2;
//        int se = game.getPieceAtLocation(getSouthEastLocation(row, col)) % 2;
//        int nw = game.getPieceAtLocation(getNorthWestLocation(row, col)) % 2;
//        int sw = game.getPieceAtLocation(getSouthWestLocation(row, col)) % 2;
//
//        if (ne == se && se == nw && nw == sw)
//            return 4;
//        else if ((nw == ne && ne == se) || (ne == se && se == sw) || (se == sw && sw == nw) || (sw == nw && nw == ne))
//            return 3;
//        else return 0;
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)


// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private boolean isTriangle(Game game, int player) {
//        return (player == 1 && (game.board[7][3] == 1 && game.board[7][2] == 1 && game.board[6][2] == 1))
//                || (player == 0 && (game.board[0][0] == 2 && game.board[1][1] == 2 && game.board[0][1] == 2));
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)

// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private boolean isOreo(Game game, int player) {
//        return (player == 1 && (game.board[7][1] == 1 && game.board[7][2] == 1 && game.board[6][1] == 1))
//                || (playerNumber == 0 && (game.board[0][2] == 2 && game.board[0][1] == 2 && game.board[1][2] == 2));
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)

    private int isDefenderPieces(Game game, Location loc) {
        int count = 0;

        if (loc.row < 3 || loc.row > 5) {

            Location ne = getNorthEastLocation(loc.row, loc.col);
            if (ne != null && game.getPieceAtLocation(loc) != 0 && game.getPieceAtLocation(ne) != 0 && Math.abs(game.getPieceAtLocation(ne) - game.getPieceAtLocation(loc)) == 2)
                count++;
            Location nw = getNorthWestLocation(loc.row, loc.col);
            if (nw != null && game.getPieceAtLocation(loc) != 0 && game.getPieceAtLocation(nw) != 0 && Math.abs(game.getPieceAtLocation(nw) - game.getPieceAtLocation(loc)) == 2)
                count++;

            Location se = getSouthEastLocation(loc.row, loc.col);
            if (se != null && game.getPieceAtLocation(loc) != 0 && game.getPieceAtLocation(se) != 0 && Math.abs(game.getPieceAtLocation(se) - game.getPieceAtLocation(loc)) == 2)
                count++;

            Location sw = getSouthWestLocation(loc.row, loc.col);
            if (sw != null && game.getPieceAtLocation(loc) != 0 && game.getPieceAtLocation(sw) != 0 && Math.abs(game.getPieceAtLocation(sw) - game.getPieceAtLocation(loc)) == 2)
                count++;

        }
        return count;

    }

    private boolean isCentralPieces(Location loc) {
        return (loc.row == 4 || loc.row == 3) && (loc.col == 1 || loc.col == 2);
    }

// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private boolean hasDog(Game game, int player) {
//        return (game.board[7][3] == 1 && game.board[6][3] == 2 && player == 1)
//                || (game.board[0][0] == 2 && game.board[1][0] == 1 && player == 0);
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)

    int chooseAIMove(Game game) throws InterruptedException {
        bestMove = -1;

        int completedBestMove = 0;
        int startingDepth = 5;
        long bestValue = 0;
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
            //System.out.println("maxDepth: " + maxDepth + ", d: " + d);
            bestValue = alphaBetaPrune(game, d, Constants.minVal, Constants.maxVal);
            //System.out.println("Best move: " + bestMove);

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
            System.out.println(String.format("Reached the maximum allowable depth, %d,  in %d milliseconds.", Constants.maxDepth, (currTimeMS - startTimeMS)));
        else
            System.out.println(String.format("Reached %s depth in %d milliseconds.", Constants.ordinal(maxDepth), (currTimeMS - startTimeMS)));

        if (outOfTime)
            System.out.println(String.format("Time ran out while searching %s depth.", Constants.ordinal(maxDepth + 1)));
        System.out.println(String.format("The AI for Player %d chooses the %s move.", 2 - game.currTurn, Constants.ordinal(completedBestMove)));

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

// --Commented out by Inspection START (11/5/17 9:54 PM):
//    private long evaluateState2(Game game) {
//        int piecesDiff = game.pieces[1].size() - game.pieces[0].size(); //Done
//        int kingsDiff = 0; //Done
//        int pawnDiff = 0;
//        int posVal = 0; //Done
//        int regDef = 0;
//        int kingDef = 0;
//        int sidesDef = 0;
//        int cornDef = 0;
//        int posMoves = game.availableMoves.size();
//        int kingsGame = 0;
//        int regsGame = 0;
//        int advantageValue = 0;
//        int playerAdvantage = -1;
//
//        if (piecesDiff > 0)
//            playerAdvantage = 1;
//        else if (piecesDiff < 0)
//            playerAdvantage = 0;
//
//
//        posMoves = game.currTurn == 1 ? posMoves : -posMoves;
//
//        for (Location loc : game.pieces[1]) {
//            if (game.board[loc.row][loc.col] > 2) {
//                kingsDiff++;
//                if ((loc.row < 2 && loc.col == 1) || (loc.row > 5 && loc.col == 3) || (loc.row == 0 && loc.col == 3) || (loc.row == 7 && loc.row == 0))
//                    cornDef++;
//
//                if (game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) % 2 == 1)
//                    kingDef++;
//                if (game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) % 2 == 1)
//                    kingDef++;
//                if (game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) % 2 == 1)
//                    kingDef++;
//                if (game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) % 2 == 1)
//                    kingDef++;
//
//
//                // If behind in pieces and about to lose, retreat to double corners
//                if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) {
//                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0 || isCentralPieces(loc) || onMainDiagonal(loc) || onDoubleDiagonal(loc))) {
//                        advantageValue += 9;
//                    }
//                }
//                // Else: If player 1 has advantage, smaller bonus for being near the double corner (closer the better)
//                else if ((playerAdvantage == 1) && (game.pieces[0].size() < 3)) {
//
//                    // If Player 2 occupying top left corner
//                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 0)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 0))) {
//
//                        // Right on the corner
//                        if ((loc.row < 2) && (loc.col == 0)) {
//                            advantageValue += 7;
//                        }
//                        // One move away from the corner
//                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
//                            advantageValue += 5;
//                        }
//                        // Two moves away from the corner
//                        else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
//                            advantageValue += 3;
//                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
//                            advantageValue += 3;
//                        }
//
//                    }
//                    // If Player 2 occupying bottom right corner
//                    else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 0)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 0))) {
//
//                        // Right on the corner
//                        if ((loc.row > 5) && (loc.col == 3)) {
//                            advantageValue += 7;
//                        }
//                        // One move away from the corner
//                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
//                            advantageValue += 5;
//                        }
//                        // Two moves away from the corner
//                        else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
//                            advantageValue += 3;
//                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
//                            advantageValue += 3;
//                        }
//                    }
//                }
//
//            } else {
//                if (loc.row == 7) // Staying in back row to prevent opponent getting promoted
//                    posVal += 9;
//                else posVal += (7 - loc.row); // Closer to being king, row = 0, the better
//
//                if (game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) % 2 == 1)
//                    regDef++;
//                if (game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) % 2 == 1)
//                    regDef++;
//
//                if (loc.row == 0 || loc.row == 7 || loc.col == 0 || loc.col == 7)
//                    sidesDef++;
//
//                pawnDiff++;
//            }
//        }
//
//        for (Location loc : game.pieces[0]) {
//            if (game.board[loc.row][loc.col] > 2) {
//                kingsDiff--;
//                if ((loc.row < 2 && loc.col == 1) || (loc.row > 5 && loc.col == 3) || (loc.row == 0 && loc.col == 3) || (loc.row == 7 && loc.row == 0))
//                    cornDef--;
//
//                if (game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getSouthWestLocation(loc.row, loc.col)) % 2 == 0)
//                    kingDef--;
//                if (game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getSouthEastLocation(loc.row, loc.col)) % 2 == 0)
//                    kingDef--;
//                if (game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) % 2 == 0)
//                    kingDef--;
//                if (game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) % 2 == 0)
//                    kingDef--;
//
//
//                // If behind in pieces and about to lose, retreat to double corners
//                if ((playerAdvantage == 1) && (game.pieces[0].size() < 3 || isCentralPieces(loc) || onMainDiagonal(loc) || onDoubleDiagonal(loc))) {
//                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0)) {
//                        advantageValue -= 9;
//                    }
//                }
//                // Else: If player 2 has advantage, smaller bonus for being near the double corner (closer the better)
//                else if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) {
//                    // If Player 1 occupying top left corner
//                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 1)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 1))) {
//
//                        // Right on the corner
//                        if ((loc.row < 2) && (loc.col == 0)) {
//                            advantageValue -= 7;
//                        }
//                        // One move away from the corner
//                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
//                            advantageValue -= 5;
//                        }
//                        // Two moves away from the corner
//                        else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
//                            advantageValue -= 3;
//                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
//                            advantageValue -= 3;
//                        }
//
//                    }
//                    // If Player 1 occupying bottom right corner
//                    else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 1)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 1))) {
//
//                        // Right on the corner
//                        if ((loc.row > 5) && (loc.col == 3)) {
//                            advantageValue -= 7;
//                        }
//                        // One move away from the corner
//                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
//                            advantageValue -= 5;
//                        }
//                        // Two moves away from the corner
//                        else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
//                            advantageValue -= 3;
//                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
//                            advantageValue -= 3;
//                        }
//                    }
//                }
//
//            } else {
//                if (loc.row == 0) // Staying in back row to prevent opponent getting promoted
//                    posVal -= 9;
//                else posVal -= loc.row; // Closer to being king, row = 7, the better
//
//                if (game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getNorthEastLocation(loc.row, loc.col)) % 2 == 0)
//                    kingDef--;
//                if (game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) > 0 && game.getPieceAtLocation(getNorthWestLocation(loc.row, loc.col)) % 2 == 0)
//                    kingDef--;
//
//                if (loc.row == 0 || loc.row == 7 || loc.col == 0 || loc.col == 7)
//                    sidesDef--;
//
//                pawnDiff--;
//            }
//        }
//
//
//        for (Location a : game.pieces[0]) {
//            for (Location b : game.pieces[1]) {
//                if (game.board[a.row][a.col] > 2 || game.board[b.row][b.col] > 2)
//                    kingsGame += estimateDistance(a, b);
//                else regsGame += estimateDistance(a, b);
//            }
//        }
//
//        if (piecesDiff > 0)
//            kingsGame = -kingsGame;
//        else if (piecesDiff == 0)
//            kingsGame = 0;
//
//
//        int kingDiffWeight = 5000000;
//        int pawnDiffWeight = 3000000;
//        int posValWeight = 10000;
//        int pieceDiffWeight = 1000;
//        int regDefWeight = 10000;
//        int kingDefWeight = 50000;
//        int sidesDefWeight = 30000;
//        int cornDefWeight = 20000;
//        int posMovesWeight = 1000;
//        int kingsGameWeight = 10000;
//        int regsGameWeight = 10000;
//        int advantageValueWeight = 100;
//        if (game.pieces[1].size() < 4 || game.pieces[0].size() < 4)
//            advantageValueWeight = 10000;
//
//        kingsDiff *= kingDiffWeight;
//        piecesDiff *= pieceDiffWeight;
//        pawnDiff *= pawnDiffWeight;
//        advantageValue *= advantageValueWeight;
//        posVal *= posValWeight;
//        regDef *= regDefWeight;
//        kingDef *= kingDefWeight;
//        sidesDef *= sidesDefWeight;
//        cornDef *= cornDefWeight;
//        posMoves *= posMovesWeight;
//        kingsGame *= kingsGameWeight;
//        regsGame *= regsGameWeight;
//
//        int score = kingsDiff + pawnDiff + piecesDiff + posVal + regDef + kingDef + sidesDef + cornDef + posMoves + kingsGame + regsGame + advantageValue;
//
//        return playerNumber == 0 ? -score : score;
//    }
// --Commented out by Inspection STOP (11/5/17 9:54 PM)


    private long evaluateState(Game game) {
        int piecesValue = 0, positionValue = 0, differenceValue, advantageValue = 0, defenseValue = 0;
        int playerAdvantage = -1;

        if (game.pieces[1].size() > game.pieces[0].size())
            playerAdvantage = 1;
        else if (game.pieces[1].size() < game.pieces[0].size())
            playerAdvantage = 0;

        for (Location loc : game.pieces[1]) {
            if (game.board[loc.row][loc.col] > 2) {
                // Kings
                piecesValue += 5;

                if (isSafe(loc)) {
                    defenseValue += 5;
                }

                // If behind in pieces and about to lose, retreat to double corners
                if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) {
                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0 || isCentralPieces(loc) || onMainDiagonal(loc) || onDoubleDiagonal(loc))) {
                        advantageValue += 9;
                    }
                }
                // Else: If player 1 has advantage, smaller bonus for being near the double corner (closer the better)
                else if ((playerAdvantage == 1) && (game.pieces[0].size() < 3)) {

                    // If Player 2 occupying top left corner
                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 0)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 0))) {

                        // Right on the corner
                        if ((loc.row < 2) && (loc.col == 0)) {
                            advantageValue += 7;
                        }
                        // One move away from the corner
                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
                            advantageValue += 5;
                        }
                        // Two moves away from the corner
                        else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
                            advantageValue += 3;
                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
                            advantageValue += 3;
                        }

                    }
                    // If Player 2 occupying bottom right corner
                    else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 0)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 0))) {

                        // Right on the corner
                        if ((loc.row > 5) && (loc.col == 3)) {
                            advantageValue += 7;
                        }
                        // One move away from the corner
                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
                            advantageValue += 5;
                        }
                        // Two moves away from the corner
                        else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
                            advantageValue += 3;
                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
                            advantageValue += 3;
                        }
                    }
                }
            } else {
                // Regular pieces
                piecesValue += 3;
                if (isSafe(loc))
                    piecesValue += 3;

                defenseValue += (isDefenderPieces(game, loc) * 3);

                // Staying in back row to prevent opponent kinging
                if (loc.row == 7) {
                    positionValue += 9;
                } else {
                    // Closer to being king, row = 0, the better
                    positionValue += (7 - loc.row);
                }
            }
        }

        for (Location loc : game.pieces[0]) {
            if (game.board[loc.row][loc.col] > 2) {
                // Kings
                piecesValue -= 5;

                if (isSafe(loc))
                    defenseValue -= 5;

                // If behind in pieces and about to lose, retreat to double corners
                if ((playerAdvantage == 1) && (game.pieces[0].size() < 3 || isCentralPieces(loc) || onMainDiagonal(loc) || onDoubleDiagonal(loc))) {
                    if ((loc.row > 5 && loc.col == 3) || (loc.row < 2 && loc.col == 0)) {
                        advantageValue -= 9;
                    }
                }
                // Else: If player 2 has advantage, smaller bonus for being near the double corner (closer the better)
                else if ((playerAdvantage == 0) && (game.pieces[1].size() < 3)) {
                    // If Player 1 occupying top left corner
                    if (((game.board[0][0] > 0) && (game.board[0][0] % 2 == 1)) || ((game.board[1][0] > 0) && (game.board[1][0] % 2 == 1))) {

                        // Right on the corner
                        if ((loc.row < 2) && (loc.col == 0)) {
                            advantageValue -= 7;
                        }
                        // One move away from the corner
                        if ((loc.row == 2 && loc.col == 0) || (loc.row == 1 && loc.col == 1)) {
                            advantageValue -= 5;
                        }
                        // Two moves away from the corner
                        else if ((loc.row == 3 && loc.col == 0) || (loc.row == 3 && loc.col == 1)) {
                            advantageValue -= 3;
                        } else if ((loc.row == 2 && loc.col == 1) || (loc.row == 0 && loc.col == 1)) {
                            advantageValue -= 3;
                        }

                    }
                    // If Player 1 occupying bottom right corner
                    else if (((game.board[6][3] > 0) && (game.board[6][3] % 2 == 1)) || ((game.board[7][3] > 0) && (game.board[7][3] % 2 == 1))) {

                        // Right on the corner
                        if ((loc.row > 5) && (loc.col == 3)) {
                            advantageValue -= 7;
                        }
                        // One move away from the corner
                        if ((loc.row == 6 && loc.col == 2) || (loc.row == 5 && loc.col == 3)) {
                            advantageValue -= 5;
                        }
                        // Two moves away from the corner
                        else if ((loc.row == 4 && loc.col == 3) || (loc.row == 4 && loc.col == 2)) {
                            advantageValue -= 3;
                        } else if ((loc.row == 5 && loc.col == 2) || (loc.row == 7 && loc.col == 2)) {
                            advantageValue -= 3;
                        }
                    }
                }
            } else {
                // Regular pieces
                piecesValue -= 3;

                if (isSafe(loc))
                    defenseValue -= 3;

                defenseValue -= (isDefenderPieces(game, loc) * 3);

                // Staying in back row to prevent opponent kinging
                if (loc.row == 0) {
                    positionValue -= 9;
                } else {
                    // Closer to being king, row = 7, the better
                    positionValue -= loc.row;
                }
            }
        }

        differenceValue = (game.pieces[1].size() - game.pieces[0].size());

        int score = 0;
        score = score * 100 + piecesValue;
        score = score * 100 + positionValue;
        score = score * 100 + differenceValue;
        score = score * 100 + advantageValue;
        score = score * 100 + defenseValue;
        score = score * 100 + ((new Random()).nextInt(50) - 25);

        return (playerNumber == 0 ? -score : score);
    }


    private long alphaBetaPrune(Game gameNode, int depth, long alpha, long beta) {

        if (!outOfTime) {

            if ((new Date()).getTime() - startTimeMS > .998 * Constants.timeLimit) {
                outOfTime = true;
                return 0;
            }

            gameNode.getNextMoves();

            if (isTerminalState(gameNode))
                return utilityFunction(gameNode, depth);

            if (depth == 0)
                return evaluateState(gameNode);

            if (gameNode.currTurn == 1) {
                long bestValue = alpha;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    Game childGame = gameNode.clone(child);
                    childGame.chooseMove(1);
                    long ret = alphaBetaPrune(childGame, depth - 1, bestValue, beta);
                    if (ret > bestValue) {
                        bestValue = ret;
                        if (depth == maxDepth)
                            bestMove = child;
                    } else if ((ret == bestValue) && (depth == maxDepth))
                        if ((new Random()).nextBoolean())
                            bestMove = child;
                    if (beta <= bestValue)//Beta prune
                        break;
                }
                return bestValue;
            } else {
                long bestValue = beta;

                for (int child = 1; child <= gameNode.availableMoves.size(); child++) {
                    Game childGame = gameNode.clone(child);
                    childGame.chooseMove(1);

                    long ret = alphaBetaPrune(childGame, depth - 1, alpha, bestValue);
                    if (ret < bestValue) {
                        bestValue = ret;
                        if (depth == maxDepth)
                            bestMove = child;
                    } else if ((ret == bestValue) && (depth == maxDepth))
                        if ((new Random()).nextBoolean())
                            bestMove = child;
                    if (bestValue <= alpha)// Alpha prune
                        break;

                }
                return bestValue;
            }
        } else return 0;
    }
}
