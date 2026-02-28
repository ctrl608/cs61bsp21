package byow.Core.Structure;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class RectangleRoom extends Structure {
    private final int WIDTH;
    private final int HEIGHT;
    private final boolean isRectangle = true;

    RectangleRoom(int x, int y, int width, int height) {
        super(x, y);
        WIDTH = width;
        HEIGHT = height;
    }

    RectangleRoom() {
        this(10, 10, 5, 5);
    }


    public int width() {
        return WIDTH;
    }

    public int height() {
        return HEIGHT;
    }

    @Override
    public void draw(TETile[][] world) {
        for (int dx = 0; dx < WIDTH; ++dx) {
            for (int dy = 0; dy < HEIGHT; ++dy) {
                int nx = x + dx;
                int ny = y + dy;
                world[nx][ny] = (dx == 0 || dy == 0 || dx == WIDTH - 1 || dy == HEIGHT - 1) ?
                        Tileset.WALL : Tileset.NOTHING;
            }
        }
    }

    @Override
    public RectBox bound() {
        return new RectBox(x, y, WIDTH, HEIGHT);
    }

    @Override
    public boolean contains(int px, int py) {
        return px >= x && px < x + WIDTH &&
                py >= y && py < y + HEIGHT;
    }

    public static Structure randomStructure(Random random, int worldW, int worldH) {
        int x, y, width, height;
        width = RandomUtils.uniform(random, 3, 10);
        height = RandomUtils.uniform(random, 5, 15);
        x = RandomUtils.uniform(random, 1, worldW - width - 1);
        y = RandomUtils.uniform(random, 1, worldH - height - 1);
        return new RectangleRoom(x, y, width, height);
    }

    public static Structure[] randomStructures(int num, Random random, int worldW, int worldH) {
        ArrayList<Structure> list = new ArrayList<>();

        int attempts = 0;
        int maxAttempts = num * 20;   // 防止后期卡死

        while (list.size() < num && attempts < maxAttempts) {
            attempts++;

            Structure temp = randomStructure(random, worldW, worldH);

            boolean collide = false;
            for (Structure s : list) {
                if (temp.intersects(s, 1)) {   // 1 = 留一格空隙
                    collide = true;
                    break;
                }
            }

            if (!collide) {
                list.add(temp);
            }
        }

        return list.toArray(new Structure[0]);
    }

}
