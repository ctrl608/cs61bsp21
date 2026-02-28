package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorldState implements Serializable {
    private final int WIDTH, HEIGHT;
    private final long worldSeed;
    public static final String SAVES = System.getProperty("user.dir");
    private TETile[][] tiles;
    Avatar avatar;


    private WorldState(int WIDTH, int HEIGHT, long seed) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.tiles = new TETile[WIDTH][HEIGHT];
        worldSeed = seed;
        avatar = new Avatar(0, 0);
        //TODO: 可能被卡
    }

    public int width() {
        return WIDTH;
    }

    public int height() {
        return HEIGHT;
    }

    public TETile get(int x, int y) {
        return tiles[x][y];
    }

    public void set(int x, int y, TETile t) {
        tiles[x][y] = t;
    }


    public TETile[][] snapshot() {
        if (tiles == null) {
            return null;
        }
        TETile[][] copy = new TETile[tiles.length][];
        for (int x = 0; x < tiles.length; x++) {
            if (tiles[x] != null) {
                copy[x] = tiles[x].clone();   // 关键：复制每一行
            }
        }
        copy[avatar.x()][avatar.y()] = Tileset.AVATAR;
        return copy;
    }

    public boolean tryMoveAvatar(int dx, int dy) {
        int nx = avatar.x() + dx;
        int ny = avatar.y() + dy;
        if (isWalkable(nx, ny)) {
            avatar.tp(nx, ny);
            return true;
        }
        return false;
    }

    private boolean isWalkable(int x, int y) {
        return 0 <= x && x < WIDTH && 0 <= y && y < HEIGHT && isStandable(tiles[x][y]);
    }

    private boolean isStandable(TETile tile) {
        return tile == Tileset.FLOWER || tile == Tileset.GRASS || tile == Tileset.FLOOR;
    }

    private void setWorld(TETile[][] tiles) {
        this.tiles = tiles;
    }

    public static WorldState generate(long seed, int WIDTH, int HEIGHT) {

        WorldState ws = new WorldState(WIDTH, HEIGHT, seed);
        WorldGenerator gen = new WorldGenerator(seed, WIDTH, HEIGHT);
        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        gen.generate(tiles);
        ws.setWorld(tiles);
        return ws;
    }
}


