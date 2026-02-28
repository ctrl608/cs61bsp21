package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import org.junit.Test;

import javax.swing.*;

public class TestMain {
    public static void main(String[] args) {
        str();
//        render();
//
//        keyboard();
    }

    public static void render() {
        Engine engine = new Engine();
        TERenderer teRenderer = new TERenderer();
        TETile[][] snapshot = engine.interactWithInputString("N14S");
        teRenderer.initialize(snapshot.length, snapshot[0].length);
        teRenderer.renderFrame(snapshot);
    }

    public static void keyboard() {
        Main.main(new String[]{});
    }

    public static void str() {

        String[] command = {"-s", "n7095889102109223638s"};
        Main.main(command);
    }
}
