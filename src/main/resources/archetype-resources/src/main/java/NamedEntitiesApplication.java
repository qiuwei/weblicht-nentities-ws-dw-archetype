#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import ${package}.resources.IndexResource;
import ${package}.resources.NamedEntitiesResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NamedEntitiesApplication extends Application<NamedEntitesConfiguration> {
    public static void main(String[] args) throws Exception{
        new NamedEntitiesApplication().run(args);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void initialize(Bootstrap<NamedEntitesConfiguration> bootstrap) {
    }

    @Override
    public void run(NamedEntitesConfiguration configuration, Environment environment) throws Exception {
        NamedEntitiesResource namedEntitiesResource = new NamedEntitiesResource();
        IndexResource indexResource = new IndexResource();
        environment.jersey().register(namedEntitiesResource);
        environment.jersey().register(indexResource);
    }
}
