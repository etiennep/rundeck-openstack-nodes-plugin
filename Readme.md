Rundeck OpenStack Nodes Plugin
========================

This is a Resource Model Source plugin for [RunDeck][] 1.5+ that provides
OpenStack servers as nodes for the RunDeck server.

[RunDeck]: http://rundeck.org

Installation
------------

Put the `openstack-nodes-plugin-VERSION-bin.jar` into your `$RDECK_BASE/libext` dir.

Usage
-----

You can configure the Resource Model Sources for a project either via the
RunDeck GUI, under the "Admin" page, or you can modify the `project.properties`
file to configure the sources.

See: [Resource Model Source Configuration](http://rundeck.org/1.5/manual/plugins.html#resource-model-source-configuration)

The provider name is: `openstack`

Here are the configuration properties:

* `username`: the OpenStack username value.
* `apiKey`: the password.
* `apiVersion`: the cloud provider API Version.
* `endpoint`: the OpenStack endpoint 
* `refreshInterval`: Time in seconds used as minimum interval between calls to the OpenStack API. (default 30)
* `tenant`: The OpenStack tenant (aka project)
* `activeOnly`: if "true", automatically filter out the non-active instances.
* `sshUser`: the user name to connect to the node via SSH.
* `sshPort`: the user name to connect to the node via SSH.

