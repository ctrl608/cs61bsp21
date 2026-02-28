package byow.Core;

import byow.Core.Structure.RectangleRoom;
import byow.Core.Structure.Structure;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Random;


public class WorldGenerator {
    private Random random;
    private final int WIDTH,HEIGHT;
    WorldGenerator(long seed,int width,int height){
        random=new Random(seed);
        WIDTH=width;
        HEIGHT=height;
    }

    private void generateStructures(TETile[][] canvas){
        int structureNum=RandomUtils.uniform(random,10);
        Structure[] structures= RectangleRoom.randomStructures(structureNum,random,WIDTH,HEIGHT);
        for(Structure s:structures){
            s.draw(canvas);
        }
    }
    private void generatePlot(TETile[][] canvas){
        TETile base= Tileset.GRASS;
        for(int x=0;x<canvas.length;++x){
            for(int y=0; y<canvas[0].length;++y){
                canvas[x][y]=base;
            }
        }
    }
    public void generate(TETile[][] tiles){
        generatePlot(tiles);
        generateStructures(tiles);
    }


}
