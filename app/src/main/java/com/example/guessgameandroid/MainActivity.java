package com.example.guessgameandroid;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Placing Random Number variable Here
    Random random=new Random();
    int randomNumber=random.nextInt(100)+1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        EditText etGuess=  findViewById(R.id.etGuess); //EditText
        Button btnsubmit =findViewById(R.id.btnsubmit); //Button
        btnsubmit.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                // Game logic will be here

                String guessText=etGuess.getText().toString();
                if(!guessText.isEmpty()){
                    int guess=Integer.parseInt(guessText);

                    //Now Compare it with randomNumber

                    if(guess<randomNumber){
                        Toast.makeText(MainActivity.this,"TOO LOW!",Toast.LENGTH_SHORT).show();

                    }
                    else if (guess>randomNumber) {
                        Toast.makeText(MainActivity.this,"TOO HIGH!",Toast.LENGTH_SHORT).show();

                    } else if (guess==randomNumber) {
                        Toast.makeText(MainActivity.this,"YOU WON!",Toast.LENGTH_SHORT).show();

                    }

                }


            }
        }) ;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}