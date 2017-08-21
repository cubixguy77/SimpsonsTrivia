package com.triviabilities.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.triviabilities.R;
import com.triviabilities.utils.Utils;

public class DonutProgress extends View {
    private Paint finishedPaint;
    private Paint textPaint;
    private Paint glowPaint;
    private Paint transparentPaint;

    private ObjectAnimator glowAnim;

    private RectF finishedOuterRect = new RectF();

    /* Custom Design Attributes */
    private int textColor;
    private float finishedStrokeWidth;
    private int color_background;

    /* State properties */
    private boolean isGlowing = false;
    private int progress = 0;
    private float partialArcFillAnimProgress = 0f;
    private float partialGlowAnimProgress = 0f;
    private float partialFiveFillAnimProgress = 0f;
    private float partialStartUpAnimProgress = 0f;
    private float partialCollapseAnimProgress = 0f;
    private boolean visible = false;

    private final int color_unfinished = Color.rgb(100, 181, 246);
    private final int color_5 = Color.rgb(255, 202, 40);
    private final int color_4 = Color.rgb(255, 213, 79);
    private final int color_3 = Color.rgb(255, 224, 130);
    private final int color_2 = Color.rgb(255, 236, 179);
    private final int color_1 = Color.rgb(255, 248, 230);
    private final int color_glow = color_unfinished;

    private final int default_background_color = Color.rgb(33, 150, 243);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int min_size;
    private final float default_stroke_width;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_BACKGROUND_COLOR = "background_color";
    private static final String INSTANCE_IS_GLOWING = "is_glowing";
    private static final String INSTANCE_PARTIAL_ARC_FILL_ANIM_PROGRESS = "partial_arc_fill_anim_progress";
    private static final String INSTANCE_PARTIAL_GLOW_ANIM_PROGRESS = "partial_glow_anim_progress";
    private static final String INSTANCE_PARTIAL_FIVE_FILL_ANIM_PROGRESS = "partial_five_fill_anim_progress";
    private static final String INSTANCE_PARTIAL_START_UP_ANIM_PROGRESS = "partial_start_up_anim_progress";
    private static final String INSTANCE_PARTIAL_COLLAPSE_ANIM_PROGRESS = "partial_collapse_anim_progress";
    private static final String INSTANCE_IS_VISIBLE = "is_visible";

    public DonutProgress(Context context) {
        this(context, null);
    }
    public DonutProgress(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public DonutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        min_size = (int) Utils.dp2px(getResources(), 72);
        default_stroke_width = Utils.dp2px(getResources(), 10);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DonutProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initPainters() {
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(getTextColor());
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(getTextSize());
            textPaint.setAntiAlias(true);
        }

        textPaint.setTextSize(getHeight() / 3);

        if (finishedPaint == null) {
            finishedPaint = new Paint();
            finishedPaint.setStyle(Paint.Style.STROKE);
            finishedPaint.setAntiAlias(true);
            finishedPaint.setStrokeWidth(finishedStrokeWidth);
        }

        finishedOuterRect.set(finishedStrokeWidth, finishedStrokeWidth, getWidth() - finishedStrokeWidth, getHeight() - finishedStrokeWidth);

        if (glowPaint == null) {
            glowPaint = new Paint();
            glowPaint.setColor(color_glow);
            glowPaint.setStyle(Paint.Style.FILL);
        }

        if (transparentPaint == null) {
            transparentPaint = new Paint();
            transparentPaint.setColor(color_background);
            transparentPaint.setStyle(Paint.Style.FILL);
        }

        // kicking off this anim will result in periodic calls to setPartialGlowAnimProgress(newValue)
        if (glowAnim == null) {
            glowAnim = ObjectAnimator.ofFloat(this, "partialGlowAnimProgress", 0f, 100f);
            glowAnim.setDuration(1500);
            glowAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            glowAnim.setRepeatMode(ValueAnimator.RESTART);
            glowAnim.setRepeatCount(ObjectAnimator.INFINITE);
        }
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeWidth = attributes.getDimension(R.styleable.DonutProgress_donut_finished_stroke_width, default_stroke_width);
        textColor = attributes.getColor(R.styleable.DonutProgress_donut_text_color, default_text_color);
        color_background = attributes.getColor(R.styleable.DonutProgress_donut_background_color, default_background_color);
    }

    public float getFinishedStrokeWidth() {
        return finishedStrokeWidth;
    }

    public int getProgress() {
        return progress;
    }

    public float getTextSize() {
        return getHeight() / 3;
    }

    public int getTextColor() {
        return textColor;
    }

