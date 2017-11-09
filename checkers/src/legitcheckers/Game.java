/*
 * Krishna Thiyagarajan
 * ECE-469: Artificial Intelligence
 * Professor Sable
 * November 6, 2017
 */

package legitcheckers;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

class Game {

    final int[][] board = new int[8][4];
    final ArrayList<Location>[] pieces = new ArrayList[2];
    final ArrayList<Move> availableMoves = new ArrayList<>(Constants.maxMoves);
    int currTurn, numWalkMoves;
    private boolean onlyShowJumps;
    private int moveCount = 0;

    Game(int startTurn) {
        currTurn = startTurn;
        numWalkMoves = 0;
        pieces[0] = new ArrayList<>();
        pieces[1] = new ArrayList<>();
    }

    private String colorPieces(int piece, int ii, int jj) {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder, Locale.US);
        String squareColor;

        if (ii % 2 == jj % 2)
            squareColor = Constants.invalidColor;
        else squareColor = Constants.validColor;

        if (piece == Constants.regularPlayerOne)
            formatter.format("\033[%s;5;%sm %s \033[0m", squareColor, Constants.playerOneColor, Constants.regularPlayerOnePiece);
        else if (piece == Constants.kingPlayerOne)
            formatter.format("\033[%s;5;%sm %s \033[0m", squareColor, Constants.playerOneColor, Constants.kingPlayerOnePiece);
        else if (piece == Constants.regularPlayerTwo)
            formatter.format("\033[%s;5;%sm %s \033[0m", squareColor, Constants.playerTwoColor, Constants.regularPlayerTwoPiece);
        else if (piece == Constants.kingPlayerTwo)
            formatter.format("\033[%s;5;%sm %s \033[0m", squareColor, Constants.playerTwoColor, Constants.kingPlayerTwoPiece);
        else formatter.format("\033[%s;5;92m %s \033[0m", squareColor, Constants.emptyPlayerPiece);

