package com.myboot.restapi.runner;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DBRunner implements ApplicationRunner {
	@Autowired
	private DataSource dataSource;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("DataSource 구현 클래스명 : " + dataSource.getClass().getName());
		//alt + shift + l(엘)
		Connection connection = dataSource.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		System.out.println("DB URL => " + metaData.getURL());
		System.out.println("DB Username => " + metaData.getUserName());
	}
}
