package quique.proyecto.excercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class Ball extends Activity implements SensorEventListener {
	  private SensorManager manager;
	  private BubbleView bubbleView;
	  private Sensor accel;
	  
	  public class BubbleView extends View {
		  private int diameter;
		  private int x;
		  private int y;
		  private int x1;
		  private int y1;
		  private ShapeDrawable bubble, bubble2;
		  private int width; private int height;
		  public BubbleView(Context context,int xPoint, int yPoint) {
		    super(context);
		    createBubble(xPoint, yPoint);///x1,y1 son la posición del punto que se tiene que buscar con la otra pelota.
		  }
		  
		  protected void move(float f, float g) {
			  x = (int) (x + f);
			  y = (int) (y + g);
			  bubble.setBounds(x, y, x + diameter, y + diameter);
			  if((x==x1)||(y==y1)){
				  ///Aqui es donde se verifica que la pelota esta encima
		        	Toast.makeText(getApplicationContext(),
		        			("Got IT"),
		        			Toast.LENGTH_LONG).show(); 
			  }
		  }
		  
		  @SuppressLint("NewApi")
		private void createBubble(int xPoint, int yPoint) {
			  x1=xPoint;
			  y1=yPoint;
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Point size = new Point();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				    display.getSize(size);
				    width = (size.x)/2;                 
				    height = (size.y)/2;
				}else{
				    width = getWidth()/2;
				    height = getHeight()/2;    			    	
				}  
				x = width;
				y = height;
				diameter = 100;
				bubble = new ShapeDrawable(new OvalShape());
				bubble2 = new ShapeDrawable(new OvalShape());
				bubble.setBounds(x, y, x + diameter, y + diameter);
				bubble2.setBounds(x1,y1,x1 + diameter, y1 + diameter);
				bubble.getPaint().setColor(0xff74AC23);
				bubble2.getPaint().setColor(Color.RED);
		  }
		  protected void onDraw(Canvas canvas) {
		    super.onDraw(canvas);
			bubble.draw(canvas);
			bubble2.draw(canvas);
		  }
		}
	  
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    bubbleView = new BubbleView(this,400,400);///Aqui se define el punto aleatorio
	    setContentView(bubbleView);
	    manager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    accel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    manager.registerListener(this, accel, 
	        SensorManager.SENSOR_DELAY_GAME);
	  }
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // don't do anything; we don't care
	  }
	  public void onSensorChanged(SensorEvent event) {
	    bubbleView.move(event.values[0]*2, event.values[1]*2);
	    bubbleView.invalidate();
	  }
	  protected void onResume() {
	    super.onResume();
	    manager.registerListener(this, accel, 
	          SensorManager.SENSOR_DELAY_GAME);
	  }
	  protected void onPause() {
	    super.onPause();
	    manager.unregisterListener(this);
	  }
	}