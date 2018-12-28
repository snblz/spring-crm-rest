package by.koles.springrest.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("by.koles.springrest")
@PropertySource("classpath:persistence-postgresql.properties")
public class AppConfig implements WebMvcConfigurer{
	
	@Autowired
	private Environment env;
	
	@Bean
	public DataSource myDataSource() {		
		//create connection pool
		ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
		
		//set the jdbc driver class
		try {
			pooledDataSource.setDriverClass(env.getProperty("jdbc.driver"));
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
		
		//set database connection properties
		pooledDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
		pooledDataSource.setUser(env.getProperty("jdbc.user"));
		pooledDataSource.setPassword(env.getProperty("jdbc.password"));
		
		//set connection pool properties
		pooledDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		pooledDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		pooledDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));
		pooledDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));
		
		return pooledDataSource;
	}
	
	private Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		return properties;
	} 
	
	private int getIntProperty(String propName) {
		String propVal = env.getProperty(propName);
		int intPropVal = Integer.parseInt(propVal);
		return intPropVal;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		
		// create session factorys
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		// set the properties
		sessionFactory.setDataSource(myDataSource());
		sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
		sessionFactory.setHibernateProperties(getHibernateProperties());
		
		return sessionFactory;
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		
		// setup transaction manager based on session factory
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);

		return txManager;
	}	 
	
}
