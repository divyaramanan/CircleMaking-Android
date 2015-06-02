package divya.example.com.appcircle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.sqrt;


public class CircleView extends View {
    float radius = 50.0f;
    float growingCircleRadius = radius;
    static int red=112,green=113,blue=255;
    Paint paint;
    PointF currentPoint ;
    private GestureDetectorCompat mDetector;
    String pointStatus;
    int down = 0;
    int canAddSize = 0;
    PointF findMovingPoint;
    float startX;
    float startY;
    boolean swipeInProgress = false;
    private List<PointF> points = new ArrayList<PointF>();
    ArrayList<Float> circleSize = new ArrayList<Float>(20);
    PointF p = new PointF();

    public CircleView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setARGB(255, red, green, blue);
        paint.setStyle(Paint.Style.FILL);
        setFocusable(true);
        mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());

    }



    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    //Draw Circle
    @Override
    public void onDraw(Canvas canvas) {

        paint.setARGB(255, red, green, blue);
        int i = 0;
        for(PointF p: points){
            if(p != currentPoint)
            {
                    if (i < circleSize.size()) {
                       canvas.drawCircle(p.x, p.y, circleSize.get(i), paint);
                     }
                     i++;
            }
            else
            {
                    canvas.drawCircle(p.x, p.y,growingCircleRadius, paint);
            }

        }
    }


    // Repeats the growth of circle on Long Press
    Handler handler = new Handler();
    boolean downPressed = false;
    Runnable repeater = new Runnable() {
        @Override public void run()
        {
            increaseSize();
            if (downPressed)
            { handler.postDelayed(this, 50);
            }
        }
    };

    // Repeats the Movement of circle on swipe
    Handler swipehandler = new Handler();
    Runnable swiperepeater = new Runnable() {
        @Override public void run()
        {
            movePoint();
            if (swipeInProgress)
            {
                swipehandler.postDelayed(this, 50);
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        this.mDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {

                case MotionEvent.ACTION_MOVE:
                startX = event.getX();
                startY = event.getY();
                break;

                case MotionEvent.ACTION_UP:
                if(canAddSize == 1)
                {
                    circleSize.add(growingCircleRadius);
                }
                swipeInProgress = false;
                downPressed=false;
                break;

                case MotionEvent.ACTION_CANCEL:
                break;

                case MotionEvent.ACTION_POINTER_UP:
                swipeInProgress = false;
                return false;


        }
        invalidate();
        return true;


    }

    // Start the process of moving the circle
    private boolean handleActionDown(MotionEvent event)
    {
        swipeInProgress = true;
        startX = findMovingPoint.x;
        startY = findMovingPoint.y;
        swipeInProgress = true;
        handler.post(swiperepeater);
        return true;
    }

   // Increase the Size of Circle
    void increaseSize()
    {
        growingCircleRadius = growingCircleRadius + 0.6f ;
        invalidate();
    }


   // Check if the points which is being touched has another existing circle. If yes, do not add the point.
    boolean checkPointPosition(float currentX,float currentY)
    {
        int i = 0;

        if(points.size() == 0)
        {
            return true;
        }
        else
        {
            for (PointF p : points)
            {
                pointStatus = inCircle(p, currentX, currentY, circleSize.get(i));
                if ((pointStatus == "inCircle") || (pointStatus == "onCircle"))
                {
                    findMovingPoint = p;
                    return false;
                }
                i++;
            }
        }

        return true;
    }


    // Change the position of points as they move
    void movePoint()
    {
        for (PointF p : points)
        {
            if (p == findMovingPoint)
            {
                p.x = startX;
                p.y = startY;
                invalidate();
                return;
            }
         }
     }



    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {
            canAddSize = 0;
            if(checkPointPosition(event.getX(),event.getY()))
            {
                growingCircleRadius = radius;
                PointF p = new PointF();
                p.x = event.getX();
                p.y = event.getY();
                points.add(p);
                currentPoint = p;
                down = 1;
                canAddSize = 1;

            }
            else
            {
                handleActionDown(event);
            }
            return true;
        }


        @Override
        public void onLongPress(MotionEvent event)
        {
            if(down == 1)
            {
                growingCircleRadius = radius;
                currentPoint = points.get(points.size() - 1);
                downPressed = true;
                handler.post(repeater);
            }
            down = 0;

         }

       }


    // Check if two circles overlap
    String inCircle(PointF p,float currentX,float currentY,float radius1)
    {

        float radius2 = radius;
        float dx = (p.x - currentX);
        float dy = (p.y - currentY);

        if( ((sqrt((dx * dx) + (dy * dy))) < (radius1+radius2)))
        {
            return "inCircle";
        }
        if( ((sqrt((dx * dx) + (dy * dy))) == 0))
        {
            return "onCircle";
        }
        if( ((sqrt((dx * dx) + (dy * dy))) > (radius1 + radius2)))
        {
            return "outCircle";
        }

        return "";

    }





}