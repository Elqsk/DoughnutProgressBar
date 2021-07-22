package com.example.doughnutprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class DoughnutProgressBar extends View {

    Context context;

    // Seek Bar를 움직이면 Main Activity에 구현해 놓은 Seek Bar 리스너를 통해 현재 게이지 값을 전달 받는다.
    // setValue(...) → invalidate() → onDraw(...) 순서를 거쳐서 게이지(빨강 도넛)를 다시 그린다.
    int value;

    // size는 init(...)에서 사각형(RectF)을 만들 때 사용한다.
    // 테두리 너비를 반영하지 않고 사각형을 (0, 0) 부터 그리면 도넛이 그만큼 잘려서 나온다.
    RectF rectF;
    int size;
    // onMeasure(...)를 구현해야 해서 쓰긴 하는데 커스텀 속성도 없기 때문에
    // 여기서는 size = width = height
    // 너비와 높이는 텍스트 그릴 정 가운데 좌표 값을 얻을 때 사용한다.
    int width;
    int height;

    int strokeWidth;

    int textSize;

    Paint paint;

    public DoughnutProgressBar(Context context) {
        super(context);

        Log.d("kkang", "Doughnut Progress Bar / Constructor 1");

        this.context = context;
        init();

        Log.d("kkang", " ");
    }

    public DoughnutProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Log.d("kkang", "Doughnut Progress Bar / Constructor 2");

        this.context = context;
        init();

        Log.d("kkang", " ");
    }

    public DoughnutProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Log.d("kkang", "Doughnut Progress Bar / Constructor 3");

        this.context = context;
        init();

        Log.d("kkang", " ");
    }

    private void init() {
        Log.d("kkang", "Doughnut Progress Bar / init() / Context context: " + context);

        size = getResources().getDimensionPixelSize(R.dimen.doughnut_progress_bar_size);
        strokeWidth = getResources().getDimensionPixelOffset(R.dimen.doughnut_progress_bar_stroke_width);
        textSize = getResources().getDimensionPixelSize(R.dimen.doughnut_progress_bar_text_size);

        Log.d("kkang", "Doughnut Progress Bar / init() / int size: " + size);
        Log.d("kkang", "Doughnut Progress Bar / init() / int strokeWidth: " + strokeWidth);
        Log.d("kkang", "Doughnut Progress Bar / init() / int textSize: " + textSize);

        // size는 사각형(RectF)을 만들 때 사용한다.
        // 테두리 너비를 반영하지 않고 사각형을 (0, 0) 부터 그리면 도넛이 그만큼 잘려서 나온다.
        // (20, 20) 부터 시작하는 이유는 테두리 픽셀 너비 39(약 40)을 감안했기 때문이다.
        rectF = new RectF(20, 20, size - 20, size - 20);

        paint = new Paint();

        Log.d("kkang", " ");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 레이아웃(xml)에서 뷰를 조정하면서 크기를 유동적으로 반영하고 싶다면--match_parent나
        // wrap_content 등--attr에 포함된 mode를 받아와서 처리해줘야 한다.
        // MeasureSpec.getMode(int measureSpec)
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        Log.d("kkang", "Doughnut Progress Bar / onMeasure(...) / int width: " + width);
        Log.d("kkang", "Doughnut Progress Bar / onMeasure(...) / int height: " + height);
        Log.d("kkang", " ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 회색 도넛
        paint.setColor(Color.LTGRAY);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        // 동(0도), 남(90도), 서(180도), 북(270도) 그리고 다시 동(360도)
        // "0도 부터 시작해서 360도 까지 원을 그려라"
        // 채우기는 비워두고 테두리만 색을 지정했기 때문에 회색 도넛이 그려진다.
        canvas.drawArc(rectF, 0, 360, false, paint);

        Log.d("kkang", "Doughnut Progress Bar / onDraw(...) / 회색 도넛");


        // 빨강 도넛(게이지)
        // 레이아웃(xml)에서 뷰를 조정하면서 색을 바꾸고 싶으면 커스텀 속성을 만들어야 한다.
        // 현재는 커스텀 속성이 없어 색이 고정된 하드 코딩 상태다.
        paint.setColor(Color.RED);
//        paint.setStrokeJoin(Paint.Join.ROUND);

        // startAngle에 270(북쪽)을 넣어도, 0도 기준 -90을 넣어도 동일하게 작동한다.
        canvas.drawArc(rectF, 270, value, false, paint);

        Log.d("kkang", "Doughnut Progress Bar / onDraw(...) / 빨강 도넛(게이지)");


        // 값 텍스트
        paint.setTextSize(textSize);
        paint.setStrokeWidth(5);
        String valueText = String.valueOf(value);
        int valueTextX = width / 2 - (int) (paint.measureText(valueText) / 2);
        // 알파벳 기준 소문자가 다른 알파벳 보다 아래로 내려온 만큼의 부분 즉, y의 꼬리를 descent라 하고
        // 그 윗부분을 ascent라고 하기 때문에, 이 둘을 합치면 텍스트의 총 높이 길이가 나온다.
        int valueTextY = (int) (height / 2 - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(valueText, valueTextX, valueTextY, paint);

        Log.d("kkang", "Doughnut Progress Bar / onDraw(...) / 값 텍스트");
        Log.d("kkang", " ");
    }

    // Seek Bar를 움직이면 Main Activity에 구현해 놓은 Seek Bar 리스너를 통해 현재 게이지 값을 전달 받는다.
    // setValue(...) → invalidate() → onDraw(...) 순서를 거쳐서 게이지(빨강 도넛)를 다시 그린다.
    public void setValue(int value) {
        this.value = value;
        invalidate();

        Log.d("kkang", "Doughnut Progress Bar / setValue(...) / value: " + value);
        Log.d("kkang", " ");
    }
}
