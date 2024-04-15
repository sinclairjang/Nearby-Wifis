package com.test;

import com.algo.MinPQ;
import com.nw.GCS;

public class GCSTest {

	public static void main(String[] args) {
		MinPQ<GCS> mpq = new MinPQ<>(1000);
		
		GCS gcs2 = new GCS("1", -1.31861111111111, 1.38271211111);
		GCS gcs5 = new GCS("4", 37.421085, 127.02412);
		GCS gcs3 = new GCS("2", 37.42082, 127.02405);
		GCS gcs1 = new GCS("0", -157.2214497, 179.0550211);
		GCS gcs4 = new GCS("3", 37.42105, 127.02415);
		GCS.setPivot(37.2214497, 127.0550211);
		
		//int comp = gcs1.compareTo(gcs2);
		//System.out.println(comp);
		
		mpq.insert(gcs1);
		mpq.insert(gcs2);
		mpq.insert(gcs3);
		mpq.insert(gcs4);
		mpq.insert(gcs5);
		
		while(!mpq.isEmpty()) {
			GCS min = mpq.delMin();
			System.out.println(min);
			System.out.println(min.calcDistance());
		}
		
	}
}
