package byow.Core.Structure;

public class RectBox {
    /// RectBox 采用左闭右开 [x, x+w)、[y, y+h)
    private final int x;
    private final int y;
    private final int w;
    private final int h;

    RectBox(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean intersects(RectBox other) {
        return x < other.x + other.w &&
                x + w > other.x &&
                y < other.y + other.h &&
                y + h > other.y;
    }

    public boolean intersects(RectBox other, int margin) {
        return x - margin < other.x + other.w &&
                x + w + margin > other.x &&
                y - margin < other.y + other.h &&
                y + h + margin > other.y;
    }

    public static boolean isIntersect(RectBox one, RectBox[] many) {
        if (many == null) {
            return true;
        }
        for (RectBox box : many) {
            if (box.intersects(one)) {
                return false;
            }
        }
        return true;
    }
}
