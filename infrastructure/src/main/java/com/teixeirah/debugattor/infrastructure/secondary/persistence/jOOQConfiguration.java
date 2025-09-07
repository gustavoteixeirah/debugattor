package com.teixeirah.debugattor.infrastructure.secondary.persistence;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class jOOQConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    DSLContext dsl() {
        return new DefaultDSLContext(dslConfig());
    }

    private org.jooq.Configuration dslConfig() {
        final var settings = new Settings()
                // render in lowercase (matches unquoted Postgres identifiers)
                .withRenderNameCase(RenderNameCase.LOWER)
                // never quote names unless you really need to
                .withRenderQuotedNames(RenderQuotedNames.NEVER);

        final var defaultConfiguration = new DefaultConfiguration();
        defaultConfiguration.set(dataSource)
                .set(SQLDialect.POSTGRES)
                .set(settings);
//                .set(DefaultExecuteListenerProvider.providers(new QueryRuntimeListener()))
        return defaultConfiguration;
    }

}