        return builder.toString();
    }

    void printBoard() {
        System.out.println(this);
    }


    private boolean takePiece(Location captured) {
        return pieces[1 - currTurn].remove(captured);
    }

    private static Location setEndLocation(Location start, int dir, int dist) {
        if (dist > 2 || dist < 1)
            return null;

        Location end = new Location();

        switch (dir) {
            case Constants.NE:
                if (start.row > dist - 1) {
                    end.row = start.row - dist;
                    if ((start.row % 2 == 1) && (dist == 1)) {
                        end.col = start.col;
                    } else if (start.col < 3) {
                        end.col = start.col + 1;
                    }
                }
                break;
            case Constants.NW:
                if (start.row > dist - 1) {
                    end.row = start.row - dist;
                    if ((start.row % 2 == 0) && (dist == 1)) {
                        end.col = start.col;
                    } else if (start.col > 0) {
                        end.col = start.col - 1;
                    }
                }
                break;
            case Constants.SE:
                if (start.row + dist < 8) {
                    end.row = start.row + dist;
                    if ((start.row % 2 == 1) && (dist == 1)) {
                        end.col = start.col;
                    } else if (start.col < 3) {
                        end.col = start.col + 1;
                    }
                }
                break;
            case Constants.SW:
                if (start.row + dist < 8) {
                    end.row = start.row + dist;
                    if ((start.row % 2 == 0) && (dist == 1)) {
                        end.col = start.col;
                    } else if (start.col > 0) {
                        end.col = start.col - 1;
                    }
                }
                break;
        }
        return end;
    }

    private String nameSquare(int row, int col) {
        return row % 2 == 0 ? Character.toString((char) ('A' + (2 * col + 1))) + Integer.toString(8 - row) : Character.toString((char) ('A' + (2 * col))) + Integer.toString(8 - row);
    }

    void getNextMoves() {
        availableMoves.clear();
        onlyShowJumps = false;
        ArrayList<Integer> dirs = new ArrayList<>();
        Location startPos;

        for (int ii = 0; ii < pieces[currTurn].size(); ii++) {

            startPos = pieces[currTurn].get(ii);

            if (board[startPos.row][startPos.col] > 2) {
                dirs.add(Constants.NE);
                dirs.add(Constants.NW);
                dirs.add(Constants.SE);
                dirs.add(Constants.SW);
            } else if (currTurn == 0) {
                dirs.add(Constants.SE);
                dirs.add(Constants.SW);
            } else {
                dirs.add(Constants.NE);
                dirs.add(Constants.NW);
            }

            Move firstJump = new Move(ii, startPos);

            if (getJumps(firstJump, dirs) && !onlyShowJumps)
                getWalks(firstJump, dirs);

            dirs.clear();
        }
    }

    private void getWalks(Move move, ArrayList<Integer> dirs) {
        for (int dir : dirs) {
            Move newMove = new Move(move);
            Location startPosition = pieces[currTurn].get(newMove.startPiece);
            Location endPos = setEndLocation(startPosition, dir, 1);

            if (endPos != null && endPos.col >= 0 && endPos.col <= 3)
                if (board[endPos.row][endPos.col] == Constants.emptyPlayer && !onlyShowJumps) {
                    newMove.end = new Location(endPos);
                    addMove(newMove, Constants.walkMove);
                }
        }
    }

    private boolean getJumps(Move move, ArrayList<Integer> dirs) {
        boolean extraJump = false;
        for (int ii = 0; ii < dirs.size(); ii++) {
            Move newMove = new Move(move);
            Location startPos = new Location(newMove.end);

            Location captPos, endPos;
            captPos = setEndLocation(startPos, dirs.get(ii), 1);

            if (captPos != null && captPos.col >= 0)
                if (board[captPos.row][captPos.col] % 2 != currTurn && board[captPos.row][captPos.col] > Constants.emptyPlayer) {
                    boolean chainOfPieces = false;

                    for (int jj = 0; jj < newMove.jumpedLocations.size(); jj++)
                        if (newMove.jumpedLocations.get(jj).row == captPos.row && newMove.jumpedLocations.get(jj).col == captPos.col) {
                            chainOfPieces = true;
                            break;
                        }

                    if (!chainOfPieces) {
                        endPos = setEndLocation(startPos, dirs.get(ii), 2);

                        if (endPos != null)
                            if ((endPos.col != -1 && board[endPos.row][endPos.col] == Constants.emptyPlayer) ||
                                    (pieces[currTurn].get(newMove.startPiece).row == endPos.row && pieces[currTurn].get(newMove.startPiece).col == endPos.col)) {
                                extraJump = true;
                                newMove.end = endPos;
                                newMove.jumpedLocations.add(captPos);

                                if (getJumps(newMove, dirs))
                                    addMove(newMove, Constants.jumpMove);

                            }
                    }
                }

        }
        return !extraJump;
    }

    private void addMove(Move newMove, int type) {
        if (type == Constants.jumpMove && !onlyShowJumps) {
            onlyShowJumps = true;
            availableMoves.clear();
        }
        availableMoves.add(newMove);
    }

    void chooseMove(int choice, int blah) {
        if (choice > availableMoves.size() && blah == 1) {
            System.out.println("Choice: " + choice + ", Moves: " + availableMoves);
        }
        Move move = availableMoves.get(choice - 1);
        Location pieceNum = pieces[currTurn].get(move.startPiece);
        int origPiece = board[pieceNum.row][pieceNum.col];

        for (int ii = 0; ii < move.jumpedLocations.size(); ii++) {
            Location currPos = move.jumpedLocations.get(ii);
            if (!takePiece(currPos))
                System.out.println("Could not find opponent's captured piece at " + move.jumpedLocations.get(ii));
            else board[currPos.row][currPos.col] = Constants.emptyPlayer;
        }

        if (move.jumpedLocations.size() == 0 && origPiece > 2)
            numWalkMoves++;
        else numWalkMoves = 0;

        board[pieceNum.row][pieceNum.col] = Constants.emptyPlayer;
        if (move.end.row == 0 && origPiece == Constants.regularPlayerOne)
            board[move.end.row][move.end.col] = Constants.kingPlayerOne;
        else if (move.end.row == 7 && origPiece == Constants.regularPlayerTwo)
            board[move.end.row][move.end.col] = Constants.kingPlayerTwo;
        else board[move.end.row][move.end.col] = origPiece;

        pieces[currTurn].get(move.startPiece).row = move.end.row;
        pieces[currTurn].get(move.startPiece).col = move.end.col;

        currTurn = 1 - currTurn;
    }


    void printNextMoves() {
        getNextMoves();
        System.out.println(String.format("There have been %3d moves so far. Scroll up to see previous moves.", moveCount++));
        System.out.println((currTurn == 0 ? "Red   Player" : "Black Player") + " has a total of " + availableMoves.size() + " moves available. " + (Constants.movesToDraw - numWalkMoves) + " moves until draw.");

        for (int ii = 0; ii < availableMoves.size(); ii++) {
            int start = availableMoves.get(ii).startPiece;
            Location end = availableMoves.get(ii).end;

            System.out.print(String.format("%5d", (ii + 1)) + ". " + nameSquare(pieces[currTurn].get(start).row, pieces[currTurn].get(start).col) + " \u27F9  ");

            if (availableMoves.get(ii).jumpedLocations.size() > 0)
                for (int jj = 0; jj < availableMoves.get(ii).jumpedLocations.size(); jj++)
                    System.out.print(nameSquare(availableMoves.get(ii).jumpedLocations.get(jj).row, availableMoves.get(ii).jumpedLocations.get(jj).col) + " \u27F9  ");

            System.out.print(nameSquare(end.row, end.col));
            System.out.println();
        }
        System.out.println();
        System.out.println("Black King: " + colorPieces(Constants.kingPlayerOne, 0, 1) + "\033[1;97m\tRed King: " + colorPieces(Constants.kingPlayerTwo, 0, 1));
        System.out.println("\033[1;97mBlack Pawn: " + colorPieces(Constants.regularPlayerOne, 0, 1) + "\033[1;97m\tRed Pawn: " + colorPieces(Constants.regularPlayerTwo, 0, 1) + "\n\n\033[1;97m");
    }

    Game cloneGame(int moveNum) {
        Game destGame = new Game(-1);
        destGame.currTurn = this.currTurn;

        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 4; jj++) {
                destGame.board[ii][jj] = this.board[ii][jj];
            }
        }

        for (int ii = 0; ii < this.pieces[0].size(); ii++) {
            Location loc = new Location();
            loc.row = this.pieces[0].get(ii).row;
            loc.col = this.pieces[0].get(ii).col;
            destGame.pieces[0].add(loc);
        }

        for (int ii = 0; ii < this.pieces[1].size(); ii++) {
            Location loc = new Location();
            loc.row = this.pieces[1].get(ii).row;
            loc.col = this.pieces[1].get(ii).col;
            destGame.pieces[1].add(loc);
        }

        Move temp = new Move();

        temp.startPiece = this.availableMoves.get(moveNum - 1).startPiece;
        temp.end.row = this.availableMoves.get(moveNum - 1).end.row;
        temp.end.col = this.availableMoves.get(moveNum - 1).end.col;
        temp.jumpedLocations.clear();

        for (int ii = 0; ii < this.availableMoves.get(moveNum - 1).jumpedLocations.size(); ii++) {
            temp.jumpedLocations.add(new Location(this.availableMoves.get(moveNum - 1).jumpedLocations.get(ii)));
        }

        destGame.availableMoves.add(temp);
        return destGame;
    }


    //    public String toString() {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("\n\033[1;97m    A  B  C  D  E  F  G  H  \n");
