package areaCalc.prj;

import java.text.DecimalFormat;
import java.util.Calendar;

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
        btn_Turn.setText("start");
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
    		Toast.makeText(this, "welcome to the app!", Toast.LENGTH_LONG).show();
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
    	if(mgr != null)
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
    	//show distance from the start point
    	TextView dis_show = (TextView) findViewById(R.id.distance);
		DecimalFormat nf = new DecimalFormat("0.00");
		
		int idx = GlobalVar.get().nodeIdx;
		dis_show.setText(nf.format( calcModel.distance(idx-1)) + 'm');
		//show position
		TextView pos_show = (TextView) findViewById(R.id.position);
		pos_show.setText("lon_" + longitude + ", " + "lat_" + latitude);
		/////show time duration
		int hour = GlobalVar.get().calendar.get(Calendar.HOUR_OF_DAY);
    	int minute = GlobalVar.get().calendar.get(Calendar.MINUTE);
    	int second = GlobalVar.get().calendar.get(Calendar.SECOND);
    	TextView time_show = (TextView) findViewById(R.id.time);
    	//calculate the time duration
    	int s_c = second - GlobalVar.get().start_s;
    	if(s_c < 0){
    		s_c = s_c + 60;
    		minute--;
    	}
    	int m_c = minute - GlobalVar.get().start_m;
    	if(m_c < 0){
    		m_c = m_c + 60;
    		hour--;
    	}
    	int h_c = hour - GlobalVar.get().start_h;
    	time_show.setText( h_c+"h"+m_c+"m"+s_c+"s" );
    	//////////////////////////////
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
    	//get start time
    	GlobalVar.get().start_h = GlobalVar.get().calendar.get(Calendar.HOUR_OF_DAY);
    	GlobalVar.get().start_m = GlobalVar.get().calendar.get(Calendar.MINUTE);
    	GlobalVar.get().start_s = GlobalVar.get().calendar.get(Calendar.SECOND);
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
    			////
    			Button btn_Turn = (Button) findViewById(R.id.Turn);
                btn_Turn.setText("end");
    			return;
    		}
    		
    		GlobalVar.get().recordOver = true;
    		//end record
    		//caculate the area from the data
    		double areaS = calcModel.calcArea();
    		////////////////debug////////////////
    		TextView t_display = (TextView) findViewById(R.id.display);
    		DecimalFormat nf = new DecimalFormat("0.00");
    		GlobalVar.get().msg = "area sum is:" + areaS + "M2\n";
    		GlobalVar.get().msg = GlobalVar.get().msg + "\npath is: \n";
    		for(int idx=0; idx<GlobalVar.get().nodeIdx; idx++)
    			GlobalVar.get().msg = GlobalVar.get().msg +  nf.format(GlobalVar.get().longitude[idx])
    					+ "," + nf.format(GlobalVar.get().latitude[idx]) + "    ";
    		t_display.setText(GlobalVar.get().msg);		
    		/////////////////////////////////////
    	}
    };
}