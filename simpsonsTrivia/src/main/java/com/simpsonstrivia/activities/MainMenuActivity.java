package com.simpsonstrivia.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.simpsonstrivia.MyApplication;
import com.triviabilities.simpsonstrivia.R;
import com.simpsonstrivia.enums.Difficulty;
import com.simpsonstrivia.enums.GamePlayType;
import com.simpsonstrivia.models.GameMode;
import com.simpsonstrivia.utils.IntentManager;
import com.simpsonstrivia.utils.Measure;
import com.simpsonstrivia.views.CircularTransition;
import com.simpsonstrivia.views.SingleTapGesture;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/* Home Screen */
public class MainMenuActivity extends AppCompatActivity implements View.OnTouchListener
{
	private ImageView titleText;
	private ImageView titleSubText;

	Button ChallengeButton;
	Button SpeedChallengeButton;
	Button HelpButton;
	Button HighScoreButton;

	Button EasyChallengeButton;
	Button HardChallengeButton;
	Button EasySpeedButton;
	Button HardSpeedButton;

	RelativeLayout ClassicLayout;
	RelativeLayout SpeedLayout;

	private FrameLayout root;

	private boolean introAnimationsPlayed = false;

	private GestureDetector gestureDetector;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

		gestureDetector = new GestureDetector(this, new SingleTapGesture());

		ClassicLayout = (RelativeLayout) findViewById(R.id.ClassicLayout);
		ClassicLayout.setVisibility(View.INVISIBLE);
		SpeedLayout = (RelativeLayout) findViewById(R.id.SpeedLayout);
		SpeedLayout.setVisibility(View.INVISIBLE);

		root = (FrameLayout) findViewById(R.id.randomasslayout);

		titleText = (ImageView) findViewById(R.id.TitleText);
		titleText.setVisibility(View.INVISIBLE);
		titleSubText = (ImageView) findViewById(R.id.TitleSubText);
		titleSubText.setVisibility(View.INVISIBLE);

        ChallengeButton      = (Button) findViewById(R.id.ChallengeButton);
        SpeedChallengeButton = (Button) findViewById(R.id.SpeedChallengeButton);
        HelpButton           = (Button) findViewById(R.id.HelpButton);
        HighScoreButton      = (Button) findViewById(R.id.HighScoreButton);

		EasyChallengeButton = (Button) findViewById(R.id.EasyChallengeButton);
		HardChallengeButton = (Button) findViewById(R.id.HardChallengeButton);
		EasySpeedButton     = (Button) findViewById(R.id.EasySpeedButton);
		HardSpeedButton     = (Button) findViewById(R.id.HardSpeedButton);

		ChallengeButton.setOnTouchListener(this);
		SpeedChallengeButton.setOnTouchListener(this);
		HelpButton.setOnTouchListener(this);
		HighScoreButton.setOnTouchListener(this);

		EasyChallengeButton.setOnTouchListener(this); EasyChallengeButton.setVisibility(View.INVISIBLE);
		HardChallengeButton.setOnTouchListener(this); HardChallengeButton.setVisibility(View.INVISIBLE);
		EasySpeedButton.setOnTouchListener(this);     EasySpeedButton.setVisibility(View.INVISIBLE);
		HardSpeedButton.setOnTouchListener(this);     HardSpeedButton.setVisibility(View.INVISIBLE);

