package knezzz.hr.ultrarapidspectate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateChooser extends Activity {
    NumberPicker day, hour, minute;
    Button ok, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_chooser);

        day = (NumberPicker) findViewById(R.id.day);
        hour = (NumberPicker) findViewById(R.id.hour);
        minute = (NumberPicker) findViewById(R.id.minute);

        ok = (Button) findViewById(R.id.okButton);
        cancel = (Button) findViewById(R.id.cancelButton);

        setUpButtonListeners();


        day.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        day.setMinValue(1);
        day.setMaxValue(14);

        hour.setMinValue(0);
        hour.setMaxValue(23);

        String[] valueSet = new String[12];

        for (int i = 0; i < 12; i++) {
            valueSet[i] = String.valueOf(i*5);
        }

        minute.setMinValue(0);
        minute.setMaxValue(valueSet.length-1);
        minute.setDisplayedValues(valueSet);

        long currentEpoch = getIntent().getLongExtra("currentEpoch", 1427866200000l);
        Date date = new Date(currentEpoch);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        day.setValue(cal.get(Calendar.DAY_OF_MONTH));
        hour.setValue(cal.get(Calendar.HOUR_OF_DAY));
        minute.setValue(cal.get(Calendar.MINUTE) * 5);
    }

    private void setUpButtonListeners(){
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = "Apr "+day.getValue()+" 2015 "+hour.getValue()+":"+(minute.getValue()*5)+":00";
                SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.getDefault());
                Date date;
                try {
                    date = df.parse(d);
                    long epoch = date.getTime();
                    Intent i = new Intent();
                    i.putExtra("epoch", epoch);
                    setResult(RESULT_OK, i);
                    finish();
                }catch(Exception e){
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
