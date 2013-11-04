package quique.proyecto.excercise;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SensorTestActivity extends Activity implements SensorEventListener {
	  private SensorManager sensorManager;
	  private boolean color = false;
	  private View view;
	  private long lastUpdate;
	  private int counter = 0;
	@Override
	public void onCreate(Bundle savedInstanceState){
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	        WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_shake);
	    view = findViewById(R.id.textView);
	    view.setBackgroundColor(Color.GREEN);

	    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    lastUpdate = System.currentTimeMillis();
			
	}
	
	  @Override
	  public void onSensorChanged(SensorEvent event) {
	    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	      getAccelerometer(event);
	    }

	  }
	  
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	  private void getAccelerometer(SensorEvent event) {
		    float[] values = event.values;
		    // Movement
		    float x = values[0];
		    float y = values[1];
		    float z = values[2];

		    float accelationSquareRoot = (x * x + y * y + z * z)
		        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		    long actualTime = System.currentTimeMillis();
		    if (accelationSquareRoot >= 4) //
		    {
		      if (actualTime - lastUpdate < 200) {
		        return;
		      }
              counter++;
		      lastUpdate = actualTime;
		      Toast.makeText(this, "Device had jumped", Toast.LENGTH_SHORT)
		          .show();
		      if(counter > 4){
		    	  if (color) {
		    		  view.setBackgroundColor(Color.GREEN);
		    	  } else {
		    		  view.setBackgroundColor(Color.RED);
		    	  }
		    	  color = !color;
		    	  counter = 0;
		      }
		    }
		  }
	  
	  @Override
	  protected void onResume() {
	    super.onResume();
	    // register this class as a listener for the orientation and
	    // accelerometer sensors
	    sensorManager.registerListener(this,
	        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	        SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	  protected void onPause() {
	    // unregister listener
	    super.onPause();
	    sensorManager.unregisterListener(this);
	  }

	
}
