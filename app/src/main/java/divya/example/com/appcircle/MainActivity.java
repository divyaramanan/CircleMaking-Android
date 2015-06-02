package divya.example.com.appcircle;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;

public class MainActivity extends Activity  {
    private GestureDetector myGesture ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View shapes = new CircleView(this);
        setContentView(shapes);


    }
}

