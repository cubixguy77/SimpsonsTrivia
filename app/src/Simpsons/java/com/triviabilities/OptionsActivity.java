package com.triviabilities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.triviabilities.enums.Difficulty;
import com.triviabilities.enums.GamePlayType;
import com.triviabilities.utils.Measure;
import com.triviabilities.views.CircularTransition;
import com.triviabilities.views.SingleTapGesture;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/* Home Screen */
public class OptionsActivity extends AppCompatActivity implements View.OnTouchListener
{
	private TextView titleText;
	private TextView titleSubText;

	private FrameLayout easyLayout;
	private ImageView easyImage;
	private FrameLayout easyTextContainer;
	private TextView easyText;

	private FrameLayout hardLayout;
	private ImageView hardImage;
	private FrameLayout hardTextContainer;
	private TextView hardText;

	private FrameLayout root;

	private boolean introAnimationsPlayed = false;

	private GestureDetector gestureDetector;

	private GamePlayType gamePlayType;

	private final int titleTextRevealDelay = 0;
	private int titleTextRevealDuration = 200;

	private int subTitleTextRevealDelay = 250;
	private int subTitleTextRevealDuration = titleTextRevealDuration;

	private int easyButtonRevealDelay = 0;//titleTextRevealDuration + subTitleTextRevealDelay + subTitleTextRevealDuration - 1900;

	private int buttonRevealBeatTime = 100;
	private int hardButtonRevealDelay = easyButtonRevealDelay + buttonRevealBeatTime;

	private int buttonCircleRevealDuration = 400;

	private int imageFadeInDuration = 300;

	private int textContainerRevealDuration = 100;
	private int textContainerRevealDelay = 50;

	private int imageFadeInDelay = textContainerRevealDuration + textContainerRevealDelay - 200;

	private int difficultyTextRevealDuration = imageFadeInDuration;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

		gestureDetector = new GestureDetector(this, new SingleTapGesture());

		root = (FrameLayout) findViewById(R.id.topRootFrame);

		titleText = (TextView) findViewById(R.id.TitleText);
		assert titleText != null;
		titleText.setAlpha(0f);

		titleSubText = (TextView) findViewById(R.id.chooseDifficultyText);
		assert titleSubText != null;
		titleSubText.setAlpha(0f);

		GamePlayType gamePlayType = (GamePlayType) getIntent().getBundleExtra("OptionsScreenBundle").getSerializable("GamePlayType");
		this.gamePlayType = gamePlayType;
		if (gamePlayType == GamePlayType.CHALLENGE) {
			titleText.setText(getResources().getString(R.string.options_title_text_classic));
		}
		else {
			titleText.setText(getResources().getString(R.string.options_title_text_speed));
		}

		easyLayout = (FrameLayout) findViewById(R.id.easyLayout);
		easyImage = (ImageView) findViewById(R.id.easyImage);
		easyTextContainer = (FrameLayout) findViewById(R.id.easyTextContainer);
		easyText = (TextView) findViewById(R.id.easyText);

		easyLayout.setVisibility(View.INVISIBLE);
		easyImage.setVisibility(View.INVISIBLE);
		easyTextContainer.setVisibility(View.INVISIBLE);
		//easyText.setVisibility(View.INVISIBLE);
		easyText.setAlpha(0f);
		easyLayout.setOnTouchListener(this);


		hardLayout = (FrameLayout) findViewById(R.id.hardLayout);
		hardImage = (ImageView) findViewById(R.id.hardImage);
		hardTextContainer = (FrameLayout) findViewById(R.id.hardTextContainer);
		hardText = (TextView) findViewById(R.id.hardText);

