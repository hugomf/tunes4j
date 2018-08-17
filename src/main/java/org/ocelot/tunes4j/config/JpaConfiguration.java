package org.ocelot.tunes4j.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "org.ocelot.tunes4j.dao")
@EnableTransactionManagement
public class JpaConfiguration {
	
	@Bean
    public DataSource dataSource() {
		DriverManagerDataSource dm = new DriverManagerDataSource();
		dm.setDriverClassName("org.hsqldb.jdbcDriver");
		dm.setUrl("jdbc:hsqldb:file:db/tunes4j");
		return dm;
	}

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.HSQL);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
        emFactory.setJpaVendorAdapter(vendorAdapter);
        emFactory.setPackagesToScan("org.ocelot.tunes4j.dto");
        emFactory.setDataSource(dataSource());
        return emFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }	
	
}
