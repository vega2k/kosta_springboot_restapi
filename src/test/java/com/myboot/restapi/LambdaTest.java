package com.myboot.restapi;

import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

public class LambdaTest {
	
	@Test
	public void iterable() {
		
		List<String> myList = List.of("람다","함수형", "스트림");
		
		//1. Annoymous Inner class로 만들어서 재정의
		myList.forEach(new Consumer<String>() {
			@Override
			public void accept(String t) {
				System.out.println(t);				
			}
		});
		//2. Lambda expression 을 만들어서 재정의
		myList.forEach(value -> System.out.println(value + " => "));				
		
		//3. Method Reference 로 재정의
		myList.forEach(System.out::println);
	}

}
