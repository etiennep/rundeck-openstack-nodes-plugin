package com.dtolabs.rundeck.plugin.resources.openstack;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.NovaAsyncApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import com.google.common.base.Predicate;
import org.jclouds.rest.RestContext;

import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;

public class OpenStackResourceModelSource implements ResourceModelSource 
{
    private String endpoint;    
    private String tenant;
    private String username;
    private String password;
    private String apiVersion;
    private boolean activeOnly = true;
    private int refreshInterval = -1;
    
    private OpenStackNodeMapper nodeMapper;
    private long lastRefresh = 0;    
    private ComputeService compute;
    private RestContext<NovaApi, NovaAsyncApi> nova;
    private Set<String> zones;
    private NodeSetImpl nodeSet;
    
    HashMap<String, Map<String,String>> imageMetaDataCache = new HashMap<String,Map<String,String>>();
    
    protected OpenStackResourceModelSource(Properties properties) {
        tenant = properties.getProperty(OpenStackResourceModelSourceFactory.TENANT);
        endpoint = properties.getProperty(OpenStackResourceModelSourceFactory.ENDPOINT);    
        username = properties.getProperty(OpenStackResourceModelSourceFactory.USERNAME);
        password = properties.getProperty(OpenStackResourceModelSourceFactory.API_KEY);
        apiVersion = properties.getProperty(OpenStackResourceModelSourceFactory.API_VERSION);
        if (properties.containsKey(OpenStackResourceModelSourceFactory.ACTIVE_ONLY)) {
            activeOnly = Boolean.parseBoolean(properties.getProperty(
                    OpenStackResourceModelSourceFactory.ACTIVE_ONLY));
        }      
           
        int refreshSecs = 30;
        final String refreshStr = properties.getProperty(OpenStackResourceModelSourceFactory.REFRESH_INTERVAL);
        if (null != refreshStr && !"".equals(refreshStr)) {
          try {
              refreshSecs = Integer.parseInt(refreshStr);
          } catch (NumberFormatException e) {
              
          }
        }
        refreshInterval = refreshSecs * 1000;
        nodeMapper = 
          new OpenStackNodeMapper(properties.getProperty(OpenStackResourceModelSourceFactory.SSH_USER), 
                                  Integer.parseInt(properties.getProperty(OpenStackResourceModelSourceFactory.SSH_PORT)));               
    }
    
   

    protected OpenStackResourceModelSource(String endpoint,
            String tenant, String username, String password, String apiVersion,
            boolean activeOnly, int refreshInterval, String sshUser, int sshPort)
    {
        super();
        this.endpoint = endpoint;
        this.tenant = tenant;
        this.username = username;
        this.password = password;
        this.apiVersion = apiVersion;
        this.activeOnly = activeOnly;
        this.refreshInterval = refreshInterval;
        nodeMapper = new OpenStackNodeMapper(sshUser, sshPort);         
    }



    public INodeSet getNodes() throws ResourceModelSourceException
    {
        init();
        if (!needsRefresh()) {
            return nodeSet;
        }
        
        lastRefresh = System.currentTimeMillis();        
        nodeSet = new NodeSetImpl();
            
        for (String zone: zones) {

            ServerApi serverApi = nova.getApi().getServerApiForZone(zone);
            ImageApi imageApi = nova.getApi().getImageApiForZone(zone);

            for (Server server: serverApi.listInDetail().concat()) {
                // Only return active servers.
                if (activeOnly && server.getStatus() != Server.Status.ACTIVE)  {
                    continue;
                }
                Map<String,String> imageMetaData;
                String imageId = server.getImage().getId();
                if (imageMetaDataCache.containsKey(imageId)) {
                    imageMetaData = imageMetaDataCache.get(imageId);
                } else {
                    imageMetaData = imageApi.getMetadata(imageId);
                    imageMetaDataCache.put(imageId, imageMetaData);
                }                   
                nodeSet.putNode(nodeMapper.toINodeEntry(zone, server, 
                        imageMetaData));               
            }            
        }
       
        close();
        return nodeSet;
    }

    private void init() {
        
                 
        ContextBuilder contextBuilder = ContextBuilder.newBuilder(new NovaApiMetadata());                
        String identity = (tenant != null ? tenant + ":" : "") + username;
        contextBuilder.credentials(identity, password);
        
        if (endpoint != null) 
            contextBuilder.endpoint(endpoint);
        if (apiVersion != null)
              contextBuilder.apiVersion(apiVersion);
              
        ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);
        compute = context.getComputeService();
        nova = context.unwrap();
        zones = nova.getApi().getConfiguredZones();
        
     }

     public void close() {
        compute.getContext().close();
     }
     
     /**
      * Returns true if the last refresh time was longer ago than the refresh interval
      */
     private boolean needsRefresh() {
         return refreshInterval < 0 || (System.currentTimeMillis() - lastRefresh > refreshInterval);
     }

     
     public static Predicate<ComputeMetadata> serverExists() {

         return new Predicate<ComputeMetadata>() {
            public boolean apply(ComputeMetadata metaData) {                
                return true;
            }
         };
      }
}
