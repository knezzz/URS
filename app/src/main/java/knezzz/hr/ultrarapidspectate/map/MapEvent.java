package knezzz.hr.ultrarapidspectate.map;

/**
 * Created by knezzz on 16/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class MapEvent {
    String killer;
    String victim;
    String eventText;
    String multiKillText;

    Integer killerColor;
    Integer victimColor;

    public MapEvent(String killer, Integer killerColor, String eventText, String victim, Integer victimColor, String multiKillText){
        this.killer = killer;
        this.killerColor = killerColor;
        this.eventText = eventText;
        this.victim = victim;
        this.victimColor = victimColor;
        this.multiKillText = multiKillText;
    }
}
