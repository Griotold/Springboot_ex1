package com.board.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration //스프링은 @Configuration이 지정된 클래스를 자바 기반의 설정 파일로 인식
//해당 클래스에서 참조할 properties 파일의 위치를 지정
@PropertySource("classpath:/application.properties") 

public class DBConfiguration {

	//Bean으로 등록된 인스턴스를 클래스에 주입하는 데 사용
	@Autowired
	//스프링 컨테이너 중 하나
	//스프링 컨테이너는 Bean의 생성과 사용, 관계, 생명 주기 등을 관리.
	private ApplicationContext applicationContext;

	@Bean //Configuration 클래스의 메소드 레벨에만 지정이 가능하다.
	//prefix로 시작하즌ㄴ 설정을 모두 읽어 들여 해당 메소드에 매핑(바인딩)
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() { //히카리CP 객체를 생성
		return new HikariConfig();
	}

	@Bean
	public DataSource dataSource() { //커넥션 풀을 지원하기 위한 인터페이스
		return new HikariDataSource(hikariConfig());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		//SqlSessionFactoryBean은 마이바티스와 스프링의 연동 모듈로 사용
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**/*Mapper.xml"));
		factoryBean.setTypeAliasesPackage("com.board.domain"); //xml파일에서 풀패키지명을 안쓰도록
		factoryBean.setConfiguration(mybatisConfg()); //아래 메소드 호출
		return factoryBean.getObject(); //SqlSessionFactory 생성
	}

	@Bean
	public SqlSessionTemplate sqlSession() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
		//SqlsessionTemplate은 SqlSessionFactory를 통해 생성되고
		//SQL의 실행에 필요한 모든 메서드를 갖는 객체
		//SQLSessionTemplate는 마이바티스 스프링 연동 모듈의 핵심.
		//SQLSessionTemplate는 SqlSession을 구현하고,
		//코드에서 SqlSession을 대체하는 역할을 한다.
	}
	//맨 위 어노테이션에서
	//지금 이 DBConfiguration.java파일은 @Configuration으로 환경설정이라고 선언했다.
	//@PropertySource으로 application.properties파일을 참고할거라고 선언했다.
	//아래 어노테이션 @ConfigurationProperties으로 "mybatis.configuration"가
	//으로 시작하는 설정들을 가지고 @Bean : 빈을 만들거다 라는 뜻이다. 
	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration")
	public org.apache.ibatis.session.Configuration mybatisConfg() {
		return new org.apache.ibatis.session.Configuration();
	}
}
