package areaCalc.prj;

//singleton design pattern
public class GlobalVar {
	private GlobalVar(){
		//private constructor so can't be instanced;
	}
	public static GlobalVar get(){
		if(pGlobalVar == null)
			pGlobalVar = new GlobalVar();
		return pGlobalVar;
	}
	public static GlobalVar pGlobalVar = null;
	///global var
	
	//path
	private static int MAXNODE = 10000;		//max node recorded in the path
	public double longitude[] = new double[MAXNODE];		//position buffer
	public double latitude[] = new double[MAXNODE];
	public int nodeIdx = 0;
	//location mgr about
	public int minTime = 3;
	public int minDistance = 3;
	//loop control signal
	public Boolean isError = false;		//turn gps off while proccessing
	public Boolean recordStart = false;
	public Boolean recordOver = false;
	public Boolean noSatelliteSignal = false;
	//global msg
	public String msg;
	
	//
	public void node_append(double longi, double lati){		//append a pair of position
		
		if(MAXNODE == nodeIdx)
			node_arrange();		//if extends the buffer,then rearrange the buffer
		longitude[nodeIdx] = longi;
		latitude[nodeIdx++] = lati;
	}
	
	//
	private void node_arrange(){
		//rearrange the position buffer by desampling of 1/2
		for(int reIdx=0; reIdx<nodeIdx/2; reIdx++)
		{
			longitude[reIdx] = longitude[reIdx * 2];
			latitude[reIdx] = latitude[reIdx * 2];
		}
		nodeIdx /= 2;
		//minTime *= 2;
		//minDistance *= 2;
	}
	
}