//
//        for (int ii = 0; ii < 8; ii++) {
//            builder.append(" \033[1;97m");
//            builder.append(8 - ii);
//            builder.append("\033[1;97m ");
//            builder.append("");
//
//            int count = 0;
//            for (int jj = 0; jj < 8; ) {
//                if (ii % 2 == 0) {
//                    builder.append(colorPieces(0, ii, jj++));
//                    builder.append("\033[1;97m");
//                }
//                builder.append(colorPieces(board[ii][count++], ii, jj++));
//                builder.append("\033[1;97m");
//                if (ii % 2 == 1) {
//                    builder.append(colorPieces(0, ii, jj++));
//                    builder.append("\033[1;97m");
//                }
//            }
//            builder.append("\n");
//
//        }
//        return builder.toString();
//    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("\n\033[1;97m     A   B   C   D   E   F   G   H  \n");
        builder.append("   \033[1;97m\u250F\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2533\u2501\u2501\u2501\u2513\n");

        for (int ii = 0; ii < 8; ii++) {
            builder.append(" \033[1;97m");
            builder.append(8 - ii);
            builder.append("\033[1;97m ");
            builder.append("\u2503");

            int count = 0;
            for (int jj = 0; jj < 8; ) {
                if (ii % 2 == 0) {
                    builder.append(colorPieces(0, ii, jj++));
                    builder.append("\033[1;97m\u2503");
                }
                builder.append(colorPieces(board[ii][count++], ii, jj++));
                builder.append("\033[1;97m\u2503");
                if (ii % 2 == 1) {
                    builder.append(colorPieces(0, ii, jj++));
                    builder.append("\033[1;97m\u2503");
                }
            }


            builder.append("\n");

            if (ii == 7)
                builder.append("   \u2517");
            else builder.append("   \u2523");

            if (ii < 7)
                builder.append("\u2501\u2501\u2501\u254B\u2501\u2501\u2501\u254B\u2501\u2501\u2501\u254B\u2501\u2501\u2501\u254B\u2501\u2501\u2501\u254B\u2501\u2501\u2501\u254B\u2501\u2501\u2501\u254B\u2501\u2501\u2501");
            else
                builder.append("\u2501\u2501\u2501\u253B\u2501\u2501\u2501\u253B\u2501\u2501\u2501\u253B\u2501\u2501\u2501\u253B\u2501\u2501\u2501\u253B\u2501\u2501\u2501\u253B\u2501\u2501\u2501\u253B\u2501\u2501\u2501");

            if (ii == 7)
                builder.append("\u251B");
            else builder.append("\u252B");
            builder.append("\n");

        }
        return builder.toString();
    }

}


class Location {
    int row, col;

    Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    Location() {
        this.row = -1;
        this.col = -1;
    }

    Location(Location another) {
        this.row = another.row;
        this.col = another.col;
    }

    public String toString() {
        return "(Row: " + row + ", Column: " + col + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Location && ((Location) other).row == this.row && ((Location) other).col == this.col;
    }
}

class Move {
    final ArrayList<Location> jumpedLocations = new ArrayList<>(12);
    int startPiece;
    Location end = new Location();

    Move() {
    }

    Move(int startPiece, Location end) {
        this.startPiece = startPiece;
        this.end = end;
    }

    Move(Move another) {
        this.startPiece = another.startPiece;
        this.end = new Location(another.end.row, another.end.col);
        for (int ii = 0; ii < another.jumpedLocations.size(); ii++)
            this.jumpedLocations.add(new Location(another.jumpedLocations.get(ii).row, another.jumpedLocations.get(ii).col));
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Move) {
            Move temp = (Move) o;
            return temp.jumpedLocations.equals(this.jumpedLocations) && this.startPiece == temp.startPiece && this.end.equals(temp.end);
        }

        return super.equals(o);
    }

    public String toString() {
        return startPiece + " " + end + " " + jumpedLocations;
    }

}