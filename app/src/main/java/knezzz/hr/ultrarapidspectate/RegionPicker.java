package knezzz.hr.ultrarapidspectate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.robrua.orianna.type.core.common.Region;

import java.util.ArrayList;

/**
 * Region picker activity. Dialog.
 */
public class RegionPicker extends Activity {
    ArrayList<String> regions;
    String currentRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_picker);

        regions = new ArrayList<>();

        for(Region r : Region.values()){
            regions.add(r.name());
        }

        currentRegion = getIntent().getStringExtra("region");

        ListView regionList = (ListView) findViewById(R.id.regions);
        RegionListView rlv = new RegionListView(this, regions, currentRegion);
        regionList.setAdapter(rlv);
        regionList.setOnItemClickListener(regionClickListener);
    }

    AdapterView.OnItemClickListener regionClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent();
            i.putExtra("newRegion", regions.get(position));
            setResult(RESULT_OK, i);
            finish();
        }
    };
}
