package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class HexWorld {
    public static class Hex {
        //左上顶点
        private final int x;
        private final int y;
        private final int size;
        private final TETile teTile;

        public Hex(int x, int y, int size, TETile teTile) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.teTile = teTile;
        }
        public void draw(TETile[][] board) {
            for (int dy = 0; dy < size; ++dy) {
                int dx = dy;
                int len = size + 2 * dx;
                drawHorLine(board, x - dx, y - dy, len, teTile);
            }
            for (int dy = size; dy < size * 2; ++dy) {
                int dx = 2 * size - dy - 1;
                int len = size + 2 * dx;
                drawHorLine(board, x - dx, y - dy, len, teTile);
            }
        }
        private Hex shift(int dx, int dy) {
            return shift(dx, dy, this.teTile);
        }

        private Hex shift(int dx, int dy, TETile tile) {
            return new Hex(x + dx, y + dy, size, tile);
        }

        // down
        public Hex down() { return shift(0, -2 * size); }
        public Hex down(TETile tile) { return shift(0, -2 * size, tile); }

        // up
        public Hex up() { return shift(0,  2 * size); }
        public Hex up(TETile tile) { return shift(0,  2 * size, tile); }

        // leftDown
        public Hex leftDown() { return shift(1 - 2 * size, -size); }
        public Hex leftDown(TETile tile) { return shift(1 - 2 * size, -size, tile); }

        // leftUp
        public Hex leftUp() { return shift(1 - 2 * size,  size); }
        public Hex leftUp(TETile tile) { return shift(1 - 2 * size,  size, tile); }

        // rightDown
        public Hex rightDown() { return shift(2 * size - 1, -size); }
        public Hex rightDown(TETile tile) { return shift(2 * size - 1, -size, tile); }

        // rightUp
        public Hex rightUp() { return shift(2 * size - 1,  size); }
        public Hex rightUp(TETile tile) { return shift(2 * size - 1,  size, tile); }
    }

    private static void drawHorLine(TETile[][] board, int x, int y, int len, TETile teTile) {
        // y 边界用 board[0].length（height）
        if (y < 0 || y >= board[0].length) {
            return;
        }
        for (int i = 0; i < len; ++i) {
            safeReplace(board, x + i, y, teTile);
        }
    }
    private static boolean isValidPoint(TETile[][] board, int x, int y) {
        return x >= 0 && y >= 0 && x < board.length && y < board[0].length;
    }

    private static void safeReplace(TETile[][] board, int nx, int ny, TETile teTile) {
        if (isValidPoint(board, nx, ny)) {
            board[nx][ny] = teTile;
        }
    }
    public static void drawHexColumn(TETile[][] world, Hex top, int n) {
        Hex cur = top;
        for (int i = 0; i < n; i++) {
            cur.draw(world);
            cur = cur.down(); // 同列往下
        }
    }
    public static void draw19Hex(TETile[][] world, Hex topLeftOfCol0) {
        int[] cnt = {3,4,5,4,3};
        Hex colTop = topLeftOfCol0;

        for (int i = 0; i < cnt.length; i++) {
            drawHexColumn(world, colTop, cnt[i]);
            colTop = i<2? colTop.rightUp():colTop.rightDown(); // 下一列的顶端（按你的坐标系）
        }
    }

    public static void main(String[] args) {
        final int HEIGHT = 58;
        final int WIDTH = 80;

        TERenderer world = new TERenderer();
        world.initialize(WIDTH, HEIGHT);

        // 关键：必须是 [WIDTH][HEIGHT]
        TETile[][] board = new TETile[WIDTH][HEIGHT];

        // 初始化也要按 x,y 来
        for (int x = 0; x < WIDTH; ++x) {
            for (int y = 0; y < HEIGHT; ++y) {
                board[x][y] = Tileset.NOTHING;
            }
        }
//
////        addHexagon(board, 0, 0, 1, Tileset.FLOWER);
//        drawHexCol(board, 55, 55, 4, Tileset.FLOWER, 5);
        Hex h=new Hex(33,33,4,Tileset.WALL);
        draw19Hex(board,h);



        world.renderFrame(board);
    }
}