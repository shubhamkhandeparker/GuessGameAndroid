package com.example.guessgameandroid;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
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

    SharedPreferences sharedPreferences;
    // Placing Random Number variable Here
    Random random=new Random();
    int randomNumber=random.nextInt(100)+1;
    int attemptsLeft=5;  // User has 5 tries
    HashSet<Integer> previousGuess = new HashSet<>();
    EditText etGuess;
    MaterialButton btnsubmit;
    TextView tvAttempts;
    Button btnRestart;

    TextView tvFeedback;

    LinearLayout guessHistoryLayout;

    TextView tvHighScoreValue;
    private ImageView[] hearts ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etGuess=  findViewById(R.id.etGuess); //EditText
        btnsubmit =findViewById(R.id.btnsubmit); //Button
        //tvAttempts =findViewById(R.id.tvAttempts); //Attempts Left
        btnRestart=findViewById(R.id.btnRestart); //To restart The game
        tvFeedback=findViewById(R.id.tvFeedback); //For Feedback
        guessHistoryLayout=findViewById(R.id.guessHistoryLayout); //To show Guess History
        btnsubmit.setOnClickListener(v -> handleGuess());

        //Shared Preference on local device
        sharedPreferences=getSharedPreferences("GamePrefs",MODE_PRIVATE);
        int highScore=sharedPreferences.getInt("HighScore ",0); //Default Value is 0
        tvHighScoreValue = findViewById(R.id.tvHighScoreValue);
        tvHighScoreValue.setText(""+highScore);
        ImageView btnback=findViewById(R.id.homeButton);
        btnback.setOnClickListener(v->showRestartConfirmation());




        //--Step A. grab the LinearLayout that host  our heart--

        LinearLayout heartContainer =findViewById(R.id.heartContainer);

        //--Step B. Make our Array exactly big as the numbers of hearts in xml --\
        hearts=new ImageView[heartContainer.getChildCount()];

        //--Step C. Loop once per child,casting each to ImageView--
        for(int i=0;i<heartContainer.getChildCount();i++){
            hearts[i]=(ImageView) heartContainer.getChildAt(i);
        }

        //--Step D. get the initial display in sync with attepmtletf--
        updateAttemptsUI();






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    private void  handleGuess(){

        // 🔔 Play a \"ding\" sound on each tap
        MediaPlayer dingPlayer=MediaPlayer.create(this,R.raw.ding);
        dingPlayer.start();
        dingPlayer.setOnCompletionListener(MediaPlayer::release);

        String guessText=etGuess.getText().toString().trim();
        if(guessText.isEmpty()){
            etGuess.setError("Enter the number");
            return;

        }
        int guess=Integer.parseInt(guessText);
        if(previousGuess.contains(guess)){
            Toast.makeText(this,"Already guessed",Toast.LENGTH_SHORT).show();
            return;
        }
        previousGuess.add(guess);
        attemptsLeft--;
        appendGuessToHistory(guess);
        updateAttemptsUI();

        if(guess==randomNumber){
            // 🎉 Play cheer sound on win
            MediaPlayer cheerPlayer=MediaPlayer.create(this,R.raw.cheer);
            cheerPlayer.setOnCompletionListener(mp->mp.release());
            cheerPlayer.start();
            //  win logic
            saveHighScoreIfNeeded();
            updateFeedback("YOU WIN!");
            showRestartDialog("\uD83C\uDF89 You Won");
            btnsubmit.setEnabled(false);
        } else if (attemptsLeft==0) {
            //Play Lose sound if Game is over
            MediaPlayer losePlayer=MediaPlayer.create(this,R.raw.lose);
            losePlayer.setOnCompletionListener(mp -> mp.release());
            losePlayer.start();

            // Now show feedback and dialog
            updateFeedback("YOU LOSE!");
            showRestartDialog("YOU LOSE ! NUMBER WAS :"+randomNumber);
            btnsubmit.setEnabled(false);
            
        }else{
            updateFeedback(guess<randomNumber?"TOO LOW! ": "TOO HIGH!");
        }
        etGuess.getText().clear();
    }

    public void  showRestartDialog(String message){

        View dialogView = getLayoutInflater().inflate(R.layout.custom_restart_dialog,null,false);

        TextView tvGameOver=dialogView.findViewById(R.id.tvGameOver);
        TextView tvMessage=dialogView.findViewById(R.id.tvMessage);
        MaterialButton btnRestart =dialogView.findViewById(R.id.btnDialogRestart);

        if(message.startsWith("\uD83C\uDF89")) {
            tvGameOver.setText(message);
            tvMessage.setVisibility(View.GONE);
        }else {
            tvGameOver.setText("YOU LOSE!");
            tvMessage.setText("The number was " + randomNumber);
        }
        AlertDialog dialog=new MaterialAlertDialogBuilder(this).setView(dialogView).setCancelable(false).create();
        dialog.show();

        btnRestart.setOnClickListener(v -> {
            dialog.dismiss();

            // 🔄 Play the "restart" sound effect
            MediaPlayer restartPlayer=MediaPlayer.create(this,R.raw.restart);
            restartPlayer.start();

            //When the sound ends the Game resets

            restartPlayer.setOnCompletionListener(mp-> {
                mp.release();
                restartGame();
            });


            btnsubmit.setEnabled(false);
            etGuess.setEnabled(false);
        });
    }

    private void  restartGame() {
        //1.New target Number
        randomNumber = random.nextInt(100) + 1;

        //2.Reset attempts & clear History
        attemptsLeft = 5;
        previousGuess.clear();
        guessHistoryLayout.removeAllViews();

        //3.Reset UI
        tvFeedback.setText("");
        btnsubmit.setEnabled(true);
        etGuess.getText().clear();

        //4.clear,re-enable ,and remove any error form the guess input
        etGuess.setEnabled(true);
        etGuess.setError(null);
        etGuess.getText().clear();
        etGuess.requestFocus();



        //5.Refresh Attempts Display
        updateAttemptsUI();
    }






        private void updateFeedback(String message){
        //Set The text message
            tvFeedback.setText(message);

        //Load the bounce animation from anim resources
            Animation bounceAnimation= AnimationUtils.loadAnimation(this,R.anim.bounce);

        //start animation on tvFeedback view
            tvFeedback.startAnimation(bounceAnimation);

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





    private void updateAttemptsUI(){
        //1.refresh the text
       // tvAttempts.setText("Attempt Left:"+attemptsLeft);

        //2.for each heart index
        for(int i=0;i< hearts.length;i++)
        {
            if(i<attemptsLeft){
                //fill the heart
                hearts[i].setImageResource(R.drawable.ic_heart_filled);
            }else{
                hearts[i].setImageResource(R.drawable.ic_heart_empty);
            }
        }
    }


    private void saveHighScoreIfNeeded(){
        int highScore=sharedPreferences.getInt("HighScore",0);
        if(attemptsLeft>highScore){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("HighScore",attemptsLeft);
            editor.apply();
            tvHighScoreValue.setText(""+attemptsLeft);
        }
    }

    private void showRestartConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("\uD83D\uDD04 Restart Game")
                .setMessage("Are you sure you want to restart your current game?")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setPositiveButton("Restart", (dialog, which) -> {
                    dialog.dismiss();
                    MediaPlayer restartPlayer = MediaPlayer.create(this, R.raw.restart);
                    restartPlayer.start();
                    restartPlayer.setOnCompletionListener(mp -> {
                        mp.release();
                        restartGame();
                    });
                })
                .setCancelable(false)
                .show();
    }

}








