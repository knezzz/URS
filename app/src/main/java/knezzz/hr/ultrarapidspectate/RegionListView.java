package knezzz.hr.ultrarapidspectate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Region list view.
 * Created by knezzz on 17/04/15.
 */
public class RegionListView extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    ArrayList<String> regions;
    String currentRegion;

    public RegionListView(Activity activity, ArrayList<String> regions, String currentRegion) {
        this.activity = activity;
        this.regions = regions;
        this.currentRegion = currentRegion;
    }

    @Override
    public int getCount() {
        return regions.size();
    }

    @Override
    public Object getItem(int position) {
        return regions.get(position);
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
            convertView = inflater.inflate(R.layout.region_info, null);
        }

        String currentRegion = regions.get(position);
        TextView regionView = (TextView) convertView.findViewById(R.id.region_info);
        regionView.setTextColor(Color.BLACK);

        if(this.currentRegion.equals(currentRegion)){
            regionView.setTypeface(Typeface.DEFAULT_BOLD);
            regionView.setBackgroundColor(Color.LTGRAY);
        }else{
            regionView.setBackgroundColor(Color.WHITE);
            regionView.setTypeface(Typeface.DEFAULT);
        }

        regionView.setText(currentRegion+"");

        return convertView;
    }
}