    public void reset() {
        partialArcFillAnimProgress = 0;
        partialGlowAnimProgress = 0;
        partialFiveFillAnimProgress = 0;
        partialStartUpAnimProgress = 0;
        partialCollapseAnimProgress = 0;

        progress = 1;
        visible = true;
    }

    public void startUp() {
        reset();

        ObjectAnimator startUpAnim = ObjectAnimator.ofFloat(this, "partialStartUpAnimProgress", 5f, 100f);
        startUpAnim.setDuration(1000);
        startUpAnim.setInterpolator(new OvershootInterpolator());
        startUpAnim.start();
    }

    public void collapse() {
        ObjectAnimator collapseAnim = ObjectAnimator.ofFloat(this, "partialCollapseAnimProgress", 0f, 100f);
        collapseAnim.setDuration(1700);
        collapseAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        collapseAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isGlowing = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                partialCollapseAnimProgress = 0;
                visible = false;
            }
        });
        collapseAnim.start();
    }

    public void setProgress(final int newProgress) {
        boolean increment = newProgress > this.progress;

        if (!increment) {
            progress--;
            this.isGlowing = false;
            partialFiveFillAnimProgress = 0;
        }

        ObjectAnimator arcFillAnim = ObjectAnimator.ofFloat(this, "partialArcFillAnimProgress", increment ? 0f : 100f, increment ? 100f : 0f);
        arcFillAnim.setDuration(700);
        arcFillAnim.setStartDelay(increment ? 600 : 0);
        arcFillAnim.setInterpolator(new DecelerateInterpolator());

        if (!increment) {
            arcFillAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (progress > 1)
                        setProgress(progress - 1);
                }
            });
        }
        else {
            arcFillAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progress = newProgress;
                    setPartialArcFillAnimProgress(0f);

                    if (newProgress == 5) {
                        ObjectAnimator fiveFillAnim = ObjectAnimator.ofFloat(DonutProgress.this, "partialFiveFillAnimProgress", 0f, 100f);
                        fiveFillAnim.setDuration(700);
                        fiveFillAnim.setInterpolator(new LinearInterpolator());
                        fiveFillAnim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                setGlow(true);
                            }
                        });
                        fiveFillAnim.start();
                    } else {
                        setGlow(false);
                    }

                    invalidate();
                }
            });
        }

        arcFillAnim.start();
    }

    public void setPartialArcFillAnimProgress(float partialArcFillAnimProgress) {
        this.partialArcFillAnimProgress = partialArcFillAnimProgress;
        invalidate();
    }

    public void setPartialFiveFillAnimProgress(float partialFiveFillAnimProgress) {
        this.partialFiveFillAnimProgress = partialFiveFillAnimProgress;
        invalidate();
    }

    public void setPartialStartUpAnimProgress(float partialStartUpAnimProgress) {
        this.partialStartUpAnimProgress = partialStartUpAnimProgress;
        invalidate();
    }

    public void setPartialCollapseAnimProgress(float partialCollapseAnimProgress) {
        this.partialCollapseAnimProgress = partialCollapseAnimProgress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!visible)
            return;

        if (this.isGlowing) {
            drawGlow(canvas);
        }

        if (partialCollapseAnimProgress == 0) {
            drawAllSectors(canvas);
        }

        if (this.partialFiveFillAnimProgress > 0) {
            drawGlobalArc(canvas, finishedOuterRect, this.partialFiveFillAnimProgress, finishedPaint);
        }

        if (this.progress > 0 && this.partialStartUpAnimProgress == 100 && partialCollapseAnimProgress == 0) {
            drawText(canvas, this.progress + "x");
        }
    }

    private void drawAllSectors(Canvas canvas) {
        drawArc(canvas, 1);
        drawArc(canvas, 2);
        drawArc(canvas, 3);
        drawArc(canvas, 4);
        drawArc(canvas, 5);
    }

    private void drawGlow(Canvas canvas) {
        float pad = finishedStrokeWidth/2;
        canvas.drawCircle(getWidth() / 2,getHeight() / 2, getWidth() / 2 - pad + (pad/100f * partialGlowAnimProgress), glowPaint); // outer glow
        canvas.drawCircle(getWidth() / 2,getHeight() / 2, getWidth() / 2 - finishedStrokeWidth, transparentPaint); // fills donut hole with background color
    }

    private void setPartialGlowAnimProgress(float partialGlowAnimProgress) {
        this.partialGlowAnimProgress = partialGlowAnimProgress;
        glowPaint.setAlpha((255 - (int) (partialGlowAnimProgress * 2.55f)));
        invalidate();
    }

    public boolean isGlowing() {
        return this.isGlowing;
    }

    public void setGlow(boolean isGlowing) {
        this.isGlowing = isGlowing;

        if (isGlowing) {
            glowAnim.start();
        }
        else {
            setPartialGlowAnimProgress(0);
            glowAnim.cancel();
            glowAnim.removeAllListeners();
        }
    }

    private void drawGlobalArc(Canvas canvas, RectF rect, float partialProgress, Paint paint) {
        paint.setColor(getColor(5));

        if (partialCollapseAnimProgress > 0 && partialCollapseAnimProgress < 50) {
            paint.setStrokeWidth(finishedStrokeWidth * (1 + (partialCollapseAnimProgress / 100)));
        }
        else if (partialCollapseAnimProgress >= 50) {
            paint.setStrokeWidth((2 - (2 * finishedStrokeWidth * partialCollapseAnimProgress / 100)));
            setScale(2 - (partialCollapseAnimProgress / 50));//      1 - (partialCollapseAnimProgress / 100));
        }

        canvas.drawArc(rect, 270, -360 * partialProgress / 100, false, paint);
    }

    private void setScale(float scale) {
        float size = (getWidth() - (2 * finishedStrokeWidth)) * scale;
        float margin = (getWidth() / 2) - (size / 2);
        finishedOuterRect.set(margin, margin, getWidth() - margin, getHeight() - margin);
    }

    private void drawArc(Canvas canvas, int sector) {
        if (partialStartUpAnimProgress > 0) {
            finishedPaint.setStrokeWidth(finishedStrokeWidth * (partialStartUpAnimProgress / 100));
            setScale(partialStartUpAnimProgress / 100);
        }

        if (this.progress < sector) {
            finishedPaint.setColor(color_unfinished);
            finishedPaint.setAlpha(255);
            canvas.drawArc(finishedOuterRect, 270 - (72 * (sector-1)), -72, false, finishedPaint);

            /* Draw Partially Filled Arc during animations */
            if (this.partialArcFillAnimProgress > 0 && this.progress + 1 == sector) {
                finishedPaint.setColor(getColor(sector));
                canvas.drawArc(finishedOuterRect, 270 - (72 * (sector-1)), -72*this.partialArcFillAnimProgress /100, false, finishedPaint);
            }
        }
        else {
            finishedPaint.setColor(getColor(sector));
            canvas.drawArc(finishedOuterRect, 270 - (72 * (sector - 1)), -72, false, finishedPaint);
        }
    }

    private void drawText(Canvas canvas, String text) {
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
        textPaint.setAlpha((int) (255f - (partialArcFillAnimProgress * 2.55f)));
        canvas.drawText(text, xPos, yPos, textPaint);
    }

    private int getColor(int sector) {
        switch (sector) {
            case 1: return color_1;
            case 2: return color_2;
            case 3: return color_3;
            case 4: return color_4;
            case 5: return color_5;
            default: return color_1;
        }
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_BACKGROUND_COLOR, color_background);
        bundle.putBoolean(INSTANCE_IS_GLOWING, isGlowing);
        bundle.putFloat(INSTANCE_PARTIAL_ARC_FILL_ANIM_PROGRESS, partialArcFillAnimProgress);
        bundle.putFloat(INSTANCE_PARTIAL_GLOW_ANIM_PROGRESS, partialGlowAnimProgress);
        bundle.putFloat(INSTANCE_PARTIAL_FIVE_FILL_ANIM_PROGRESS, partialFiveFillAnimProgress);
        bundle.putFloat(INSTANCE_PARTIAL_START_UP_ANIM_PROGRESS, partialStartUpAnimProgress);
        bundle.putFloat(INSTANCE_PARTIAL_COLLAPSE_ANIM_PROGRESS, partialCollapseAnimProgress);
        bundle.putBoolean(INSTANCE_IS_VISIBLE, visible);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH);
            color_background = bundle.getInt(INSTANCE_BACKGROUND_COLOR);
            isGlowing = bundle.getBoolean(INSTANCE_IS_GLOWING);
            visible = bundle.getBoolean(INSTANCE_IS_VISIBLE);

            partialArcFillAnimProgress = bundle.getFloat(INSTANCE_PARTIAL_ARC_FILL_ANIM_PROGRESS);
            partialGlowAnimProgress = bundle.getFloat(INSTANCE_PARTIAL_GLOW_ANIM_PROGRESS);
            partialFiveFillAnimProgress = bundle.getFloat(INSTANCE_PARTIAL_FIVE_FILL_ANIM_PROGRESS);
            partialStartUpAnimProgress = bundle.getFloat(INSTANCE_PARTIAL_START_UP_ANIM_PROGRESS);
            partialCollapseAnimProgress = bundle.getFloat(INSTANCE_PARTIAL_COLLAPSE_ANIM_PROGRESS);

            initPainters();
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}