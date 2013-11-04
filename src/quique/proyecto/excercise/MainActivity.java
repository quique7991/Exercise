package quique.proyecto.excercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
    Polyline route;
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
		Button button = (Button) findViewById(R.id.button1);
		button.setClickable(false);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				LatLng RecMark = CreatePointOnArea();
 				googleMap.addMarker(new MarkerOptions()
				.position(RecMark)
		        .title("RandomPoint"));			
 				}
			
		});
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(v.getContext(),SensorTestActivity.class);
				startActivity(intent);
			}
		});
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
					ArrayList<LatLng> points = new ArrayList<LatLng>();
					points.add(newPoint);
					LatLng location = new LatLng(latitude,longitude);
					points.add(location);
					points.add(upperRight);
					points.add(bottomLeft);
					points.add(upperLeft);
					points.add(bottomRight);
					addRoute(points);
					/*googleMap.addCircle(new CircleOptions()
				     .center(arg0)
				     .radius(1000)
				     .strokeColor(Color.RED)
				     .strokeWidth(7));
     				googleMap.addMarker(new MarkerOptions()
     				 	.position(newPoint)
				        .title("RandomPoint"));*/

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
        Button but = (Button) findViewById(R.id.button1);
        but.setClickable(true);
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
    	double x0 = initial.longitude;
    	double y0 = initial.latitude;
    	///Radius to degrees
    	double r = distance;
    	double w = r;
    	double t = 2 * Math.PI * randV;
    	double y = w * Math.sin(t)/110540f;
    	double x = w * Math.cos(t)/(111320f*Math.cos((y+y0)/180*Math.PI));
    	LatLng respuesta;
    	respuesta = new LatLng(y+y0,x+x0);
    	return respuesta;   	
    }
    
    private LatLng CreatePointOnArea(){
    	double MaxLatitude = upperLeft.latitude;
    	double MinLatitude = bottomRight.latitude;
    	double MaxLongitud = bottomRight.longitude;
    	double MinLongitud = upperLeft.longitude;
    	double LatitudeDiff = Math.abs(MaxLatitude-MinLatitude);
    	double LongitudeDiff = Math.abs(MaxLongitud - MinLongitud);
    	double LatRes = MinLatitude + (Math.random() * LatitudeDiff);
    	double LongRes = MinLongitud + (Math.random() * LongitudeDiff);
    	LatLng ReturnVal;
    	ReturnVal = new LatLng(LatRes,LongRes);
    	return ReturnVal;
    }

    private int addRoute(ArrayList<LatLng> points){
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
    	return 0;
    }
}
