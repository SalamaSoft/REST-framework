package com.salama.util.test;

import com.salama.reflect.PreScanClassFinder;

public class ReflectTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreScanClassFinder classFinder = new PreScanClassFinder();
		classFinder.loadClassOfPackage("com.salama");

	}

}
