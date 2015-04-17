package knezzz.hr.ultrarapidspectate.map;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.robrua.orianna.type.core.common.Side;
import com.robrua.orianna.type.core.match.Event;
import com.robrua.orianna.type.core.match.Frame;
import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.core.match.MatchTeam;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.match.Position;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import knezzz.hr.ultrarapidspectate.R;

/**
 * View for map, extends View and uses canvas for painting the map, and user animation.
 * Created by knezzz on 06/04/15.
 */
public class MapView extends View{
    private final static int FRAME_RATE = 30;
    private final static int bitmapTurretHeight = 80;
    private final static int bitmapTurretWidth = 80;
    private static final int bitmapInhibSize = 100;
    private static final int championIconSize = 50;
    private static final int bitmapNexusSize = 130;
    private static final int moveChamptionIconFor = championIconSize/2;

    private static final int redNexusx = 13200;
    private static final int redNexusy = 13500;
   // 13052, 12612
   // 12611, 13084
    private static final int blueNexusx = 1600;
    private static final int blueNexusy = 1500;
   // 1748, 2270
    // 2177, 1807

    private Dragon dragon;
    private Baron baron;

    private Match currentMatch = null;
    private Bitmap map, turretDestroy, turretBlue, turretRed;
    private Bitmap inhibDestroy, inhibBlue, inhibRed;
    private Bitmap nexusRed, nexusBlue, nexusDestroy;
    private Paint paint;
    private Handler h;
    private TextView matchTimeTextField;
    private MapEventListAdapter timelineTextField;

    private int currentFrame, totalFrames, eventsFrame = -1;
    private ArrayList<Summoner> summoners;
    private ArrayList<Tower> towers;
    private ArrayList<Event> currentEvents;
    private ArrayList<Inhibitor> inhibitors;

    private boolean firstBlood = false;
    private boolean isPlaying = false;
    private long startTime, timePassed;
    //  private ArrayList<Ward> wards;

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Adding players to arrayList for easy accsess;
     */
    private void getPlayers() {
        Log.d("getPlayers()", "getting players icons......");
        for (Participant player : currentMatch.getParticipants()) {
            int color;
            Position pp = currentMatch.getTimeline().getFrames().get(currentFrame).getFrameForParticipant(player).getPosition();

            if (player.getTeam().getID() == Side.BLUE.getID()) {
                color = getResources().getColor(R.color.blueTeam);//R.color.blueTeam;//Color.BLUE);
            } else {
                color = getResources().getColor(R.color.redTeam);//R.color.redTeam;//Color.RED);
            }

            summoners.add(new Summoner(player, pp.getX(), pp.getY(), getIconWithBorder(player.getChampion().getImage().getFull(), color)));
        }

        Log.d("getPlayers()", "got all player icons!");
    }

