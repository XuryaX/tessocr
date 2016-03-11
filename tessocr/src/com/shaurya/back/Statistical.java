package com.shaurya.back;

import java.util.ArrayList;
import java.util.Arrays;

import com.shaurya.back.Types.RegionImage;

public class Statistical {

	
	/* To Get the Median */
	public static int median(Integer list[])
	{
		int median;
		Arrays.sort(list);
		median = list[list.length/2];
		return median;
	}
	
	public static int median(ArrayList<RegionImage> list)
	{
		ArrayList<Integer> reslist = RegionToInt(list);
		return(median(reslist.toArray(new Integer[reslist.size()])));
	}
	
	
	
	/* To get the Median Absolute Deviation */
	public static int mad(Integer list[])
	{
		int median;
		median = median(list);
		return(mad(list,median));
	}
	
	public static int mad(Integer list[], int median)
	{
		int mad;
		for(int i=0;i<list.length;i++)
		{
			list[i]=Math.abs(list[i]-median);
		}
		
		Arrays.sort(list);
		mad = list[list.length/2];
		return mad;
	}
	
	public static int mad(ArrayList<RegionImage> list)
	{
		ArrayList<Integer> reslist = RegionToInt(list);
		return(mad(reslist.toArray(new Integer[reslist.size()])));
	}
	
	public static int mad(ArrayList<RegionImage> list,int median)
	{
		
		ArrayList<Integer> reslist = RegionToInt(list);
		return(mad(reslist.toArray(new Integer[reslist.size()]),median));
	}
	
	
	/* To change a Region Image to an Integer ArrayList */
	private static ArrayList<Integer> RegionToInt(ArrayList<RegionImage> list)
	{
		ArrayList<Integer> intList = new ArrayList<Integer>(list.size());
		for(int i=0;i<list.size();i++)
		{
			intList.add(i, list.get(i).height());
		}
		return intList;
		
	}
}
