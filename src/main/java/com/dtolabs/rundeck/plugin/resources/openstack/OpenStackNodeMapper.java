package com.dtolabs.rundeck.plugin.resources.openstack;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.google.common.collect.Multimap;

public class OpenStackNodeMapper
{
    
    private String sshUser;
    private int sshPort;
    
    protected OpenStackNodeMapper(String sshUser, int sshPort)
    {
        super();
        this.sshUser = sshUser;
        this.sshPort = sshPort;
    }
   
    protected INodeEntry toINodeEntry(String zone, Server server, Map<String,String> imageMetaData) {
        String hostname = getHostname(server);
        if (hostname == null) {
            return null;
        }
        hostname += ":" + sshPort;
        NodeEntryImpl node = new NodeEntryImpl(hostname, server.getName());
        node.setUsername(sshUser);
        node.setDescription(server.getName());
        String arch = imageMetaData.get("arch");
        if (arch != null)
            node.setOsArch(arch.replace('-', '_'));
        node.setOsFamily(imageMetaData.get("os_type"));
        node.setOsName(imageMetaData.get("os_distro"));
        node.setOsVersion(imageMetaData.get("os_version"));
        node.setAttribute("zone", zone);
        node.setAttribute("server_id", server.getId());
        node.setAttribute("image_id", server.getImage().getId());
        node.setAttribute("image_name", server.getImage().getName());
        for (Entry<String , String> meta : server.getMetadata().entrySet()) {
            node.setAttribute(meta.getKey(), meta.getValue());
        }
        return  node;
    }
    
    protected String getHostname(Server server) {
        if (server.getAccessIPv4() != null) {
            return server.getAccessIPv4() + ":" + sshPort; 
        }
        if (server.getAddresses() == null || server.getAddresses().isEmpty()) {
            return null;
        }
        Multimap<String, Address> addresses = server.getAddresses();
        if (addresses.containsKey("public")) {
            for (Address addr : addresses.get("public")) {
                if (addr.getVersion() == 4) {
                    return addr.getAddr();
                }
            }
        }
        if (addresses.containsKey("private")) {
            for (Address addr : addresses.get("private")) {
                if (addr.getVersion() == 4) {
                    return addr.getAddr();
                }
            }
        }
        return null;
    }

}
