package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private int width;
    /**
     * The height of the window of this game.
     */
    private int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private Random rand;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        args = new String[]{"123"};
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        round = 1;
        rand = new Random(seed);
        gameOver = false;
    }

    public String generateRandomString(int n) {
        StringBuilder result = new StringBuilder();
        if (n <= 0) {
            throw new RuntimeException();
        }
        while (n > 0) {
            result.append(CHARACTERS[RandomUtils.uniform(rand, 26)]);
            n--;
        }
        return result.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear();
        StdDraw.text((double) width / 2, (double) height / 2, s);

        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.text(width - 10, height - 1, ENCOURAGEMENT[RandomUtils.uniform(rand, ENCOURAGEMENT.length)]);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); ++i) {
            StdDraw.clear();
            StdDraw.text(width / 2.0, height / 2.0, "" + letters.charAt(i));
            StdDraw.show();
            StdDraw.pause(1000);

            StdDraw.clear();
            StdDraw.show();
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        String s = "";
        while (n > 0) {
            while (!StdDraw.hasNextKeyTyped()) {
                StdDraw.pause(5);
            }
            s += StdDraw.nextKeyTyped();
            drawFrame(s);
            n--;
        }
        return s;
    }
    private void clearInputs(){
        while (StdDraw.hasNextKeyTyped()){
            StdDraw.nextKeyTyped();
        }
    }
    public void startGame() {
        while (true) {
            String ans=generateRandomString(round);
            flashSequence(ans);
            clearInputs();
            String input=solicitNCharsInput(ans.length());
            if(!ans.equals(input)){
                gameOver=true;
                drawFrame("Game Over! You made it to round: " + round);
                return;
            }

            round += 1;
        }
    }

}
