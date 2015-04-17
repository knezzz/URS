package knezzz.hr.ultrarapidspectate.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import knezzz.hr.ultrarapidspectate.R;

/**
 * Created by knezzz on 15/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class Baron {
    private final static int x = 5007;
    private final static int y = 10471;
    private final static int spawnTime = 1200000; // 20:00
    private final static int respawnTime = 420000; // 7:00
    private long killTimestamp;
    private Bitmap baronBitmap;
    private final static int BARON_SIZE = 120;
    private boolean dead;

    public Baron(MapView c){
        dead = false;
        baronBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.baron_nashor);
        baronBitmap = Bitmap.createScaledBitmap(baronBitmap, BARON_SIZE, BARON_SIZE, false);
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
        return baronBitmap;
    }

    public int getBaronSize(){
        return BARON_SIZE;
    }
}
