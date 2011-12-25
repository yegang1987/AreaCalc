package areaCalc.prj;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import areaCalc.prj.GlobalVar;
import areaCalc.prj.calcModel;

public class AreaCalcActivity extends Activity implements LocationListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //System.out.println("Hello,it is the project of AreaCalc.");
        Button btn_Turn = (Button) findViewById(R.id.Turn);
        btn_Turn.setOnClickListener(TurnBtn_listener);
        //
        
    }
    ///menu ID
    protected static final int MENU_QUIT = Menu.FIRST;
    protected static final int MENU_ABOUT = Menu.FIRST + 1;
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	    	
    	menu.add(0, MENU_QUIT, 0, "Quit");
    	menu.add(0, MENU_ABOUT, 0, "About...");
    	
    	return super.onCreateOptionsMenu(menu);
    	
    }
    public boolean onOptionsItemSelected(MenuItem item){
    	//
    	switch(item.getItemId()){
    	case MENU_ABOUT:
    		break;
    	case MENU_QUIT:
    		finishClear();		//quit the application
    		break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    private void finishClear(){
    	GlobalVar.get().nodeIdx = 0;
    	GlobalVar.get().isError = false;
    	GlobalVar.get().recordStart = false;
    	GlobalVar.get().recordOver = false;
    	GlobalVar.get().noSatelliteSignal = false;
    	GlobalVar.get().msg = "";
    	
    	//delete the location updating service
		mgr.removeUpdates(this);
		
		//quit the app
    	finish();
    }
    
    //implements 4 abstract methods
    public void onLocationChanged(Location location){
    	
    	if(GlobalVar.get().recordStart == false){
    		return;
    	}
    	if(GlobalVar.get().recordOver == true){
    		//delete the location updating service
    		mgr.removeUpdates(this);
    	}
    	if(location == null){
    		if( GlobalVar.get().noSatelliteSignal == false)
    			Toast.makeText(this, "no satellite signal!", Toast.LENGTH_LONG).show();
    		GlobalVar.get().noSatelliteSignal = true;
    		return;
    	}
    	else{
    		if(GlobalVar.get().noSatelliteSignal )
    			Toast.makeText(this, "has satellite signal again!", Toast.LENGTH_LONG).show();
    		GlobalVar.get().noSatelliteSignal = false;
    	}
    	
    	double longitude = location.getLongitude();
    	double latitude = location.getLatitude();
    	
    	GlobalVar.get().node_append(longitude, latitude);		//insert new position
    }
    
    public void onProviderDisabled(String provider){
    	
    	if(provider.compareTo("gps") == 0){
    		GlobalVar.get().isError = true;
    		Toast.makeText(this, "You can't turn off gps during the process!", Toast.LENGTH_LONG).show();
    	}
    }
    
    public void onProviderEnabled(String provider){
    	
    }
    
    public void onStatusChanged(String provider, int status, Bundle extras){
    	
    }
    //
    //android location manager
    private static LocationManager mgr;
    
    private void sysInit(){	
    	//init global var
    	GlobalVar.get();
    	//init location manager
    	mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
    	mgrUpdate();
    }
    //
    private void mgrUpdate(){
    	mgr.requestLocationUpdates("gps", GlobalVar.get().minTime, GlobalVar.get().minDistance, this);
    }
    
    ///GUI about
    //button listener
    private OnClickListener TurnBtn_listener = new OnClickListener(){
    	public void onClick(View v){
    		
    		if(GlobalVar.get().recordStart == false){	//begin record gps datas
    			GlobalVar.get().recordStart = true;
    			//
    			sysInit();
    			return;
    		}
    		
    		GlobalVar.get().recordOver = true;
    		//end record
    		//caculate the area from the data
    		calcModel.calcArea();
    		////////////////debug////////////////
    		TextView t_display = (TextView) findViewById(R.id.display);
    		//DecimalFormat nf = new DecimalFormat("0.00");
    		GlobalVar.get().msg = "\npath is: \n";
    		for(int idx=0; idx<GlobalVar.get().nodeIdx; idx++)
    			GlobalVar.get().msg = GlobalVar.get().msg +  GlobalVar.get().longitude[idx]
    					+ ',' + GlobalVar.get().latitude[idx] + '\n';
    		t_display.setText(GlobalVar.get().msg);
    			
    		/////////////////////////////////////
    	}
    };
}