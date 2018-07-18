package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

@Component
public class DataBaseConfig {

    @Bean
    public HibernateJpaVendorAdapter getDataSource() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setDatabase(Database.MYSQL);

        return adapter;
    }

    @Bean
    public DatabaseServiceCredentials getDatabaseServiceCredentials(@Value("${vcap.services}") String vcapServices){
        return new DatabaseServiceCredentials(vcapServices);
    }

}
