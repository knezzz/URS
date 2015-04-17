package knezzz.hr.ultrarapidspectate.map;

/**
 * Created by knezzz on 14/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class Tower {
    private int towerPositionx;
    private int towerPositiony;
    private int teamSide;
    private boolean dead;

    public Tower(int x, int y, int teamSide){
        this.towerPositionx = x;
        this.towerPositiony = y;
        this.teamSide = teamSide;
        dead = false;
    }

    public boolean isDead(){
        return dead;
    }

    public int getX(){
        return towerPositionx;
    }

    public int getY(){
        return towerPositiony;
    }

    public int getSide(){
        return teamSide;
    }

    public void kill(){
        dead = true;
    }
}