		hardLayout.setVisibility(View.INVISIBLE);
		hardImage.setVisibility(View.INVISIBLE);
		hardTextContainer.setVisibility(View.INVISIBLE);
		//hardText.setVisibility(View.INVISIBLE);
		hardText.setAlpha(0f);
		hardLayout.setOnTouchListener(this);
    }


	@Override
	public void onResume()
	{
		super.onResume();
		enableButtons(true);
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}
	@Override
	public void onStop()
	{
		super.onStop();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}



	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		if (gestureDetector.onTouchEvent(motionEvent)) {
			int touchX = (int) motionEvent.getRawX();
			int touchY = (int) motionEvent.getRawY() - getStatusBarHeight();
			if (v == easyLayout) {
				LaunchQuestionMode(Difficulty.EASY, gamePlayType, touchX, touchY);
			}
			else if (v == hardLayout) {
				LaunchQuestionMode(Difficulty.HARD, gamePlayType, touchX, touchY);
			}
		}
		return false;
	}



	public void LaunchQuestionMode(final Difficulty difficulty, final GamePlayType gamePlayType, int touchX, int touchY)
	{
		enableButtons(false);

		GameMode.newInstance(difficulty, gamePlayType);

		CircularTransition transition = new CircularTransition(this, root, touchX, touchY);
		transition.start(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				startActivity(IntentManager.getQuestionIntent(OptionsActivity.this));
			}
		});
	}



	/***************** Play Intro Animations *********************/
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus && !introAnimationsPlayed) {
			Measure.loadScreenDimensions(this);
			animateTitleTextReveal();
			introAnimationsPlayed = true;
		}
	}

	private ObjectAnimator getRevealTextAnimator(final View view, int duration, AnimatorListenerAdapter listener)
	{
		float endY = view.getY();
		float height = view.getHeight();
		final float startY = endY + height;
		ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", startY, endY);
		anim.setDuration(duration);
		if (listener != null)
			anim.addListener(listener);

		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				view.setVisibility(View.VISIBLE);
				view.setAlpha(1);
			}
		});
		return anim;
	}

	private void animateTitleTextReveal() {
		ObjectAnimator titleTextAnim = getRevealTextAnimator(titleText, titleTextRevealDuration, new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				final ObjectAnimator subTextAnim = getRevealTextAnimator(titleSubText, subTitleTextRevealDuration, new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						//onTitleTextRevealed();
					}
				});
				subTextAnim.setStartDelay(subTitleTextRevealDelay);
				subTextAnim.start();
			}});

		titleTextAnim.setStartDelay(titleTextRevealDelay);
		titleTextAnim.start();
		onTitleTextRevealed();
	}

	private void animateTitleTextReveal2() {
		final ObjectAnimator titleFadeIn = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.fade_in);
		titleFadeIn.setStartDelay(titleTextRevealDelay);
		titleFadeIn.setDuration(titleTextRevealDuration);
		titleFadeIn.setTarget(titleText);

		final ObjectAnimator subtitleFadeIn = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.fade_in);
		subtitleFadeIn.setStartDelay(subTitleTextRevealDelay);
		subtitleFadeIn.setDuration(subTitleTextRevealDuration);
		subtitleFadeIn.setTarget(titleSubText);

		AnimatorSet set = new AnimatorSet();
		set.playSequentially(titleFadeIn, subtitleFadeIn);
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				//onTitleTextRevealed();
			}
		});
		set.start();
		onTitleTextRevealed();
	}

	private void onTitleTextRevealed() {
		Handler handlerEasy = new Handler();
		handlerEasy.postDelayed(new Runnable() {
			public void run() {
				animateButtonReveal(true);
			}
		}, easyButtonRevealDelay);

		Handler handlerHard = new Handler();
		handlerHard.postDelayed(new Runnable() {
			public void run() {
				animateButtonReveal(false);
			}
		}, hardButtonRevealDelay);
	}

	private void animateButtonReveal(final boolean easy) {

		final FrameLayout layout = easy ? easyLayout : hardLayout;
		final ImageView image = easy ? easyImage : hardImage;
		final FrameLayout textContainer = easy ? easyTextContainer : hardTextContainer;
		final TextView text = easy ? easyText : hardText;

		enableButtons(false);

		final Animation fabReveal = AnimationUtils.loadAnimation(MyApplication.getAppContext(), R.anim.fab_show);
		fabReveal.setDuration(buttonCircleRevealDuration);
		fabReveal.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				layout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				final ObjectAnimator imageFadeIn = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.fade_in);
				imageFadeIn.setTarget(image);
				imageFadeIn.setDuration(imageFadeInDuration);
				imageFadeIn.setStartDelay(imageFadeInDelay);
				image.setAlpha(0.0f);
				image.setVisibility(View.VISIBLE);
				imageFadeIn.start();

				final int centerX = (int) textContainer.getX() + textContainer.getWidth() / 2;
				final int centerY = (int) textContainer.getY() + textContainer.getHeight() / 2;

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						SupportAnimator revealSubText = ViewAnimationUtils.createCircularReveal(textContainer, centerX, centerY, 0, textContainer.getWidth());
						revealSubText.setInterpolator(new AccelerateDecelerateInterpolator());
						revealSubText.setDuration(textContainerRevealDuration);
						revealSubText.addListener(new SupportAnimator.AnimatorListener() {
							@Override
							public void onAnimationStart() {
							}
							@Override
							public void onAnimationEnd() {


								final ObjectAnimator diffTextFadeIn = (ObjectAnimator) AnimatorInflater.loadAnimator(MyApplication.getAppContext(), R.animator.fade_in);
								diffTextFadeIn.setStartDelay(0);
								diffTextFadeIn.setDuration(difficultyTextRevealDuration);
								diffTextFadeIn.setTarget(text);
								diffTextFadeIn.addListener(new AnimatorListenerAdapter() {
									@Override
									public void onAnimationEnd(Animator animation) {
										enableButtons(true);
									}
								});
								diffTextFadeIn.start();


								/*
								final ObjectAnimator subTextAnim = getRevealTextAnimator(text, difficultyTextRevealDuration, new AnimatorListenerAdapter() {
									@Override
									public void onAnimationEnd(Animator animation) {
										enableButtons(true);
									}
								});
								subTextAnim.start();
								*/

							}
							@Override
							public void onAnimationCancel() {}
							@Override
							public void onAnimationRepeat() {}
						});

						textContainer.setVisibility(View.VISIBLE);
						revealSubText.start();
					}}, textContainerRevealDelay);

			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});

		layout.startAnimation(fabReveal);
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
		easyLayout.setEnabled(enabled);
		hardLayout.setEnabled(enabled);
	}
}