package knezzz.hr.ultrarapidspectate.map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import knezzz.hr.ultrarapidspectate.R;

/**
 * Created by knezzz on 08/04/15 in knezzz.hr.ultrarapidspectate.map.
 */
public class MapEventListAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    ArrayList<MapEvent> events;
    int colorGray;

    public MapEventListAdapter(Activity activity){
        this.activity = activity;
        events = new ArrayList<>();
        colorGray = activity.getResources().getColor(R.color.grey);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;//events.get(position);
    }

    public void addItem(String event){
        addItem(null, null, event, null, null, null);
    }

    public void addItem(String killer, Integer killerColor, String event){
        addItem(killer, killerColor, event, null, null, null);
    }

    public void addItem(String killer, Integer killerColor, String event, String victim, Integer textColor, String multiKill){
        events.add(0, new MapEvent(killer, killerColor, event, victim, textColor, multiKill));
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            convertView = inflater.inflate(R.layout.map_timeline_list, null);
        }

        TextView killerTextView = (TextView) convertView.findViewById(R.id.killerText);
        TextView timelineTextView = (TextView) convertView.findViewById(R.id.mainText);
        TextView victimTextView = (TextView) convertView.findViewById(R.id.victimText);
        TextView multiKillTextView = (TextView) convertView.findViewById(R.id.multiKillText);

        MapEvent currentEvent = events.get(position);
        //Log.d("currentEvent", currentEvent.killer+"/"+currentEvent.eventText+"/"+currentEvent.victim+"/"+currentEvent.multiKillText);

        if(currentEvent.killer != null) {
            killerTextView.setTextColor(currentEvent.killerColor);
            killerTextView.setText(currentEvent.killer);
            if(currentEvent.killer.equals("Blue team") || currentEvent.killer.equals("Red team")){
                timelineTextView.setTextColor(currentEvent.killerColor);
            }else {
                timelineTextView.setTextColor(colorGray);//currentEvent.killerColor);
            }
        }else{
            killerTextView.setText("");
            timelineTextView.setTextColor(colorGray);//currentEvent.killerColor);
        }

        timelineTextView.setText(currentEvent.eventText);

        if(currentEvent.victim != null) {
            victimTextView.setTextColor(currentEvent.victimColor);
            victimTextView.setText(currentEvent.victim);
        }else{
            victimTextView.setText("");
        }

        if(currentEvent.multiKillText != null){
            multiKillTextView.setTextColor(colorGray);
            multiKillTextView.setText(currentEvent.multiKillText);
        }else{
            multiKillTextView.setText("");
        }

        return convertView;
    }
}