		/*
		if (User.getInstance().isRegisteredUser())
			Toast.makeText(getApplicationContext(), "hello! " + User.getInstance().getUserName(), Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext(), "please subscribe to our newsletter", Toast.LENGTH_LONG).show();
			*/
    }


	@Override
	public void onResume()
	{
		super.onResume();
		if (root.getChildCount() >= 3) // remove the circular reveal image view
		{
			root.removeViewAt(2);
		}

		enableButtons(true);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		ChallengeButton.setVisibility(View.VISIBLE);
		ChallengeButton.setTextColor(ContextCompat.getColor(this, R.color.main_buttons_text));

		EasyChallengeButton.setVisibility(View.INVISIBLE);
		HardChallengeButton.setVisibility(View.INVISIBLE);

		SpeedChallengeButton.setVisibility(View.VISIBLE);
		SpeedChallengeButton.setTextColor(ContextCompat.getColor(this, R.color.main_buttons_text));
		EasySpeedButton.setVisibility(View.INVISIBLE);
		HardSpeedButton.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		if (gestureDetector.onTouchEvent(motionEvent)) {

			if (v == ChallengeButton) {
				animateButtonCollapse(ChallengeButton, EasyChallengeButton, HardChallengeButton);
			}

			else if (v == SpeedChallengeButton) {
				animateButtonCollapse(SpeedChallengeButton, EasySpeedButton, HardSpeedButton);
			}

			else if (v == HelpButton) {
				Intent i = new Intent(MainMenuActivity.this, ResultsActivity.class);
				GameMode.newInstance(Difficulty.HARD, GamePlayType.CHALLENGE);

				Bundle bundle = new Bundle();
				bundle.putInt("Score", 4400);
				bundle.putInt("CorrectAnswers", 10);
				bundle.putInt("TotalAnswers", 15);
				bundle.putInt("BonusScore", 500);
				bundle.putInt("CorrectBonusAnswers", 3);
				bundle.putInt("TotalBonusAnswers", 4);
				i.putExtra("ScoreModelBundle", bundle);

				MainMenuActivity.this.startActivity(i);
			}

			else if (v == HighScoreButton) {
				navigateToHighScore();
			}

			else
			{
				int touchX = (int) motionEvent.getRawX();
				int touchY = (int) motionEvent.getRawY() - getStatusBarHeight();

				if (v == EasyChallengeButton)
				{
					LaunchQuestionMode(Difficulty.EASY, GamePlayType.CHALLENGE, touchX, touchY);
				}
				else if (v == HardChallengeButton)
				{
					LaunchQuestionMode(Difficulty.HARD, GamePlayType.CHALLENGE, touchX, touchY);
				}
				else if (v == EasySpeedButton)
				{
					LaunchQuestionMode(Difficulty.EASY, GamePlayType.SPEED, touchX, touchY);
				}
				else if (v == HardSpeedButton)
				{
					LaunchQuestionMode(Difficulty.HARD, GamePlayType.SPEED, touchX, touchY);
				}
			}
		}
		return false;
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private void enableButtons(boolean enabled)
	{
		ChallengeButton.setEnabled(enabled);
		SpeedChallengeButton.setEnabled(enabled);

		EasyChallengeButton.setEnabled(enabled);
		HardChallengeButton.setEnabled(enabled);
		EasySpeedButton.setEnabled(enabled);
		HardSpeedButton.setEnabled(enabled);
	}

	public void LaunchQuestionMode(final Difficulty difficulty, final GamePlayType gamePlayType, int touchX, int touchY)
	{
		enableButtons(false);

		GameMode.newInstance(difficulty, gamePlayType);

		CircularTransition transition = new CircularTransition(this, root, touchX, touchY);
		transition.start(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				startActivity(IntentManager.getQuestionIntent(MainMenuActivity.this));
			}
		});
	}

	public void navigateToHighScore()
	{
		startActivity(IntentManager.getHighScoresIntent(this));
	}






	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus && !introAnimationsPlayed) {
			Measure.loadScreenDimensions(this);


			ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.main_company_name_enter);
			anim.setTarget(titleText);
			anim.setFloatValues(-500, titleText.getY());
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					titleText.setVisibility(View.VISIBLE);

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							titleSubText.setVisibility(View.VISIBLE);
							int centerX = (int) titleSubText.getX() + titleSubText.getWidth() / 2;
							int centerY = (int) titleSubText.getY() + titleSubText.getHeight() / 2;
							SupportAnimator revealSubText = ViewAnimationUtils.createCircularReveal(titleSubText, centerX, centerY, 0, titleSubText.getWidth());
							revealSubText.setInterpolator(new AccelerateDecelerateInterpolator());
							revealSubText.setDuration(1100);
							revealSubText.addListener(new SupportAnimator.AnimatorListener() {
								@Override
								public void onAnimationStart() {}
								@Override
								public void onAnimationEnd() {

									int screenHeight = MyApplication.screenHeight;

									ObjectAnimator classic = (ObjectAnimator) AnimatorInflater.loadAnimator(MainMenuActivity.this, R.animator.main_classic_button_enter);
									classic.setTarget(ClassicLayout);
									classic.setFloatValues(screenHeight - ClassicLayout.getY(), ClassicLayout.getY());
									classic.addListener(new AnimatorListenerAdapter() {
										@Override
										public void onAnimationStart(Animator animation) {
											ClassicLayout.setVisibility(View.VISIBLE);
										}
									});

									ObjectAnimator speed = (ObjectAnimator) AnimatorInflater.loadAnimator(MainMenuActivity.this, R.animator.main_speed_button_enter);
									speed.setTarget(SpeedLayout);
									speed.setFloatValues(screenHeight - SpeedLayout.getY(), SpeedLayout.getY());
									speed.addListener(new AnimatorListenerAdapter() {
										@Override
										public void onAnimationStart(Animator animation) {
											SpeedLayout.setVisibility(View.VISIBLE);
										}
									});

									AnimatorSet set = new AnimatorSet();
									set.playTogether(classic, speed);
									set.start();
								}
								@Override
								public void onAnimationCancel() {}
								@Override
								public void onAnimationRepeat() {}
							});
							revealSubText.start();
						}
					}, 200);
				}

				@Override
				public void onAnimationEnd(Animator animation) {}
			});
			anim.start();
			introAnimationsPlayed = true;
		}
	}

	private void animateButtonCollapse(final Button mainButton, final Button easyButton, final Button hardButton)
	{
		ObjectAnimator collapse = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.main_button_collapse);
		collapse.setTarget(mainButton);

		ObjectAnimator expand = (ObjectAnimator) AnimatorInflater.loadAnimator(MainMenuActivity.this, R.animator.main_button_expand);
		expand.setTarget(mainButton);

		AnimatorSet collapseButton = new AnimatorSet();
		collapseButton.playSequentially(collapse, expand);
		collapseButton.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator anion) {
				mainButton.setTextColor(ContextCompat.getColor(MainMenuActivity.this, android.R.color.transparent));
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mainButton.setVisibility(View.INVISIBLE);
				mainButton.setScaleX(1.0f);
				easyButton.setVisibility(View.VISIBLE);
				hardButton.setVisibility(View.VISIBLE);
			}
		});

		collapseButton.start();
	}
}