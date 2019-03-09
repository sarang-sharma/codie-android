package org.codiecon.reportit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.codiecon.reportit.auth.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager manager = new SessionManager(getApplicationContext());
        manager.authenticate();
    }
}
