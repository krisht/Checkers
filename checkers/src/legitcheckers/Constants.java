/*
 * Krishna Thiyagarajan
 * ECE-469: Artificial Intelligence
 * Professor Sable
 * November 6, 2017
 */

package legitcheckers;

class Constants {
    final static int maxMoves = 32;
    final static int maxDepth = 50;
    final static int movesToDraw = 100;
    final static int startPlayer = 1;

    final static int NE = 1;
    final static int NW = 2;
    final static int SE = 3;
    final static int SW = 4;

    final static int jumpMove = 1;
    final static int walkMove = 2;

    final static int regularPlayerOne = 1;
    final static int regularPlayerTwo = 2;
    final static int kingPlayerOne = 3;
    final static int kingPlayerTwo = 4;
    final static int emptyPlayer = 0;

    final static String regularPlayerOnePiece = "\u25B2";
    final static String regularPlayerTwoPiece = "\u25BC";
    final static String kingPlayerOnePiece = "\u2617";
    final static String kingPlayerTwoPiece = "\u26CA";
    final static String emptyPlayerPiece = " ";

    final static String invalidColor = "46";
    final static String validColor = "47";
    final static String playerOneColor = "30";
    final static String playerTwoColor = "31";

    final static int playerMode = 1;
    final static int computerMode = 0;

    final static long minVal = Long.MIN_VALUE;
    final static long maxVal = Long.MAX_VALUE;

    //Ascii art thanks to http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20
    static final String winPlayerTwo = "   _____                         ____                 _ \n" +
            "  / ____|                       / __ \\               | |\n" +
            " | |  __  __ _ _ __ ___   ___  | |  | |_   _____ _ __| |\n" +
            " | | |_ |/ _` | '_ ` _ \\ / _ \\ | |  | \\ \\ / / _ \\ '__| |\n" +
            " | |__| | (_| | | | | | |  __/ | |__| |\\ V /  __/ |  |_|\n" +
            "  \\_____|\\__,_|_| |_| |_|\\___|  \\____/_ \\_/ \\___|_|  (_)\n" +
            "      |  __ \\        | | \\ \\        / /        | |      \n" +
            "      | |__) |___  __| |  \\ \\  /\\  / /__  _ __ | |      \n" +
            "      |  _  // _ \\/ _` |   \\ \\/  \\/ / _ \\| '_ \\| |      \n" +
            "      | | \\ \\  __/ (_| |    \\  /\\  / (_) | | | |_|      \n" +
            "      |_|  \\_\\___|\\__,_|     \\/  \\/ \\___/|_| |_(_)      \n" +
            "                                                        \n" +
            "                                                        ";

    static final String winPlayerOne = "   _____                         ____                 _ \n" +
            "  / ____|                       / __ \\               | |\n" +
            " | |  __  __ _ _ __ ___   ___  | |  | |_   _____ _ __| |\n" +
            " | | |_ |/ _` | '_ ` _ \\ / _ \\ | |  | \\ \\ / / _ \\ '__| |\n" +
            " | |__| | (_| | | | | | |  __/ | |__| |\\ V /  __/ |  |_|\n" +
            "  \\_____|\\__,_|_| |_| |_|\\___|_ \\____/  \\_/ \\___|_| _(_)\n" +
            "   |  _ \\| |          | |    \\ \\        / /        | |  \n" +
            "   | |_) | | __ _  ___| | __  \\ \\  /\\  / /__  _ __ | |  \n" +
            "   |  _ <| |/ _` |/ __| |/ /   \\ \\/  \\/ / _ \\| '_ \\| |  \n" +
            "   | |_) | | (_| | (__|   <     \\  /\\  / (_) | | | |_|  \n" +
            "   |____/|_|\\__,_|\\___|_|\\_\\     \\/  \\/ \\___/|_| |_(_)  \n" +
            "                                                        \n" +
            "                                                        ";

    static final String gameDraw = "  _____ _   _                  _____                     _ \n" +
            " |_   _| | ( )         /\\     |  __ \\                   | |\n" +
            "   | | | |_|/ ___     /  \\    | |  | |_ __ __ ___      _| |\n" +
            "   | | | __| / __|   / /\\ \\   | |  | | '__/ _` \\ \\ /\\ / / |\n" +
            "  _| |_| |_  \\__ \\  / ____ \\  | |__| | | | (_| |\\ V  V /|_|\n" +
            " |_____|\\__| |___/ /_/    \\_\\ |_____/|_|  \\__,_| \\_/\\_/ (_)\n" +
            "                                                           \n" +
            "                                                           ";
    private static final String ANSI_CLS = "\u001b[2J";
    private static final String ANSI_HOME = "\u001b[H";
    static int timeLimit = 10000; //10 seconds

    static void clearScreen() {
        System.out.print(ANSI_CLS + ANSI_HOME);
    }

    static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }
}
