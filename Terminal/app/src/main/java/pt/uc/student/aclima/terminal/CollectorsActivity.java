package pt.uc.student.aclima.terminal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pt.uc.student.aclima.terminal.Collectors.PeriodicDataCollector.PeriodicDataCollector;

public class CollectorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectors);

        PeriodicDataCollector.startActionRAM(this, "foo", "bar");

    }

}
