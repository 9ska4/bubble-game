package bubble;

public class Game {
    private final int size;
    private final int colorPalette;
    private final int groupSize;


    public Game(int size, int colorPalette, int groupSize) {
        if (size < 3 || colorPalette < 2 || groupSize < 2) {
            throw new IllegalArgumentException();
        }
        this.size = size;
        this.colorPalette = colorPalette + 1; // because we color=0 is no color
        this.groupSize = groupSize;
    }

    public void play() {
        int[][] gameField = new int[size][size];

        initColorsToZeros(gameField);

        System.out.print("Initial game:");
        Printer.printField(gameField);

        boolean ifContinue;
        int iter = 0;
        System.out.print("\n\nIterations:");
        do {
            iter++;
            var reduced = checkAndReduce(gameField);
            int numberOfRotated = initColorsToZeros(reduced);
            gameField = reduced;
            System.out.print("\nno. " + iter + " - rotated colors: " + numberOfRotated);
            ifContinue = numberOfRotated != 0;
        } while (ifContinue);

        System.out.print("\n\nEnded game:");
        Printer.printField(gameField);
    }

    private int[][] checkAndReduce(int[][] field) {
        var checkHorizontally = checkHorizontally(field);
        //printField(checkHorizontally);

        var checkVertically = checkVertically(field);
        //printField(checkVertically);

        var checkSum = collect(checkHorizontally, checkVertically);
        //printField(checkSum);

        field = validate(field, checkSum);
        //printField(field);

        field = gravitization(field);
        //printField(field);

        return field;
    }

    private int[][] gravitization(int[][] field) {
        int[][] transposed = transpose(field);
        for (int i = 0; i < size; i++) {
            int[] ints = transposed[i];
            int[] gravitized = moveColors(ints);
            transposed[i] = gravitized;
        }
        int[][] result = transpose(transposed);
        return result;
    }

    public int[] moveColors(int[] input) {
        for (int i = (size - 1); i > 0; i--) {
            int current = input[i];
            if (current == 0) {
                int candidateIndex = i - 1;

                boolean candidateFits;
                do {
                    int candidateValue = input[candidateIndex];
                    if (candidateValue == 0) {
                        candidateFits = false;
                        candidateIndex--;
                    } else {
                        candidateFits = true;
                        input[candidateIndex] = 0;
                        input[i] = candidateValue;
                    }
                } while (candidateFits == false && candidateIndex >= 0);
            }
        }
        return input;
    }

    private int[][] validate(int[][] field, boolean[][] checkSum) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (checkSum[i][j]) {
                    field[i][j] = 0;
                }
            }
        }
        return field;
    }

    private boolean[][] checkHorizontally(int[][] field) {
        boolean[][] checkHorizontally = new boolean[size][size];
        for (int i = 0; i < field.length; i++) {
            checkHorizontally[i] = findGroups(field[i]);
        }
        return checkHorizontally;
    }

    private boolean[][] checkVertically(int[][] field) {
        int[][] fieldTransposed = transpose(field);
        boolean[][] checkVerticallyTransposed = new boolean[size][size];
        for (int i = 0; i < field.length; i++) {
            checkVerticallyTransposed[i] = findGroups(fieldTransposed[i]);
        }
        return transpose(checkVerticallyTransposed);
    }

    private boolean[][] collect(boolean[][] checkH, boolean[][] checkV) {
        boolean[][] result = new boolean[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (checkH[i][j] || checkV[i][j]) {
                    result[i][j] = true;
                }
            }
        }
        return result;
    }

    private int[][] transpose(int[][] original) {
        int[][] transpose = new int[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                transpose[i][j] = original[j][i];
            }
        }
        return transpose;
    }

    private boolean[][] transpose(boolean[][] original) {
        boolean[][] transpose = new boolean[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                transpose[i][j] = original[j][i];
            }
        }
        return transpose;
    }

    private boolean[] findGroups(int[] array) {
        boolean[] result = new boolean[size];

        int lastColor = -1;
        int colorCounter = 0;
        for (int i = 0; i < size; i++) {
            if (lastColor != array[i]) {
                colorCounter = 0;
            }
            lastColor = array[i];
            colorCounter += 1;

            if (colorCounter >= groupSize) {
                for (int j = 0; j < groupSize; j++) {
                    result[i - j] = true;
                }
            }
        }
        return result;
    }

    private int initColorsToZeros(int[][] field) {
        int initializedBlocks = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] == 0) {
                    field[i][j] = randomColor();
                    initializedBlocks++;
                }
            }
        }
        return initializedBlocks;
    }

    // random color from 1 to {colorPalette}
    private int randomColor() {
        return (int) (Math.random()*(colorPalette - 1) + 1);
    }

    private static class Printer {

        private static void printField(int[][] field) {
            printHeader(field[0].length);
            printLine(field[0].length);

            // i-th row
            for (int i = 0; i < field.length; i++) {

                // row number
                System.out.print("\n" + i + " | ");

                for (int j = 0; j < field[i].length; j++) {
                    // j-th element
                    System.out.print(field[i][j] + " ") ;
                }
            }
            printLine(field[0].length);
        }

        private static void printHeader(int size) {
            System.out.print("\nx | ");
            for (int i = 0; i < size; i++) {
                System.out.print(i + " ");
            }
        }

        private static void printLine(int size) {
            System.out.print("\n- | ");
            for (int i = 0; i < size; i++) {
                System.out.print("- ");
            }
        }

        private static void printField(boolean[][] field) {
            printHeader(field[0].length);

            // i-th row
            for (int i = 0; i < field.length; i++) {

                // row number
                System.out.print(i + " | ");

                for (int j = 0; j < field[i].length; j++) {
                    // j-th element
                    System.out.print(replaceBoolean(field[i][j]) + " ") ;
                }
                System.out.print("\n");
            }
        }

        private static String replaceBoolean(boolean b) {
            return b ? "x" : "o";
        }

    }
}

