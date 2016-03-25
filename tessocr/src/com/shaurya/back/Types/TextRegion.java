package com.shaurya.back.Types;

public class TextRegion {
	int x1;
	int y1;
	int x2;
	int y2;
	double mass;
	
	public TextRegion(int xs, int ys, int xe, int ye, int maxx, int maxy, double m){
		if (xs < 0)
			x1 = 0;

		else if (xs > maxx)
			x1 = maxx;

		else 
			x1 = xs;

	     if (xe < 0)
	         x2 = 0;
	
	     else if (xe > maxx)
	         x2 = maxx;
	
	     else 
	    	 x2 = xe;
	
	     if (ys < 0)
	         y1 = 0;
	
	     else if (ys > maxy)
	         y1 = maxy;
	
	     else 
	    	 y1 = ys;
	
	     if (ye < 0)
	         y2 = 0;
	
	     else if (ye > maxy)
	         y2 = maxy;
	
	     else 
	    	 y2 = ye;
	     mass = m;
	
	 }

	public  int area() {
	    return width() * height();
	}
	
	 public int height() {
	     return y2 - y1;
	 }
	
	 public int width() {
	     return x2 - x1;
	 }
		
	 public double density() {
	     return mass / area();
	 }

	 public double aspect() {
	
	     return (double)height() / (double)width();
	
	 }

	 public int hstart()
	 {
		 return x1;
	 }
	 
	 public void sethstart(int a)
	 {
		 x1=a;
	 }
	 
	 public int hend()
	 {
		 return x2;
	 }
	 
	 public void sethend(int a)
	 {
		 x2=a;
	 }
	 
	 public int vstart()
	 {
		 return y1;
	 }
	 
	 public void setvstart(int a)
	 {
		 y1=a;
	 }
	 
	 public int vend()
	 {
		 return y2;
	 }

	 public void setvend(int a)
	 {
		 y2=a;
	 }

	 /**
	 * @param a
	 * @uml.property  name="mass"
	 */
	public void setMass(double a)
	 {
		 mass = a;
	 }
	 
	 /**
	 * @return
	 * @uml.property  name="mass"
	 */
	public double getMass()
	 {
		 return mass;
	 }
}