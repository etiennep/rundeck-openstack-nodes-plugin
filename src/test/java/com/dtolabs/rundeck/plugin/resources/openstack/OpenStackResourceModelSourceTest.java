package com.dtolabs.rundeck.plugin.resources.openstack;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.plugin.resources.openstack.OpenStackResourceModelSource;

public class OpenStackResourceModelSourceTest
{
    
    @Before
    public void setUp() throws Exception
    {
        
    }

    @Ignore
    @Test    
    public void testGetNodes() throws Exception
    {
      OpenStackResourceModelSource crms = new OpenStackResourceModelSource("https://identity.api.rackspacecloud.com/v2.0", null, "username", "password", "2",true, -1, "root", 22);
      INodeSet nodes = crms.getNodes();
      nodes.getNodeNames();
      assertThat(nodes, notNullValue());
      for (INodeEntry node : nodes) {
          System.out.println(node);
      }
    }
    
}
