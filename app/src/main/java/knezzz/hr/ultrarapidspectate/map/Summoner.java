package knezzz.hr.ultrarapidspectate.map;

import android.graphics.Bitmap;

import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.staticdata.Champion;

/**
 * Created by knezzz on 05/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class Summoner {
    private boolean dead;
    private Participant participant;
    private long teamId;
    private Champion champion;
    private int lastx, lasty;
    private int nextx = -1, nexty = -1;
    long nextTimestamp = -1, lastTimestamp, deathTimer, lastKillTimeStamp;
    int basex, basey;
    int multiKill;
    private Bitmap icon;

    public Summoner(Participant participant, int x, int y, Bitmap icon){
        this.participant = participant;
        this.icon = icon;
        dead = false;
        multiKill = 1;
        champion = participant.getChampion();
        teamId = participant.getTeam().getID();

        lastTimestamp = 0;
        basex = x;
        basey = y;

        lastx = x;
        lasty = y;
    }

    public void setNextPosition(long timeStamp, int x, int y){
        if(nextTimestamp != -1 && nextx != -1 && nexty != -1) {
            lastTimestamp = nextTimestamp;
            lastx = nextx;
            lasty = nexty;
        }

        nextTimestamp = timeStamp/10;
        nextx = x;
        nexty = y;
    }

    public int getPlayerX(long time){
        long timeDifference = nextTimestamp-lastTimestamp;
        time -= lastTimestamp;

        int xDifference;
        if(lastx > nextx){
            xDifference = lastx-nextx;
        }else{
            xDifference = nextx-lastx;
        }

        double x = ((double)time/(double)timeDifference)*(double)xDifference;

        if(lastx > nextx) {
            return (int) (lastx-x);
        }else{
            return (int) (lastx+x);
        }
    }

    public int getPlayerY(long time){
        long timeDifference = nextTimestamp-lastTimestamp;
        time -= lastTimestamp;

        int yDifference;
        if(lasty > nexty){
            yDifference = lasty-nexty;
        }else{
            yDifference = nexty-lasty;
        }

        double y = ((double)time/(double)timeDifference)*(double)yDifference;

        if(lasty > nexty) {
            return (int) (lasty-y);
        }else{
            return (int) (lasty+y);
        }
    }

    public void setDeathTimer(long level){
        dead = true;

        double baseWaitRespawn = level * 2.5 + 5;

        if(lastTimestamp > (25*60*1000)/10){
            deathTimer = (long)(baseWaitRespawn + (baseWaitRespawn/50) * (lastTimestamp - (25*60*1000)/10));
        }else {
            deathTimer = (long) baseWaitRespawn;
        }
    }

    public long getNextTimestamp(){
        return nextTimestamp;
    }

    public long getTeamId(){
        return teamId;
    }

    public Participant getParticipant(){
        return participant;
    }

    public boolean isDead(){
        return dead;
    }

    public void respawn(){
        dead = false;
    }

    public String getChampionName(){
        return champion.getName();
    }

    public Bitmap getIcon(){
        return icon;
    }
}
