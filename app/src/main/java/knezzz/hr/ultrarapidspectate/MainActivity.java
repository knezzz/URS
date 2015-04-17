package knezzz.hr.ultrarapidspectate;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.exception.APIException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import knezzz.hr.ultrarapidspectate.map.SpectateMain;


public class MainActivity extends ActionBarActivity {
    private ListView matchView;
    private MatchListView matchList;
    private List<Match> URFMatches;
    private ProgressBar loadingBar;
    private TextView epochTime, regionView;
    long matchSeconds;//1428495600000l;
    private Region region;// = Region.EUNE;
    static final int PICK_REGION_REQUEST = 1;
    static final int PICK_DATE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("regionSettings", MODE_PRIVATE);
        region = Region.valueOf(sp.getString("region", Region.EUNE.name()));
        matchSeconds = sp.getLong("time", 1427866200000l);

        if(!APIKey.KEY.isEmpty()) {
            RiotAPI.setMirror(region);
            RiotAPI.setRegion(region);
            RiotAPI.setAPIKey(APIKey.KEY);

            loadingBar = (ProgressBar) findViewById(R.id.progress);
            epochTime = (TextView) findViewById(R.id.epochTime);
            regionView = (TextView) findViewById(R.id.region);

            loadingBar.setVisibility(View.VISIBLE);
            epochTime.setText("" + getReadableDate(matchSeconds));

            regionView.setTextColor(Color.DKGRAY);
            regionView.setText(region.name());

            epochTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, DateChooser.class);
                    i.putExtra("currentEpoch", matchSeconds);
                    startActivityForResult(i, PICK_DATE_REQUEST);
                }
            });

            regionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, RegionPicker.class);
                    i.putExtra("region", region.name());
                    startActivityForResult(i, PICK_REGION_REQUEST);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getURF();
                }
            }).start();

            matchView = (ListView) findViewById(R.id.matchListview);
        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("API Key missing");
            builder.setMessage("Put your api key in APIKey class.");
            builder.setCancelable(false);
            builder.create();
            builder.show();
        }
    }

    /**
     * Changing region
     * @param region - region to change to.
     */
    private void changeRegion(Region region){
        this.region = region;
        RiotAPI.setMirror(region);
        RiotAPI.setRegion(region);

        regionView.setText(region.name());
        loadingBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getURF();
            }
        }).start();
    }

    /**
     * Turn epoch date to human readable date.
     * @param epoch - epoch time.
     * @return - String, date in readable form.
     */
    private String getReadableDate(long epoch){
        Date d = new Date(epoch);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss zzz", Locale.getDefault());

        return df.format(d);
    }

    private void changeEpoch(long epoch){
        matchSeconds = epoch;

        epochTime.setText("" + getReadableDate(matchSeconds));
        loadingBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getURF();
            }
        }).start();
    }

    private void getURF(){
        try {
            URFMatches = RiotAPI.getURFMatches(matchSeconds);
            Log.i("URF matches: ", URFMatches.size() + "");
        }catch(APIException apiE){
            region = Region.EUNE;
            Log.e("Error getting matches:", apiE.getMessage());
            finish();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingBar.setVisibility(View.INVISIBLE);
                matchList = new MatchListView(MainActivity.this, URFMatches);
                matchView.setAdapter(matchList);
                matchView.setOnItemClickListener(clickListener);
            }
        });
    }

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("OnItemClickListener", "Clicked on: " + URFMatches.get(position).getID());
            Intent i = new Intent(MainActivity.this, SpectateMain.class);
            i.putExtra("match", URFMatches.get(position));
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_REGION_REQUEST) {
            if (resultCode == RESULT_OK) {
                String newRegion = data.getStringExtra("newRegion");

                Region r = Region.valueOf(newRegion);
                Log.d("Got new region", r.name()+"");
                changeRegion(r);
            }
        }else if(requestCode == PICK_DATE_REQUEST){
            if(resultCode == RESULT_OK){
                long newEpoch = data.getLongExtra("epoch", 1427866200000l);
                Log.e("Got new date epoch", ""+newEpoch);
                changeEpoch(newEpoch);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        SharedPreferences sp = getSharedPreferences("regionSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("region", region.name());
        editor.putLong("time", matchSeconds);

        editor.apply();
    }
}
