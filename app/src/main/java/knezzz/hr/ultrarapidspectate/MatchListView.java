package knezzz.hr.ultrarapidspectate;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.core.match.Participant;

import java.util.List;

/**
 * Created by knezzz on 04/04/15 in knezzz.hr.ultrarapidspectate.
 *
 * Created for RIOT API Challange.
 *
 * Ultra Rapid Spectate is (hopefully will be) fast game representation from top-down view of map.
 * - Movement is not exact as in game but will be simulated from using
 * API data and probably fast enough so you don't notice any big mistakes.
 *
 */
public class MatchListView extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    List<Match> matches;

    public MatchListView(Activity activity, List<Match> matches) {
        this.activity = activity;
        this.matches = matches;
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    @Override
    public Object getItem(int position) {
        return matches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            convertView = inflater.inflate(R.layout.game_info, null);
        }

        Match currentMatch = matches.get(position);

        TextView blueKills = (TextView) convertView.findViewById(R.id.scoreTeam1);
        TextView redKills = (TextView) convertView.findViewById(R.id.scoreTeam2);

        int bk, rk;

        bk = 0;
        rk = 0;

        for(Participant p : currentMatch.getParticipants()){
            if(p.getTeam().getID() == 100){
                bk += p.getStats().getKills();
            }else{
                rk += p.getStats().getKills();
            }

            int imageResource = getChampionImageResource(p.getChampion().getName());

            switch(p.getParticipantID()){
                case 1:
                    convertView.findViewById(R.id.summoner1).setBackgroundResource(imageResource);
                    break;
                case 2:
                    convertView.findViewById(R.id.summoner2).setBackgroundResource(imageResource);
                    break;
                case 3:
                    convertView.findViewById(R.id.summoner3).setBackgroundResource(imageResource);
                    break;
                case 4:
                    convertView.findViewById(R.id.summoner4).setBackgroundResource(imageResource);
                    break;
                case 5:
                    convertView.findViewById(R.id.summoner5).setBackgroundResource(imageResource);
                    break;
                case 6:
                    convertView.findViewById(R.id.summoner6).setBackgroundResource(imageResource);
                    break;
                case 7:
                    convertView.findViewById(R.id.summoner7).setBackgroundResource(imageResource);
                    break;
                case 8:
                    convertView.findViewById(R.id.summoner8).setBackgroundResource(imageResource);
                    break;
                case 9:
                    convertView.findViewById(R.id.summoner9).setBackgroundResource(imageResource);
                    break;
                case 10:
                    convertView.findViewById(R.id.summoner10).setBackgroundResource(imageResource);
                    break;
            }
        }

        blueKills.setText(""+bk);
        redKills.setText(""+rk);

        return convertView;
    }

    /**
     * Getting custom champion image.
     * @param championName - champion name.
     * @return - return Bitmap image of champion.
     */
    private int getChampionImageResource(String championName){
        championName = championName.toLowerCase();
        championName = championName.trim();
        championName = championName.replace(" ", "").replace(".", "").replace("'","");

        int imageResource = activity.getResources().getIdentifier(championName, "drawable", activity.getPackageName());

        if(imageResource == 0){
            Log.e("ERROR: ", "Image ("+championName+".png) not found.");
        }

        return imageResource;
    }
}
