package knezzz.hr.ultrarapidspectate.map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.robrua.orianna.type.core.match.Match;

import knezzz.hr.ultrarapidspectate.R;

public class SpectateMain extends ActionBarActivity {
    TextView matchTimeTextview;
    ListView timelineListView;
    MapEventListAdapter timelineAdapter;
    Match currentMatch;
    MapView imageviewMap;
    int totalFrames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectate_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timelineListView = (ListView) findViewById(R.id.spectateTimeline);
        timelineAdapter = new MapEventListAdapter(this);
        timelineListView.setAdapter(timelineAdapter);

        matchTimeTextview = (TextView) findViewById(R.id.matchTime);
        imageviewMap = (MapView) findViewById(R.id.spectateMap);

        currentMatch = (Match) getIntent().getExtras().get("match");
        totalFrames = currentMatch.getTimeline().getFrames().size();
        imageviewMap.setCurrentMatch(currentMatch);
        imageviewMap.setTextFileds(timelineAdapter, matchTimeTextview);
    }

    @Override
    public void onPause(){
        super.onPause();
        imageviewMap.pauseGame();
    }

    @Override
    public void onResume(){
        super.onResume();
        imageviewMap.resumeGame();
    }
}
