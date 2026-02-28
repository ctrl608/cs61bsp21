package byow.Core.Structure;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

abstract public class Structure {
    /**
     * 抽象类,各种结构的模版
     * 左下角为起点
     */

    protected final int x;
    protected final int y;

    protected Structure(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public abstract void draw(TETile[][] world);

    /** 统一用“包围盒”来做碰撞/越界等通用逻辑 */
    public abstract RectBox bound();
    public abstract boolean contains(int px, int py);

    public static Structure randomStructure(Random random ,int WIDTH,int HEIGHT) {
        return null;
    }
    public boolean intersects(Structure s,int margain){
        return this.bound().intersects(s.bound(),margain);
    }
    public boolean intersects(Structure s){
        return  this.bound().intersects(s.bound());
    }
    private static Structure[] randomStructures(int num) {
        return  null;
    }
//    public static RectBox[] bounds(Structure[] structures){
//        if(structures==null)return null;
//        ArrayList<RectBox> bounds=new ArrayList<>();
//        for(int i=0;i<structures.length;++i){
//            bounds.add(structures[i].bound());
//        }
//        return (RectBox[]) bounds.toArray();
//    }
}
