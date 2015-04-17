package knezzz.hr.ultrarapidspectate.map;

/**
 * Inhibitor class.
 * Has timer (5minutes) when to respawn.
 * Created by knezzz on 16/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class Inhibitor{
    private static final int respawnTime = 300000;
    private long killTimestamp;
    private int x;
    private int y;
    private long teamID;
    private boolean dead;

    public Inhibitor(int x, int y, long teamId) {
        dead = false;
        this.x = x;
        this.y = y;
        this.teamID = teamId;
    }

    public int getX(){
        return x;
    }

    public boolean isDead(){
        return dead;
    }

    public int getY(){
        return y;
    }

    public long getTeamID(){
        return teamID;
    }

    public void respawn(){
        dead = false;
    }

    public void kill(long killTimestamp){
        dead = true;
        this.killTimestamp = killTimestamp;
    }

    public long getRespawnTime(){
        return (killTimestamp+respawnTime)/10;
    }
}
