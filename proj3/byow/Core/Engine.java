package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public WorldState worldState;
    public WorldGenerator generator;
    private boolean waitingForSeed, inGame = false, quitting, writing = true;
    private char lastInput;
    private long seedCache;
    private long keysInput;
    private TERenderer renderer;
    private StringBuilder commandLog = new StringBuilder();
    private static final Path SAVE_PATH = Path.of("byow_save.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public Engine() {
        worldState = null;
        generator = null;
        renderer = new TERenderer();
        renderer.initialize(WIDTH, HEIGHT);

        keysInput = 0;

        seedCache = 0;

    }

    public void interactWithKeyboard() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                keysInput += 1;

                System.out.println(ch);

                parser(ch);

                if (inGame) {
                    TETile[][] snapshot = worldState == null ? null : worldState.snapshot();
                    renderer.renderFrame(snapshot);
                }
                if (quitting) {
                    break;
                }

            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                ;
            }

        }
        System.exit(0);   // 直接关闭窗口


    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
//        try {
//            parser(input);
//        } catch (RuntimeException e) {
//            System.out.println(e);
//        }
        reset();
        for (int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            parser(ch);
            if (quitting) {
                break;
            }
        }
        return worldState == null ? null : worldState.snapshot();
    }

    public void parser(char ch) {
        switch (ch) {
            case 'N':
            case 'n':
                if (!inGame) {
                    waitingForSeed = true;
                    if (writing) {
                        commandLog.append(Character.toLowerCase(ch));
                    }
                }
                //在游戏内绝对无效
                /// 暂时认为N后面无S仍生成世界
                break;
            case 'W':
            case 'w':
                if (writing) {
                    commandLog.append(Character.toLowerCase(ch));
                }
                if (inGame) {
                    moveUp();
                }
                break;
            case 'A':
            case 'a':
                if (writing) {
                    commandLog.append(Character.toLowerCase(ch));
                }
                if (inGame) {
                    moveLeft();
                }
                break;
            case 'S':
            case 's':
                if (writing) {
                    commandLog.append(Character.toLowerCase(ch));
                }
                if (waitingForSeed) {
                    waitingForSeed = false;
                    worldState = WorldState.generate(seedCache, WIDTH, HEIGHT);
                    inGame = true;
                    seedCache = 0;
                    break;
                }
                if (inGame) {
                    moveDown();
                }
                break;
            case 'D':
            case 'd':
                if (writing) {
                    commandLog.append(Character.toLowerCase(ch));
                }
                if (inGame) {
                    moveRight();
                }
                break;


            case 'Q':
            case 'q':
                if (lastInput == ':') {
                    saveLog();
                    quitting = true;
                    inGame = false;
                    return;
                }
                break;
            case 'l':
            case 'L':
                String log = loadLog();
                // 用 log 重建世界：相当于用户当初输入了这些
                replayFromLog(log);
                break;
            default:
                if (Character.isDigit(ch) && waitingForSeed) {
                    if (writing) {
                        commandLog.append(Character.toLowerCase(ch));
                    }
                    seedCache = seedCache * 10 + ch - '0';
                }
        }
        lastInput = ch;

    }

    @Override
    public String toString() {
        if (worldState == null) {
            return "null world";
        }
        TETile[][] view = worldState.snapshot();

        StringBuilder sb = new StringBuilder();
        for (int y = view[0].length - 1; y >= 0; y--) {          // 从上到下打印
            for (int x = 0; x < view.length; x++) {           // 从左到右打印
                sb.append(view[x][y].character());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private void moveUp() {
        worldState.tryMoveAvatar(0, 1);
    }

    private void moveDown() {
        worldState.tryMoveAvatar(0, -1);
    }

    private void moveLeft() {
        worldState.tryMoveAvatar(-1, 0);
    }

    private void moveRight() {
        worldState.tryMoveAvatar(1, 0);
    }

    private void saveLog() {
        try {
            Files.writeString(SAVE_PATH, commandLog.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Save failed", e);
        }
    }

    private String loadLog() {
        try {
            if (!Files.exists(SAVE_PATH)) {
                return "";
            }
            return Files.readString(SAVE_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Load failed", e);
        }
    }

    private void replayFromLog(String old) {
        reset();
        writing = false;
        for (int i = 0; i < old.length(); ++i) {
            char ch = old.charAt(i);
            if (ch == 'L' || ch == 'l') {
                continue;
            }
            parser(ch);
        }
        writing = true;
        commandLog = new StringBuilder(old);
    }

    private void reset() {
        quitting = false;
        lastInput = 0;
        waitingForSeed = false;
        inGame = false;
        seedCache = 0;
        worldState = null;
        commandLog.setLength(0);
    }

}
