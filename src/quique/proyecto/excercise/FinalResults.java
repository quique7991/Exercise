package quique.proyecto.excercise;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class FinalResults extends Activity {

    Polyline route;
    double latitude,longitude;
    PolygonOptions polygonOptions;
    @SuppressWarnings("unused")
	private ArrayList<LatLng> arrayPoints = null;//TODO aca se almacenan las posiciones en la base de datos
    Polygon polygon;
    private GoogleMap googleMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);
		Button button = (Button) findViewById(R.id.button1Final);
		button.setClickable(false);
		Intent intent = getIntent();
		double distance = intent.getDoubleExtra("distance", 0.0);
		int minutes = intent.getIntExtra("minutes", 0);
		int seconds = intent.getIntExtra("seconds", 0);
		arrayPoints = new ArrayList<LatLng>();
		String []Latitudes = intent.getStringArrayExtra("Latitudes");
		String []Longitudes = intent.getStringArrayExtra("Longitudes");
		TextView distancetext = (TextView) findViewById(R.id.distancia);
		TextView timetext = (TextView) findViewById(R.id.tiempo);
		String distanceString = new DecimalFormat("#.##").format(distance).toString();
		distancetext.setText(distanceString);
		timetext.setText(Integer.toString(minutes)+":"+Integer.toString(seconds));
		double speed = distance/(seconds+minutes*60);
		String speedString = new DecimalFormat("#.##").format(speed).toString();
		TextView speedText = (TextView) findViewById(R.id.velocidad);
		speedText.setText(speedString);
		for(int i = 0; i<Latitudes.length;++i){
			arrayPoints.add(new LatLng(Double.parseDouble(Latitudes[i]),Double.parseDouble(Longitudes[i])));
		}
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
					///TODO Aca va el código para reiniciar el juego.
 				}
			
		});
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapFinal)).getMap();
   
            googleMap.setMyLocationEnabled(true);
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
           
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
            	// Getting latitude of the current location
            	latitude = location.getLatitude();

            	// Getting longitude of the current location
            	longitude = location.getLongitude();

            	// Creating a LatLng object for the current location

            	LatLng myPosition = new LatLng(latitude, longitude);

            	CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(16).build();
            
            	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new CancelableCallback(){

            		@Override
            		public void onCancel() {
               		}

            		@Override
            		public void onFinish() {		
            		}
            	
            	
            	});
            

            }else{
                List<String> providers = locationManager.getProviders(true);
                Location bestLocation = null;
                for (String providerX : providers) {
                        Location l = locationManager.getLastKnownLocation(providerX);
                        if (l == null) {
                                continue;
                        }
                        if (bestLocation == null
                                        || l.getAccuracy() < bestLocation.getAccuracy()) {
                                bestLocation = l;
                        }
                }
                if (bestLocation != null) {
                  	latitude = bestLocation.getLatitude();

                	// Getting longitude of the current location
                	longitude = bestLocation.getLongitude();

                	// Creating a LatLng object for the current location

                	LatLng myPosition = new LatLng(latitude, longitude);

                	CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(16).build();
                
                	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new CancelableCallback(){

                		@Override
                		public void onCancel() {
                   		}

                		@Override
                		public void onFinish() {		
                		}
                	
                	
                	});
                }
            	
            }
        }
        Button but = (Button) findViewById(R.id.button1Final);
        but.setClickable(true);
        List<LatLng> allValues = new ArrayList<LatLng>();
        addRoute(arrayPoints);
        //TODO aca va el codigo requerido para llamar a la funcion addRoute para mostrar la ruta hecha.
        /* TODO 
         * Para agregar las imagenes con un nuevo marker utilizar este código (equivalente al del MAIN)                 
         *              MarkerOptions markerOptions1 = new MarkerOptions().position(upperLeft).title("Limite 1").draggable(true).anchor(0.17f,0.163f);
                        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.upleft));
                        marker1 = googleMap.addMarker(markerOptions1);
         * */
        //TODO aca va el codigo requerido para mostrar los datos en @+id/tiempo, @+id/distancia, @+id/velocidad
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
    
    @SuppressWarnings("unused")
	private void addRoute(ArrayList<LatLng> points){
        if (route != null) {
            Log.i("removing line", "removed");
            route.remove();
            route = null;
        }	
    	PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
    	for(int i=0; i<points.size();i++){
    	    LatLng point = points.get(i);
    	    options.add(point);	
    	}
    	route = googleMap.addPolyline(options);
    }
}
