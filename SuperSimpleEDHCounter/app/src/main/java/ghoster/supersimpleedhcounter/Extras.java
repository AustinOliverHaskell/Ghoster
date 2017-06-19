package ghoster.supersimpleedhcounter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Extras extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_extras);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        findViewById(R.id.flip_a_coin_bttn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button bttn = (Button) findViewById(R.id.flip_a_coin_bttn);

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                bttn.setBackgroundColor(color);

                int rand = (int) Math.round(Math.random());

                if (rand == 0)
                {
                    bttn.setText("Heads");
                }
                else
                {
                    bttn.setText("Tails");
                }
            }
        });

        findViewById(R.id.generate_random_bttn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.random_text);

                int max;
                if (text.getText().toString().equals(""))
                {
                    max = -1;
                }
                else
                {
                    max = (int) (1 + Math.random() * Double.parseDouble(text.getText().toString()));
                }

                Toast.makeText(getApplicationContext(), Integer.toString(max), Toast.LENGTH_LONG).show();
            }
        });


    }

}