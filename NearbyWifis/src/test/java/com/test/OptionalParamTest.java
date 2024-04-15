package com.test;

public class OptionalParamTest {

	public void test(Object... args) {
		for (Object arg : args) {
			if (arg == null) System.out.println("null");
			else if (arg instanceof String) System.out.println("String");
			else System.out.println(arg.toString());
		}
	}

	public static void main(String[] args) {
		OptionalParamTest opt = new OptionalParamTest();
		opt.test("test");
		
	}

}
