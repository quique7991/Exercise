package quique.proyecto.excercise;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Google Map-> Escuela de ingenieria electrica
	double latitude = 9.936961;
	double longitude = -84.044029;
    LatLng upperLeft;
    LatLng bottomRight;
    LatLng upperRight;
    LatLng bottomLeft;
    LatLng marker1Position;
    LatLng marker2Position;
    Marker marker1;
    Marker marker2;
    PolygonOptions polygonOptions;
    private ArrayList<LatLng> arrayPoints = null;
    Polygon polygon;
    private GoogleMap googleMap;
    Random uRand,vRand;
    double randU,randV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        arrayPoints = new ArrayList<LatLng>();
		uRand = new Random();
		vRand = new Random();
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
                    R.id.map)).getMap();
            googleMap.setOnMapLongClickListener(new OnMapLongClickListener(){

				@Override
				public void onMapLongClick(LatLng arg0) {
					LatLng newPoint = randomDistance(1000,arg0);
					googleMap.addCircle(new CircleOptions()
				     .center(arg0)
				     .radius(1000)
				     .strokeColor(Color.RED)
				     .strokeWidth(7));
     				googleMap.addMarker(new MarkerOptions()
     				 	.position(newPoint)
				        .title("RandomPoint"));

				}
            	
            	
            });
            googleMap.setOnMarkerDragListener(new OnMarkerDragListener(){
            	@Override 
            	public void onMarkerDrag(Marker marker){
            		if(polygon != null){
            			polygon.remove();
            			polygon = null;
            		}
            		arrayPoints.clear();
            		polygonOptions = new PolygonOptions();
            		marker1Position = marker1.getPosition();
            		marker2Position = marker2.getPosition();
            		if((marker1Position != null)&&(marker2Position!=null)){
            			upperLeft = new LatLng(marker1Position.latitude,marker1Position.longitude);
            			arrayPoints.add(upperLeft);
            			bottomRight = new LatLng(marker2Position.latitude,marker1Position.longitude);
            			arrayPoints.add(bottomRight);
            			upperRight = new LatLng(marker2Position.latitude,marker2Position.longitude);
            			arrayPoints.add(upperRight);
            			bottomLeft = new LatLng(marker1Position.latitude,marker2Position.longitude);
            			arrayPoints.add(bottomLeft);
            			polygonOptions.addAll(arrayPoints);
            			polygonOptions.strokeColor(Color.BLUE);
            			polygonOptions.strokeWidth(7);
            			polygonOptions.fillColor(Color.TRANSPARENT);
            			polygon = googleMap.addPolygon(polygonOptions);
            		}
            	}

				@Override
				public void onMarkerDragEnd(Marker arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onMarkerDragStart(Marker arg0) {
					// TODO Auto-generated method stub
					
				}
            	
            });
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
            			// TODO Auto-generated method stub
					
            		}

            		@Override
            		public void onFinish() {		
                        LatLngBounds curScreen = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        upperLeft = new LatLng(curScreen.northeast.latitude,curScreen.southwest.longitude);
                        bottomRight = new LatLng(curScreen.southwest.latitude,curScreen.northeast.longitude);
                        upperRight = new LatLng(curScreen.northeast.latitude,curScreen.northeast.longitude);
                        bottomLeft = new LatLng(curScreen.southwest.latitude,curScreen.southwest.longitude);
                        
                        MarkerOptions markerOptions1 = new MarkerOptions().position(upperLeft).title("Limite 1").draggable(true).anchor(0.17f,0.163f);
                        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.upleft));
                        marker1 = googleMap.addMarker(markerOptions1);
                        MarkerOptions markerOptions2 = new MarkerOptions().position(bottomRight).title("Limite 2").draggable(true).anchor(0.83f,0.85f);
                        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.downright));
                        marker2 = googleMap.addMarker(markerOptions2);
                        polygon = googleMap.addPolygon(new PolygonOptions()
                        .add(upperLeft, upperRight, bottomRight, bottomLeft,upperLeft)
                        .strokeColor(Color.RED)	.strokeWidth(5));
            		}
            	
            	
            	});
            

            }
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
    
    //Distance in meters
    private LatLng randomDistance(double distance, LatLng initial){
    	Random n = new Random();
    	randU = n.nextDouble();
    	randV = n.nextDouble();
    	///Radius to degrees
    	double r = distance / 111300f;
    	double w = r;
    	double t = 2 * Math.PI * randV;
    	double x = w * Math.cos(t);
    	double y = w * Math.sin(t);
    	double x0 = initial.longitude;
    	double y0 = initial.latitude;
    	double xPrime = x/Math.cos(y0);
    	LatLng respuesta;
    	respuesta = new LatLng(y+y0,xPrime+x0);
    	return respuesta;   	
    }

}
