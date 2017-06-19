package ghoster.supersimpleedhcounter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        addOnclickto((Button)findViewById(R.id.life_increase), (TextView)findViewById(R.id.life_counter), -1);
        addOnclickto((Button)findViewById(R.id.life_decrease), (TextView)findViewById(R.id.life_counter), 1);

        addOnclickto((Button)findViewById(R.id.poison_increase), (TextView)findViewById(R.id.poison_counter), -1);
        addOnclickto((Button)findViewById(R.id.poison_decrease), (TextView)findViewById(R.id.poison_counter), 1);

        addOnclickto((Button)findViewById(R.id.commander_increase), (TextView)findViewById(R.id.commander_counter), -1);
        addOnclickto((Button)findViewById(R.id.commander_decrease), (TextView)findViewById(R.id.commander_counter), 1);

        findViewById(R.id.utility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main.this, Extras.class);
                startActivity(intent);
            }
        });
    }




    private void addOnclickto(Button button, final TextView text, final int state)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if (!text.getText().toString().equals("")) {

                    int value = Integer.parseInt(text.getText().toString());

                    value += state;

                    text.setText(Integer.toString(value));
                }
                else
                {
                    text.setText(Integer.toString(0));
                }
            }
        });
    }
}

