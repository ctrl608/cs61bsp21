package byow.Core;

public class Avatar {
    private int x;
    private int y;

    Avatar(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public void tp(int x,int y){
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
