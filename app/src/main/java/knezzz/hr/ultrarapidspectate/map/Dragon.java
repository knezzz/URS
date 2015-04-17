package knezzz.hr.ultrarapidspectate.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import knezzz.hr.ultrarapidspectate.R;

/**
 * Created by knezzz on 15/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class Dragon {
    private final static int DRAGON_SIZE = 80;
    private final static int x = 9866;
    private final static int y = 4414;
    private final static int spawnTime = 150000; // 2:30
    private final static int respawnTime = 360000; // 6:00
    private long killTimestamp;
    private Bitmap dragonBitmap;
    private boolean dead;

    public Dragon(MapView c){
        dead = false;
        dragonBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.dragon);
        dragonBitmap = Bitmap.createScaledBitmap(dragonBitmap, DRAGON_SIZE, DRAGON_SIZE, false);
    }

    public void kill(long timeStamp){
        dead = true;
        killTimestamp = timeStamp;
    }

    public boolean isDead(){
        return dead;
    }

    public void respawn(){
        dead = false;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public long getRespawnTime(){
        return (killTimestamp+respawnTime)/10;
    }

    public long getSpawnTime(){
        return spawnTime/10;
    }

    public Bitmap getIcon(){
        return dragonBitmap;
    }

    public int getDragonSize(){
        return DRAGON_SIZE;
    }
}
