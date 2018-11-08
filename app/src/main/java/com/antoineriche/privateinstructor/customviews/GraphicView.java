package com.antoineriche.privateinstructor.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;

import java.util.Arrays;
import java.util.Locale;

public class GraphicView extends LinearLayout {

    private int nbLegends = 11;
    private int minItemCount = 5;

    String TAG = GraphicView.class.getSimpleName();

    private Float[] mValues;
    private Paint mPaint;
    private RectF mGraphRect, mStepRow;


    public GraphicView(Context context) {
        this(context, null);
    }

    public GraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.custom_view_graphic_view, this);
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mGraphRect = new RectF();
        mStepRow = new RectF();
        mValues = new Float[5];

        ((LinearLayout) findViewById(R.id.ll_graphic_y_axe)).setWeightSum(nbLegends);
        LinearLayout.LayoutParams lLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        int textGravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

        TextView tv;
        for(int i = 1 ; i <= nbLegends ; i++){
            tv = new TextView(getContext());
            tv.setText(String.valueOf(2*(nbLegends - i)));
            tv.setLayoutParams(lLP);
            tv.setGravity(textGravity);
            ((LinearLayout) findViewById(R.id.ll_graphic_y_axe)).addView(tv);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        findViewById(R.id.ll_graphic_y_axe).setBackgroundColor(getContext().getColor(R.color.white));

        mGraphRect.set(
                findViewById(R.id.ll_graphic_content).getLeft(),
                findViewById(R.id.tv_graphic_title).getBottom(),
                findViewById(R.id.ll_graphic_content).getRight(),
                findViewById(R.id.tv_graphic_legend).getTop());

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getContext().getColor(R.color.grey200));
        canvas.drawRect(mGraphRect, mPaint);
        mPaint.setColor(getContext().getColor(R.color.black));
        createRectangularBox(canvas, mPaint, mGraphRect, 2);


        float heightOfStep = (mGraphRect.bottom - mGraphRect.top) / (float) nbLegends;
        float widthItem = (mGraphRect.right - mGraphRect.left) / (float) mValues.length;

        mPaint.setColor(getContext().getColor(R.color.themPrimaryDark));
        float xPos = mGraphRect.left;

        for(Float mark : mValues){
            float height = (mark/2) * heightOfStep;
            float top = mGraphRect.bottom - height;

            mStepRow.set(xPos+1, top, xPos + widthItem - 1, mGraphRect.bottom);
            canvas.drawRect(mStepRow, mPaint);

            xPos += widthItem;
        }

        float yPos = mGraphRect.top + heightOfStep;
        mPaint.setColor(getContext().getColor(R.color.black));
        mPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < nbLegends; i++) {
            int thickness = i != 5 ? 1 : 2;
            canvas.drawRect(mGraphRect.left, yPos - thickness, mGraphRect.right, yPos + thickness, mPaint);
            yPos += heightOfStep;
        }
    }

    private void createRectangularBox(Canvas canvas, Paint paint, float top, float left, float right, float bottom, float thickness){
        //Left border
        canvas.drawRect(left, top, left + thickness, bottom, paint);
        //Right border
        canvas.drawRect(right - thickness, top, right, bottom, paint);
        //Top border
        canvas.drawRect(left, top, right, top + thickness, paint);
        //Bottom border
        canvas.drawRect(left, bottom - thickness, right, bottom, paint);
    }

    private void createRectangularBox(Canvas canvas, Paint paint, RectF rectF, float thickness){
        createRectangularBox(canvas, paint, rectF.top, rectF.left, rectF.right, rectF.bottom, thickness);
    }

    public void setTitle(String pTitle){
        ((TextView) findViewById(R.id.tv_graphic_title)).setText(pTitle);
    }

    public void setExtraData(String pExtra){
        ((TextView) findViewById(R.id.tv_graphic_extras)).setText(pExtra);
    }

    public void setValues(Float[] values){
        setValues(values, false);
    }

    public void setValues(Float[] values, boolean displayMean){
        double mean;
        if(values.length > 0) {
            mean = Arrays.stream(values).mapToDouble(Float::doubleValue).average().getAsDouble();

            if (values.length < minItemCount) {
                Float[] result = new Float[minItemCount];
                System.arraycopy(values, 0, result, 0, values.length);
                for (int i = values.length; i < minItemCount; i++) {
                    result[i] = 0f;
                }
                mValues = result;
            } else {
                mValues = values;
            }
        } else {
            mean = 0;
            mValues = new Float[]{0f, 0f, 0f, 0f, 0f};
        }

        if(displayMean){
            setExtraData(String.format(Locale.FRANCE, "Moyenne : %.2f", mean));
        }

        invalidate();
    }
}
