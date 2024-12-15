package com.cloud.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties
class UriConfiguration {
    //Comment for local testing
//    private String backend = "https://gateway-poc-axh2wfbs7q-ew.a.run.app";

    //Uncomment for local testing
    private String backend = "http://httpbin.org:80";

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

}
