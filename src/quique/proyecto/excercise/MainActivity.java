package quique.proyecto.excercise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity 
implements TextToSpeech.OnInitListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener
{
    // Google Map-> Escuela de ingenieria electrica
	double latitude = 9.936961;
	double longitude = -84.044029;
	
	//Google Map Interface Variables
    LatLng upperLeft,bottomRight,upperRight,bottomLeft,marker1Position,marker2Position;
    Marker marker1,marker2;
    Polyline route;
    PolygonOptions polygonOptions;
    Polygon polygon;
    private ArrayList<LatLng> arrayPoints = null,RoutePoints = null;
    private GoogleMap googleMap;
    
    //Creation of Random points variables
    Random uRand,vRand;
    double randU,randV;
    
    //Creation of Story Objects
	private TextToSpeech myTTS;
    private List<StoryItem> story;
    private StoryItem actualStoryItem;
    private boolean notOnAction = true;
    
    //Location Update Objects
    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    Location PreviousLocation,InitialLocation;
    double distance, MinDistance = 0.0, DistanceChange;
    
    
    @Override
    protected void onStart(){
    	super.onStart();
    	if(!mLocationClient.isConnected()){
    		mLocationClient.connect();
    	}
    }
    
    @Override
    protected void onDestroy(){
    	if(myTTS != null){
    		myTTS.stop();
    		myTTS.shutdown();
    	}
    	if(mLocationClient.isConnected()){
    		mLocationClient.removeLocationUpdates(this);
    	}
    	mLocationClient.disconnect();
    	super.onDestroy();
    }
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//
		RoutePoints = new ArrayList<LatLng>();
		//Definition of Area Selection Button Callback
		Button button = (Button) findViewById(R.id.ArSel);
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
		
		//Definition of Ok Button CallBack
		button = (Button) findViewById(R.id.OKBut);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				/*Intent intent = new Intent(v.getContext(),SensorTestActivity.class);
				startActivity(intent);*/
				speakWords(story.get(0).Fail);
			}
		});
		
		//Map initialization
        arrayPoints = new ArrayList<LatLng>();
		uRand = new Random();
		vRand = new Random();
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //Story and Text to Speech Initialization
        myTTS = new TextToSpeech(this, this);
        AssetManager am = getAssets();
        StoryReader storyReader = null;
        try {
			storyReader = new StoryReader(am.open("History.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(storyReader != null){
        	try {
				story = storyReader.GetStory();
				actualStoryItem = story.get(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    
        //Localization Update Initialization
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1500);
        mLocationClient = new LocationClient(this,this,this);
        distance = 0.0;
        DistanceChange = 300;
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Checking for Google Play Services in the phone. If they are not installed, then the program won't work
	private boolean servicesConnected(){
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(ConnectionResult.SUCCESS == resultCode){
			Log.d("Location Updates", "Google Play services is available");
			return true;
		} else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to connect to Play Services", Toast.LENGTH_SHORT).show();
            return false;
		}
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

            PreviousLocation = location;
            
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
        Button but = (Button) findViewById(R.id.ArSel);
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
    
    public void onInit(int initStatus) {
        //check for successful instantiation
    	if (initStatus == TextToSpeech.SUCCESS) {
    		if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
    			myTTS.setLanguage(Locale.US);
    	}
    	else if (initStatus == TextToSpeech.ERROR) {
    		Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
    	}
    }

    private void speakWords(String speech) {
        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Connection to Location Client Failed", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(this, Double.toString(distance),
                Toast.LENGTH_SHORT).show();
		Pedometer(location);
		if(distance > MinDistance && notOnAction){
			notOnAction = false;
			ManageStoryAction(location);
		}
	}
	
	public void Pedometer(Location location){
		float []results = new float[3];
		Location.distanceBetween(PreviousLocation.getLatitude(), PreviousLocation.getLongitude(), 
				location.getLatitude(), location.getLongitude(), results);
		if(results[0] > 2.0f && results[0] < 50.0){
			RoutePoints.add(new LatLng(PreviousLocation.getLatitude(),PreviousLocation.getLongitude()));
			distance += results[0];
		}
		PreviousLocation = location;
	}
	
	public void ManageStoryAction(Location location){
		MinDistance+=DistanceChange;
		speakWords(actualStoryItem.Mensaje);
		Intent intent;
		switch(actualStoryItem.action.Tipo){
			case Jump:
				 intent = new Intent(this,SensorTestActivity.class);
				startActivityForResult(intent,0);
				break;
			case Photo:
				intent = new Intent(this,cameraActivity.class);
				startActivityForResult(intent,0);
				break;
			case North:
				intent = new Intent(this, MagneticActivity.class);
				startActivityForResult(intent,0);
				break;
			case Focus:
				intent = new Intent(this, Ball.class);
				startActivityForResult(intent,0);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			speakWords(actualStoryItem.Success);
			if(actualStoryItem.SuccDest != -1){
				actualStoryItem = story.get(actualStoryItem.SuccDest);
			} else {
				speakWords("Your Current Mission Has Ended");
			}
		} else {
			speakWords(actualStoryItem.Fail);
			if(actualStoryItem.SuccDest != -1){
				actualStoryItem = story.get(actualStoryItem.FailDest);
			} else {
				speakWords("Your Current Mission Has Ended");
			}
		}
		addRoute(RoutePoints);
		notOnAction = true;
		RoutePoints.clear();
	}
}
