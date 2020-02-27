package com.trade.utils.easytrade.config;

import com.trade.utils.easytrade.rest.TransactionResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Named;

/**
 * jersey configuration
 */
@Named
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MultiPartFeature.class);
        register(TransactionResource.class);
    }
}
