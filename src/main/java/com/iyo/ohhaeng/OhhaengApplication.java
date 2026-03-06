package com.iyo.ohhaeng;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.iyo.ohhaeng.infra.db.mapper", annotationClass = Mapper.class)
public class OhhaengApplication {

	public static void main(String[] args) {
		SpringApplication.run(OhhaengApplication.class, args);
	}

}
