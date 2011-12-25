package areaCalc.prj;

import java.lang.Math;

public class calcModel {
	public static double calcArea(){
		la2xy();
		////known the position list,calculate the closed area
		/// approximate method: area equal sum of all triangles combined by three point in the list
		/// let start point (x0,y0) be the common point of all triangles
		/// area S = (1/2)sum(i){ x0yi + xiy(i+1) + x(i+1)y0 - x0y(i+1) - xiy0 - x(i+1)yi};
		double y0 = GlobalVar.get().latitude[0];
		double x0 = GlobalVar.get().longitude[0];
		
		double areaS = 0.0;
		
		for(int idx=1; idx<GlobalVar.get().nodeIdx-1; idx++){
			//
			double x1 = GlobalVar.get().latitude[idx];
			double y1 = GlobalVar.get().longitude[idx];
			
			double x2 = GlobalVar.get().latitude[idx+1];
			double y2 = GlobalVar.get().longitude[idx+1];
			
			areaS  = areaS + x0*y1 + x1*y2 + x2*y0 - x0*y2 - x1*y0 - x2*y1;
		}
		
		return areaS>0 ? areaS/2 : areaS/2*-1;
	}
	private static void la2xy(){
		//change the position from longitude&laltitue to x&y
		double y0 = GlobalVar.get().latitude[0];
		double x0 = GlobalVar.get().longitude[0];
		
		for(int idx=0; idx<GlobalVar.get().nodeIdx; idx++){
			_LA2XY(idx, x0, y0);
		}
	}
	
	private static void _LA2XY ( int idx, double lon_ori, double lat_ori) 
	{
		double sx, sy; 
		double roh = 45/Math.atan(1.000); 
		sy = 40000000/360; 
		sx = sy * Math.cos(lat_ori/roh); 
	 
		double lon = GlobalVar.get().longitude[idx];
		double lat = GlobalVar.get().latitude[idx];
		
		double change_x = (lon - lon_ori) * sx;
		double change_y = (lat - lat_ori) * sy; 
		
		GlobalVar.get().longitude[idx] = change_x;
		GlobalVar.get().latitude[idx] = change_y;
	} 
}
