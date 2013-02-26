package com.dtolabs.rundeck.plugin.resources.openstack;

import java.util.Properties;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.plugins.configuration.Describable;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyUtil;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;

@Plugin(name = "openstack", service = "ResourceModelSource")
public class OpenStackResourceModelSourceFactory implements ResourceModelSourceFactory, Describable
{    
    public static final String PROVIDER_NAME = "openstack";    
    public static final String ENDPOINT = "endpoint";    
    public static final String TENANT = "tenant";
    public static final String USERNAME = "username";
    public static final String API_KEY = "apiKey";
    public static final String API_VERSION = "apiVersion";
    public static final String REFRESH_INTERVAL = "refreshInterval";
    public static final String ACTIVE_ONLY = "activeOnly";
    public static final String SSH_USER = "sshUser";
    public static final String SSH_PORT = "sshPort";
    
    static Description DESC = DescriptionBuilder.builder()
            .name(PROVIDER_NAME)
            .title("OpenStack Resources")
            .description("Produces nodes from an OpenStack cloud environment")
            .property(PropertyUtil.string(USERNAME, "Username",
                                           "The username",
                                           true, null))
            .property(PropertyUtil.string(API_KEY, "API Key",
                                           "The cloud API key or password",
                                           true, null))
            .property(PropertyUtil.string(ENDPOINT, "Endpoint",
                                          "The cloud provider endpoint.",
                                          true, null))
            .property(PropertyUtil.string(API_VERSION, "API Version",
                                          "The cloud provider API Version.",
                                          false, "2"))                              
            .property(PropertyUtil.string(TENANT, "Tenant",
                                          "The cloud tenant (aka project). Optional",
                                          false, null))
            .property(PropertyUtil.integer(REFRESH_INTERVAL, "Refresh Interval",
                    "Minimum time in seconds between API requests (default is 30)", false, "30"))
            .property(PropertyUtil.bool(ACTIVE_ONLY, "Only Running Instances",
                      "Include active state instances only. If false, all instances will be returned.",
                      false, "true"))
            .property(PropertyUtil.string(SSH_USER, "SSH User",
                                          "The user name to connect to the node via SSH.",
                                          true, "root"))
            .property(PropertyUtil.integer(SSH_PORT, "SSH Port",
                                           "The port number used to connect to the node via SSH.",
                                           true, "22"))                             
            .build();
    

    public Description getDescription() {
        return DESC;
    }

    public OpenStackResourceModelSourceFactory() {

    }

    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException
    {        
        return new OpenStackResourceModelSource(configuration);
    }
    
}
