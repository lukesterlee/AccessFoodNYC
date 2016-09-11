package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import c4q.nyc.take2.accessfoodnyc.R;


/**
 * Created on 8/13/15.
 */
public class GifView extends View {

    private InputStream gifInputStream;
    private Movie gifMovie;
    private int movieWidth,movieHeight;
    private long movieDuration;
    private long movieStart;

    public GifView(Context context) {
        super(context);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        gifInputStream = context.getResources().openRawResource(R.drawable.hamburg1);

        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getMovieWidth(){
        return movieWidth;
    }

    public int getMovieHeight(){
        return movieHeight;
    }

    public long getMovieDuration(){
        return movieDuration;
    }

    protected void onDraw (Canvas canvas){
        long now = SystemClock.uptimeMillis();
        if (movieStart == 0 ){
            movieStart = now;
        }
        if (gifMovie != null){
            int dur = gifMovie.duration();
            if (dur == 0){
                dur =1000;
            }
            int realTime = (int) ((now - movieStart)% dur);
            gifMovie.setTime(realTime);
            gifMovie.draw(canvas,0,0);
            invalidate();
        }
    }

}
