package knezzz.hr.ultrarapidspectate;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.api.Action;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.exception.APIException;

import knezzz.hr.ultrarapidspectate.map.SpectateMain;


public class SearchActivity extends ActionBarActivity {
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if(query.length() != 10){
                searchError(query, "Match ID has to have 10 digits!");
                return;
            }

            SharedPreferences sp = getSharedPreferences("regionSettings", MODE_PRIVATE);
            region = Region.valueOf(sp.getString("region", Region.EUNE.name()));

            getCustomMatch(Long.parseLong(query));


            Log.e("Searching for", "query - "+ query);
        }
    }

    Match customMatch;

    private void getCustomMatch(final long matchId){
        AsyncRiotAPI.setMirror(region);
        AsyncRiotAPI.setRegion(region);
        AsyncRiotAPI.setAPIKey(APIKey.KEY);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AsyncRiotAPI.getMatch(new Action<Match>() {
                    @Override
                    public void handle(APIException e) {
                        searchError("Match ID: " + matchId, e.getStatus().toString());
                    }

                    @Override
                    public void perform(Match match) {
                        customMatch = match;
                    }
                }, matchId);
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(customMatch != null) {
            Intent i = new Intent(SearchActivity.this, SpectateMain.class);
            i.putExtra("match", customMatch);
            startActivity(i);
        }else{
            Log.e("Something went wrong", "Match not found");
        }
    }

    public void searchError(final String query, final String cause){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView errorView = (TextView) findViewById(R.id.searchText);
                errorView.setText(query + " - " + cause);
            }
        });
    }
}