    /**
     * Getting champion square icon from ddragon.
     * TODO: Add version to load from API
     * @param name - name of the image (champion);
     * @return - Return square Bitmap.
     */
    public Bitmap getChampionImage(final String name){
        final Bitmap[] finalImage = new Bitmap[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlS = "http://ddragon.leagueoflegends.com/cdn/5.7.2/img/champion/" + name;
                    URL url = new URL(urlS);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    finalImage[0] = getCircularBitmap(image);
                }catch(Exception e){
                    Log.e("ERROR: ", e.toString());
                }
            }
        });

        t.start();

        try {
            t.join();
        }catch(Exception e){
            Log.e("ERROR: ", e.toString());
        }

        return finalImage[0];
    }

    /**
     * Takes square bitmap and turns it in circular bitmap image.
     * @param bitmap - Bitmap that you want to change
     * @return - circular version of original bitmap.
     */
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return Bitmap.createScaledBitmap(output, championIconSize, championIconSize, false);
    }

    /**
     * Adding circular border around bitmap
     * @param name - name of champion (image)
     * @param color - color of team
     * @return - return bitmap with border
     */
    public Bitmap getIconWithBorder(String name, int color){
        Bitmap icon = getChampionImage(name);
        int borderSize = 10;
        int size = championIconSize + borderSize;
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);

        float r = size/2;

        Paint p = new Paint();
        p.setColor(color);
        c.drawCircle(r, r, r, p);
        c.drawBitmap(icon, borderSize / 2, borderSize / 2, null);

        return b;
    }

    /**
     * onDraw, draws and handeles all the needed events for game to animate, and show where are players on map.
     * @param canvas - canvas will be map, where map dimensions min{x:-570, y:-420} max{x:15220, y:14980} will be scaled to fit on screen.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        if(currentMatch != null){
            double scaleFor = (double)canvas.getHeight()/(double)map.getHeight();
            canvas.scale((float) scaleFor, (float) scaleFor);
            canvas.drawBitmap(map, 0, 0, null);
            paint.setAntiAlias(true);

            timePassed = System.currentTimeMillis()-startTime;

            matchTimeTextField.setText(getReadableMatchDuration(timePassed / 100) + "");

            checkForEvents(timePassed);

            canvas.drawBitmap(nexusRed, getScaledX(redNexusx) - (bitmapNexusSize / 2), getScaledY(redNexusy) - (bitmapNexusSize / 2), null);
            canvas.drawBitmap(nexusBlue, getScaledX(blueNexusx) - (bitmapNexusSize / 2), getScaledY(blueNexusy) - (bitmapNexusSize / 2), null);

            for(Tower t : towers){
                Bitmap turretBitmap;
                if(t.isDead()){
                    turretBitmap = turretDestroy;
                }else if(t.getSide() == Side.BLUE.getID()) {
                    turretBitmap = turretBlue;
                } else {
                    turretBitmap = turretRed;
                }

                canvas.drawBitmap(turretBitmap, getScaledX(t.getX()) - (bitmapTurretWidth / 2), getScaledY(t.getY()) - (bitmapTurretHeight / 2), null);
            }

            for(Inhibitor i : inhibitors){
                Bitmap inhibBitmap;
                if(i.isDead()){
                    if(i.getRespawnTime()<timePassed){
                        i.respawn();
                    }
                    inhibBitmap = inhibDestroy;
                }else if(i.getTeamID() == Side.BLUE.getID()){
                    inhibBitmap = inhibBlue;
                }else{
                    inhibBitmap = inhibRed;
                }

                int inhibSize = bitmapInhibSize/2;
                canvas.drawBitmap(inhibBitmap, getScaledX(i.getX()) - inhibSize, getScaledY(i.getY()) - inhibSize, null);
            }

            if(dragon.getSpawnTime()<timePassed){
                if(!dragon.isDead()){
                    int dragonSize = dragon.getDragonSize() / 2;
                    canvas.drawBitmap(dragon.getIcon(), getScaledX(dragon.getX()) - dragonSize, getScaledY(dragon.getY()) - dragonSize, null);
                }else if(dragon.getRespawnTime() < timePassed){
                    dragon.respawn();
                }

                if(baron.getSpawnTime()<timePassed){
                    if(!baron.isDead()){
                        int dragonSize = baron.getBaronSize() / 2;
                        canvas.drawBitmap(baron.getIcon(), getScaledX(baron.getX()) - dragonSize, getScaledY(baron.getY()) - dragonSize, null);
                    }else if(baron.getRespawnTime() < timePassed){
                        baron.respawn();
                    }
                }
            }

            for(Summoner player : summoners) {
                List<Frame> frames = currentMatch.getTimeline().getFrames();

                if(player.getNextTimestamp() < timePassed){
                    if(!player.isDead()){
                        eventLoop:for(Event e : currentEvents) {
                            switch (e.getEventType()) {
                                case CHAMPION_KILL:
                                case BUILDING_KILL:
                                case ELITE_MONSTER_KILL:
                                    //Getting level for death timer.
                                    int level = frames.get(currentFrame).getFrameForParticipant(player.getParticipant()).getLevel();

                                    if (setNextFrame(player, e, level)) {
                                        break eventLoop;
                                    }
                                    break;
                                //    case ITEM_DESTROYED:
                                case ITEM_PURCHASED:
                                case ITEM_SOLD:
                                case ITEM_UNDO:
                                    if (setFrameInBase(player, e)) {
                                        break eventLoop;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }

                        //If there was no even - or if player next Timestamp is still less then timePassed
                        //Then assingn new minute timestamp to player.
                        if (player.getNextTimestamp() < timePassed) {
                            if(currentFrame + 1 < totalFrames) {
                                if ((frames.get(currentFrame + 1).getTimestamp() / 10) > timePassed) {
                                    Position next = frames.get(currentFrame + 1).getFrameForParticipant(player.getParticipant()).getPosition();
                                    player.setNextPosition(frames.get(currentFrame + 1).getTimestamp(), next.getX(), next.getY());
                                }
                            }
                        }
                    }else{
                        player.setNextPosition(player.getNextTimestamp()+player.deathTimer, player.basex, player.basey);
                        player.respawn();
                    }
                }

            /*    for(Ward w : wards){
                    paint.setColor(Color.YELLOW);
                    canvas.drawCircle(getScaledX(w.wardx), getScaledY(w.wardy), 10f, paint);
                }*/

                canvas.drawBitmap(player.getIcon(), getScaledX(player.getPlayerX(timePassed))-moveChamptionIconFor, getScaledY(player.getPlayerY(timePassed))-moveChamptionIconFor, null);
                //      canvas.drawLine(getScaledX(player.lastx), getScaledY(player.lasty), getScaledX(player.nextx), getScaledY(player.nexty), paint);
            }

            if(currentFrame+1 < totalFrames){
                if ((currentMatch.getTimeline().getFrames().get(currentFrame + 1).getTimestamp() / 10) < timePassed) {
                    currentFrame++;
                }

                h.postDelayed(r, FRAME_RATE);
            } else {
                getWinner(canvas);
            }

            if(eventsFrame != currentFrame){
                if(currentFrame+1 < totalFrames) {
                    if (currentMatch.getTimeline().getFrames().get(currentFrame+1).getEvents() != null) {
                        setUpNewEvents(currentMatch.getTimeline().getFrames().get(currentFrame + 1).getEvents());
                    }
                }

                eventsFrame = currentFrame;
            }
        }

        canvas.restore();
    }

    /**
     * Getting next frame where player needs to move, returns true/false based on if next movement position is found or not.
     * @param player - Player that triggered event
     * @param e - Event: checking if player really did trigger event, and getting position
     * @param level - Player level this is needed only if player is victim for calculating death timer.
     * @return - returning if next frame was assigned or not.
     */
    private boolean setNextFrame(Summoner player, Event e, int level){
        boolean gotFrame = false;

        if(e.getKiller() != null) {
            if (player.getParticipant().getParticipantID() == e.getKiller().getParticipantID()) {
                player.setNextPosition(e.getTimestamp(), e.getPosition().getX(), e.getPosition().getY());
                gotFrame = true;
            } else if (e.getAssistingParticipants().contains(player.getParticipant())) {
                player.setNextPosition(e.getTimestamp(), e.getPosition().getX(), e.getPosition().getY());
                gotFrame = true;
            }
        }

        if(e.getVictim() != null) {
            if (player.getParticipant().getParticipantID() == e.getVictim().getParticipantID()) {
                player.setNextPosition(e.getTimestamp(), e.getPosition().getX(), e.getPosition().getY());
                player.setDeathTimer(level);
                gotFrame = true;
            }
        }

        if(e.getCreator() != null){
            if (player.getParticipant().getParticipantID() == e.getCreator().getParticipantID()) {
                player.setNextPosition(e.getTimestamp(), e.getPosition().getX(), e.getPosition().getY());
                gotFrame = true;
            }
        }

        return gotFrame;
    }

    /**
     * In case player event takes place in base (buying, selling or undo item) get his/hers next position in base.
     * @param player - Player that triggared event.
     * @param e - Event needed for checking player, timestamp and position.
     * @return - returning if player really bought, sold or undo the item.
     */
    private boolean setFrameInBase(Summoner player, Event e){
        boolean gotFrame = false;

        if(e.getParticipant() != null) {
            if (e.getParticipant().getParticipantID() == player.getParticipant().getParticipantID()) {
                player.setNextPosition(e.getTimestamp(), player.basex, player.basey);
                gotFrame = true;
            }
        }

        return gotFrame;
    }

    /**
     * Getting winner of the game and writing who won.
     * @param canvas - Canvas where winner text will be displayed.
     */
    private void getWinner(Canvas canvas) {
        String winner = null;
        int winnerColor = -1;

        List<MatchTeam> teams = currentMatch.getTeams();

        for(MatchTeam mt : teams) {
            if(mt.getSide().getID() == Side.BLUE.getID()) {
                if (mt.getWinner()) {
                    winner = "Blue team";
                    winnerColor = getResources().getColor(R.color.blueTeam);//Color.BLUE);
                    canvas.drawBitmap(nexusDestroy, getScaledX(redNexusx) - (bitmapNexusSize / 2), getScaledY(redNexusy) - (bitmapNexusSize / 2), null);
                } else {
                    winner = "Red team";
                    winnerColor = getResources().getColor(R.color.redTeam);//Color.RED);
                    canvas.drawBitmap(nexusDestroy, getScaledX(blueNexusx)-(bitmapNexusSize/2), getScaledY(blueNexusy)-(bitmapNexusSize/2), null);
                }
            }
        }

        if(winnerColor != -1 || winner != null) {
            timelineTextField.addItem(winner, winnerColor, " won!");
        }
    }

    /**
     * Checking if there are new events, or old ones that can be removed from array.
     * @param timePassed - game time, to compare old and new events.
     */
    private void checkForEvents(long timePassed){
        Iterator<Event> eventsIterator = currentEvents.iterator();

        while (eventsIterator.hasNext()){
            Event e = eventsIterator.next();

            if(e.getTimestamp()/10 < timePassed) {
                switch(e.getEventType()){
                    case CHAMPION_KILL:
                        handleChampionKill(e);
                        break;
                    case BUILDING_KILL:
                        handleBuildingKill(e);//, canvas);
                        break;
                    case ELITE_MONSTER_KILL:
                        handleEliteMonsterKill(e);
                        break;
                    case WARD_PLACED:
                     //   handleWardPlaced(e);
                        break;
                    case WARD_KILL:
                      //  handleWardDestroy(e);
                        break;
                    default:
                       // Log.e("unhandled event", getReadableMatchDuration(e.getTimestamp() / 1000) + " - " + e.getEventType().name());
                        break;
                }

                eventsIterator.remove();
            }
        }
    }

    /**
     * handle ward placed.. get creator and calculate relative position of ward;
     * @param e - event that was triggered.
     */
    private void handleWardPlaced(Event e){
     /*   for(Summoner s : summoners){
            if(s.getParticipant().getParticipantID() == e.getCreator().getParticipantID()){
                wards.add(new Ward(s.getPlayerX(e.getTimestamp()/10), s.getPlayerY(e.getTimestamp()/10)));
            }
        }*/
    }

    /**
     * find closest ward and destroy it;
     * @param e - event that was triggered.
     */
    private void handleWardDestroy(Event e){
    }

    /**
     * handle monster kill, get killer, assists and monster type;
     * @param e - event that was triggered.
     */
    private void handleEliteMonsterKill(Event e){
        String killer = null;
        String event = null;
        int killerColor;

        if(e.getKiller().getTeam().getID() == Side.BLUE.getID()){
            killer = "Blue team";
            killerColor = getResources().getColor(R.color.blueTeam);//Color.BLUE;
        }else{
            killer = "Red team";
            killerColor = getResources().getColor(R.color.redTeam);//Color.RED;
        }

        switch(e.getMonsterType()){
            case DRAGON:
                dragon.kill(e.getTimestamp());
                event =  " killed the dragon";
                break;
            case BARON_NASHOR:
                baron.kill(e.getTimestamp());
                event =  " killed the baron nashor";
                break;
            default:
                return;
        }

        timelineTextField.addItem(killer, killerColor, event);
    }

    /**
     * Handle building kill, get killer, building type, lane, and tower type.
     * @param e - event that was triggered.
     */
    private void handleBuildingKill(Event e){//}, Canvas c){
        String killer = "";
        String event = "";
        int killerColor;

        if(e.getKiller() == null){
            killer = "Minions";
        }else{
            killer = e.getKiller().getChampion().getName();
        }

        if(e.getTeam().getID() != Side.BLUE.getID()){
            killerColor = getResources().getColor(R.color.blueTeam);//Color.BLUE;
        }else{
            killerColor = getResources().getColor(R.color.redTeam);//Color.RED;
        }

        switch(e.getBuildingType()){
            case INHIBITOR_BUILDING:
                for(Inhibitor i : inhibitors){
                    if(e.getPosition().getX() == i.getX() && e.getPosition().getY() == i.getY()){
                        i.kill(e.getTimestamp());
                    }
                }

                event = " destroyed " + e.getLaneType().name().toLowerCase().replace("_"," ") + " inhibitor";
                break;
            case TOWER_BUILDING:
                for(Tower t : towers){
                    if(e.getPosition().getX() == t.getX() && e.getPosition().getY() == t.getY()){
                        t.kill();
                    }
                }
                event =  " destroyed " + e.getTowerType().name().toLowerCase().replace("_", " ")
                        + " in " + e.getLaneType().name().toLowerCase().replace("_", " ");
                break;
        }

        timelineTextField.addItem(killer, killerColor, event);
    }

    /**
     * Handle champion kill, get killer, victim and assisting participants. Change their location accordingly.
     * @param e - event that was triggered
     */
    public void handleChampionKill(Event e){
        String killer = null;
        String event = null;
        String victim = null;
        int killerColor;
        int victimColor;
        boolean executed;
        int multiKill = 1;
        String multiKillText = "";

        //If there is no killer then champion got executed.
        if(e.getKiller() == null){
            executed = true;
            victim = e.getVictim().getChampion().getName();
        }else{
            executed = false;
            killer = e.getKiller().getChampion().getName();

            for(Summoner summoner : summoners) {
                if(summoner.getParticipant().getParticipantID() == e.getKiller().getParticipantID()) {
                    //TODO: 30 seconds to Penta Kill after a Quadra Kill if no enemy respawns.
                    if (summoner.lastKillTimeStamp + 10*1000 >= (e.getTimestamp())) {
                        summoner.multiKill++;
                    } else {
                        summoner.multiKill = 1;
                    }

                    summoner.lastKillTimeStamp = e.getTimestamp();
                    multiKill = summoner.multiKill;
                    break;//Break the loop if summoner is found.
                }
            }
        }

        switch(multiKill){
            case 1:
                multiKillText = "";
                break;
            case 2:
                multiKillText = " for double kill.";
                break;
            case 3:
                multiKillText = " for triple kill.";
                break;
            case 4:
                multiKillText = " for QUADRA kill.";
                break;
            case 5:
                multiKillText = " for PENTA KILL!";
                break;
        }

        if(e.getVictim().getTeam().getID() != Side.BLUE.getID()){
            killerColor = getResources().getColor(R.color.blueTeam);//Color.BLUE;
            victimColor = getResources().getColor(R.color.redTeam);
        }else{
            killerColor = getResources().getColor(R.color.redTeam);//Color.RED;
            victimColor = getResources().getColor(R.color.blueTeam);
        }

        String assis = "";

        for(Participant p :  e.getAssistingParticipants()){
            assis += p.getChampion().getName() + ", ";
        }

        if(assis.length() > 1)
            assis = "["+assis.substring(0, assis.length() - 2)+"] - ";

        if(!firstBlood && !executed){
            firstBlood = true;
            victim = e.getVictim().getChampion().getName();
            event = " has drawn FIRST BLOOD! (";
            multiKillText = ")";
        }else if(executed) {
            multiKillText = " has been executed";
        }else{
            victim = e.getVictim().getChampion().getName();
            event = " has slain ";
        }

        timelineTextField.addItem(killer, killerColor, event, victim, victimColor, multiKillText);

        if(allChampionsEliminated(e.getVictim().getTeam().getID())){
            if(killerColor == getResources().getColor(R.color.blueTeam)){//Color.BLUE) {
                timelineTextField.addItem("Blue team ACE!");
            }else{
                timelineTextField.addItem("Red team ACE");
            }
        }
    }

    /**
     * Check if team got ACE
     * @param acedTeam - dead team
     * @return - isACE? true or false
     */
    public boolean allChampionsEliminated(long acedTeam){
        for(Summoner s : summoners){
            if(s.getParticipant().getTeam().getID() == acedTeam && !s.isDead()){
                return false;
            }
        }

        return true;
    }

    private void togglePlayPause(){
        if(isPlaying){
            pauseGame();
        }else{
            isPlaying = true;
            resumeGame();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if(width > height){
            size = height;
        }else{
            size = width;
        }

        setMeasuredDimension(size, size);
    }

    /**
     * Main function that gets called once MapView gets started
     * @param currentMatch - Match that will be displayed.
     */
    public void setCurrentMatch(Match currentMatch){
        this.currentMatch = currentMatch;
        currentFrame = 0;
        Log.e("mapView", "Match - " + currentMatch.getID());
        totalFrames = currentMatch.getTimeline().getFrames().size();
        getMapIcons();

        summoners = new ArrayList<Summoner>();
        currentEvents = new ArrayList<Event>();
      //  wards = new ArrayList<Ward>();
        getPlayers();
        paint = new Paint();
        h = new Handler();
        startTime = System.currentTimeMillis();
        setUpTowers();
        setUpInhibitors();
        // No if statement because no game can end in less then 1 minute..
        setUpNewEvents(currentMatch.getTimeline().getFrames().get(currentFrame+1).getEvents());

        invalidate();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
    }

    /**
     * Get map icons as drawables for towers and inhibitors.
     */
    private void getMapIcons(){
        map = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        turretDestroy = BitmapFactory.decodeResource(getResources(), R.drawable.turret_destroy);
        turretBlue = BitmapFactory.decodeResource(getResources(), R.drawable.turret_blue);
        turretRed = BitmapFactory.decodeResource(getResources(), R.drawable.turret_red);

        inhibDestroy = BitmapFactory.decodeResource(getResources(), R.drawable.inhibitor_destroy);
        inhibBlue = BitmapFactory.decodeResource(getResources(), R.drawable.inhibitor_blue);
        inhibRed = BitmapFactory.decodeResource(getResources(), R.drawable.inhibitor_red);

        nexusDestroy = BitmapFactory.decodeResource(getResources(), R.drawable.nexus_dead);
        nexusBlue = BitmapFactory.decodeResource(getResources(), R.drawable.nexus_blue);
        nexusRed = BitmapFactory.decodeResource(getResources(), R.drawable.nexus_red);

        dragon = new Dragon(this);
        baron = new Baron(this);

        turretDestroy = Bitmap.createScaledBitmap(turretDestroy, bitmapTurretWidth, bitmapTurretHeight, false);
        turretBlue = Bitmap.createScaledBitmap(turretBlue, bitmapTurretWidth, bitmapTurretHeight, false);
        turretRed = Bitmap.createScaledBitmap(turretRed, bitmapTurretWidth, bitmapTurretHeight, false);

        inhibDestroy = Bitmap.createScaledBitmap(inhibDestroy, bitmapInhibSize, bitmapInhibSize, false);
        inhibBlue = Bitmap.createScaledBitmap(inhibBlue, bitmapInhibSize, bitmapInhibSize, false);
        inhibRed = Bitmap.createScaledBitmap(inhibRed, bitmapInhibSize, bitmapInhibSize, false);

        nexusDestroy = Bitmap.createScaledBitmap(nexusDestroy, bitmapNexusSize, bitmapNexusSize, false);
        nexusBlue = Bitmap.createScaledBitmap(nexusBlue, bitmapNexusSize, bitmapNexusSize, false);
        nexusRed = Bitmap.createScaledBitmap(nexusRed, bitmapNexusSize, bitmapNexusSize, false);
    }

    // TODO: Add to array or file and load from that.

    /**
     * Setting up ArrayList with inhibitors and fill the data.
     */
    private void setUpInhibitors() {
        inhibitors = new ArrayList<>();

        //TEAM 200
        inhibitors.add(new Inhibitor(11261, 13676, 200)); // TOP
        inhibitors.add(new Inhibitor(11598, 11667, 200)); // MID
        inhibitors.add(new Inhibitor(13604, 11316, 200)); // BOT

        //TEAM 100
        inhibitors.add(new Inhibitor(1171, 3571, 100)); // TOP
        inhibitors.add(new Inhibitor(3203, 3208, 100)); // MID
        inhibitors.add(new Inhibitor(3452, 1236, 100)); // BOT
    }

    // TODO: Add to array or file and load from that.

    /**
     * Setting up ArrayList with towers and fill the data.
     */
    public void setUpTowers(){
        towers = new ArrayList<>();

        /**Team ID 200*/
        //OUTER TURRETS
        towers.add(new Tower(4318, 13875, 200)); // TOP
        towers.add(new Tower(8955, 8510, 200)); // MID
        towers.add(new Tower(13866, 4505, 200)); // BOT

        //INNER TURRETS
        towers.add(new Tower(7943, 13411, 200)); // TOP
        towers.add(new Tower(9767, 10113, 200)); // MID
        towers.add(new Tower(13327, 8226, 200)); // BOT

        //BASE TURRETS
        towers.add(new Tower(10481, 13650, 200)); // TOP
        towers.add(new Tower(11134, 11207, 200)); // MID
        towers.add(new Tower(13624, 10572, 200)); // BOT

        //NEXUS TURRETS
        towers.add(new Tower(13052, 12612, 200));
        towers.add(new Tower(12611, 13084, 200));


        /**Team ID 100*/
        //OUTER TURRETS
        towers.add(new Tower(981, 10441, 100)); // TOP
        towers.add(new Tower(5846, 6396, 100)); // MID
        towers.add(new Tower(10504, 1029, 100)); // BOT

        //INNER TURRETS
        towers.add(new Tower(1512, 6699, 100)); // TOP
        towers.add(new Tower(5048, 4812, 100)); // MID
        towers.add(new Tower(6919, 1483, 100)); // BOT

        //BASE TURRETS
        towers.add(new Tower(1169, 4287, 100)); //TOP
        towers.add(new Tower(3651, 3696, 100)); // MID
        towers.add(new Tower(4281, 1253, 100)); // BOT

        //NEXUS TURRETS
        towers.add(new Tower(1748, 2270, 100));
        towers.add(new Tower(2177, 1807, 100));
    }

    /**
     * Runnable for invalidating canvas.
     */
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if(isPlaying) {
                invalidate();
            }
        }
    };

    /**
     * Getting scaled float to fit on map.
     * @param x - double number that will get scaled.
     * @return - return scaled value
     */
    public float getScaledX(double x){
        double minX = 570;
        double maxX = 15220;
        int imageX = map.getWidth();

        double scaleFor = (maxX+minX)/imageX;
        return (float)((x/scaleFor)+(minX/scaleFor));
    }

    /**
     * Getting scaled float to fit on map.
     * @param y - double number that will get scaled.
     * @return - return scaled value
     */
    public float getScaledY(double y){
        double minY = 420;
        double maxY = 14980;
        int imageY = map.getHeight();

        double scaleFor = (maxY+minY)/imageY;
        return (float)((maxY-y)/scaleFor);
    }

    /**
     * Turn seconds to minutes:seconds
     * @param dur - time in seconds
     * @return - return String formated like "MM:SS"
     */
    private String getReadableMatchDuration(long dur){
        int minutes = (int)(dur / 60);
        int sec = (int)(dur % 60);
        String finalTime = "";

        if(minutes<10){
            finalTime += "0";
        }
        finalTime += minutes+":";

        if(sec<10){
            finalTime += "0";
        }

        finalTime += sec;

        return finalTime;
    }

    /**
     * Setting other views around MapView.
     * @param textFiled - Adapter for listView below map, used for events.
     * @param matchTimeTextField - Textview above map, shows current game time.
     */
    public void setTextFileds(MapEventListAdapter textFiled, TextView matchTimeTextField) {
        this.timelineTextField = textFiled;
        this.matchTimeTextField = matchTimeTextField;
    }

    /**
     * Getting events for nex minute of match.
     * @param newEvents - getting List of events, turning them to array list for easier manipulation.
     */
    public void setUpNewEvents(List<Event> newEvents) {
        currentEvents.clear();

        for(Event e : newEvents){
            currentEvents.add(e);
        }
    }

    /**
     * Pausing the game.
     */
    public void pauseGame(){
        isPlaying = false;
    }

    /**
     * Resuming the game.
     */
    public void resumeGame(){
     //   isPlaying = true;
        startTime = System.currentTimeMillis() - timePassed;
        invalidate();
    }
}