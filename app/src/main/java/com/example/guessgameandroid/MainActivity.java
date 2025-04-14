package com.example.guessgameandroid;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Placing Random Number variable Here
    Random random=new Random();
    int randomNumber=random.nextInt(100)+1;
    int attemptsLeft=5;  // User has 5 tries
    HashSet<Integer> previousGuess = new HashSet<>();
    EditText etGuess;
    Button btnsubmit;
    TextView tvAttempts;
    Button btnRestart;

    TextView tvFeedback;

    LinearLayout guessHistoryLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

         etGuess=  findViewById(R.id.etGuess); //EditText
        btnsubmit =findViewById(R.id.btnsubmit); //Button
        tvAttempts =findViewById(R.id.tvAttempts); //Attempts Left
        btnRestart=findViewById(R.id.btnRestart); //To restart The game
        tvFeedback=findViewById(R.id.tvFeedback); //For Feedback
        guessHistoryLayout=findViewById(R.id.guessHistoryLayout); //To show Guess History


        btnsubmit.setOnClickListener(new View.OnClickListener(){

            @Override
                    public void onClick(View v){
                // Game logic will be here

                String guessText=etGuess.getText().toString().trim();
                btnRestart.setVisibility(View.GONE);

                if(guessText.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter The Number",Toast.LENGTH_LONG).show();
                    return;

                }
                    int guess=Integer.parseInt(guessText);

                // Check For Duplicate Guess
                    if(previousGuess.contains(guess)){
                        Toast.makeText(MainActivity.this,"Already Guessed ",Toast.LENGTH_LONG).show();
                        return;
                    }
                    previousGuess.add(guess);
                    attemptsLeft--;
                    tvAttempts.setText("Attempts Left : "+attemptsLeft);

                    //Add the guess history so that it remains on screen
                appendGuessToHistory(guess);

                    //Evaluate guess only once

                    if(guess == randomNumber){
                        updateFeedback("You Won!");
                        showRestartDialog("\uD83C\uDF89 You Won");
                        btnsubmit.setEnabled(false);
                    } else if (attemptsLeft==0) {
                        showRestartDialog("You Lose ! Number was :"+randomNumber);
                        btnsubmit.setEnabled(false);

                    }

                    //Now Compare it with randomNumber

                    if(guess<randomNumber){
                        updateFeedback("Too Low!");

                    }
                    else if (guess>randomNumber) {
                        updateFeedback("Too High!");

                    }


                    if(attemptsLeft==0){
                        Toast.makeText(MainActivity.this,"You Lost The Number was : "+randomNumber,Toast.LENGTH_LONG).show();
                        btnsubmit.setEnabled(false); //Disables the "Submit" Button to Stop the game .
                    }


                    etGuess.setText("");
                    etGuess.requestFocus();


            }
        }) ;



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void  showRestartDialog(String message){
        // Step 1:Create a Dialog builder to create the custom dialog
        AlertDialog. Builder builder=new AlertDialog.Builder(MainActivity.this);

        //Step 2:Set the view of the dialog to your custom layout ( Custom_restart_dialog.xml)
        View dialogView= getLayoutInflater().inflate(R.layout.custom_restart_dialog,null);
        builder.setView(dialogView);

        // Prevent the dialog from being canceled by tapping outside or pressing back
        builder.setCancelable(false);

        //Step 3:Find the text view and Button inside the dialog layout

        TextView tvGameOver =dialogView.findViewById(R.id.tvGameOver);
        Button btnRestart=dialogView.findViewById(R.id.btnDialogRestart);

        //Step 4: Set Message Dynamically (win or Lose )
        tvGameOver.setText(message);

        //Step 5: Handel the restart button click
        btnRestart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                randomNumber=random.nextInt(100)+1;
                attemptsLeft=5;  // User has 5 tries
                previousGuess.clear();

                btnsubmit.setEnabled(true); //Re-Enable the submit button
                etGuess.setText(""); //Clear the input
                tvAttempts.setText("Attempts Left "+ attemptsLeft);
                btnRestart.setVisibility(View.GONE);
                tvFeedback.setText("");
                guessHistoryLayout.removeAllViews();

                Toast.makeText(MainActivity.this,"Game Restarted ",Toast.LENGTH_SHORT).show();




            }
        });

        AlertDialog dialog= builder.create();
        dialog.show();

        }

        private void updateFeedback(String message){
        //Set The text message
            tvFeedback.setText(message);

        //Start with text view hidden
        tvFeedback.setAlpha(0f);

        //Face in over 500ms
            tvFeedback.animate().alpha(1f).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    //hold the message in 2 seconds and fade out in 500ms
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          tvFeedback.animate().alpha(0f).setDuration(500);
                        }
                    },2000);
                }
            });

        }


        //Create a Helper Method to Append Previous Guesses
    private void appendGuessToHistory(int Guess){
        //Create a Textview for Guess
        TextView guessItem=new TextView(this);
        guessItem.setText("Your guess :"+Guess);
        guessItem.setTextColor(getResources().getColor(android.R.color.white));
        guessItem.setTextSize(16);

        //Setting some Padding and Margin
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,8,0,8);
        guessItem.setLayoutParams(params);


        //Additional Small Fade in Animation
        guessItem.setAlpha(0f);
        guessHistoryLayout.addView(guessItem);
        guessItem.animate().alpha(1f).setDuration(300);
    }







    }



