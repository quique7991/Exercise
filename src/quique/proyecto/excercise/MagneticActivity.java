package quique.proyecto.excercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
 
public class MagneticActivity extends Activity implements SensorEventListener {
 
  Float azimut;  // View to draw a compass
 
  public class CustomDrawableView extends View {
    Paint paint = new Paint();
    public CustomDrawableView(Context context) {
      super(context);
      paint.setColor(Color.rgb(0, 153, 204));
      paint.setStyle(Style.STROKE);
      paint.setStrokeWidth(10);
      paint.setAntiAlias(true);
    };
 
    protected void onDraw(Canvas canvas) {
      int width = getWidth();
      int height = getHeight();
      int centerx = width/2;
      int centery = height/2;
      canvas.drawLine(centerx, 0, centerx, height, paint);
      canvas.drawLine(0, centery, width, centery, paint);
      // Rotate the canvas with the azimut     
      if (azimut != null){
    	float value = -azimut*360/(2*3.14159f);
        canvas.rotate(value, centerx, centery);
        value = (value+360)%360;
        if((value<10) ||(value>350)){
        	///>>Aqui es donde detecta el norte
        	Toast.makeText(getApplicationContext(),
        			("NORTE"),
        			Toast.LENGTH_LONG).show(); 
      	  	Intent intent = new Intent();
      	  	setResult(Activity.RESULT_OK,intent);
      	  	finish();
        }
      }

      paint.setColor(Color.rgb(0,51,153));
      canvas.drawLine(centerx, -1000, centerx, +1000, paint);
      canvas.drawText("N", centerx+5, centery-10, paint);
      paint.setColor(Color.rgb(204, 255, 204));
    }
  }
 
  CustomDrawableView mCustomDrawableView;
  private SensorManager mSensorManager;
  Sensor accelerometer;
  Sensor magnetometer;
 
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCustomDrawableView = new CustomDrawableView(this);
    setContentView(mCustomDrawableView);    // Register the sensor listeners
    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
      accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    mCustomDrawableView.setBackgroundColor(Color.rgb(0, 153, 204));
  }
 
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
  }
 
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(this);
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
 
  float[] mGravity;
  float[] mGeomagnetic;
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
      mGravity = event.values;
    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
      mGeomagnetic = event.values;
    if (mGravity != null && mGeomagnetic != null) {
      float R[] = new float[9];
      float I[] = new float[9];
      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
      if (success) {
        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);
        azimut = orientation[0]; // orientation contains: azimut, pitch and roll
      }
    }
    mCustomDrawableView.invalidate();
  }
}