package org.moo.bottle

/* Imports for logging */

import org.apache.log4j.*
import groovy.util.logging.*

/* Imports for JMX */

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import javax.naming.Context
import javax.management.ObjectName;

// j2ee stuff
import javax.management.*;
import javax.naming.*;

class CannotConnectX extends Exception { def CannotConnectX(message) { super(message) } }


@Log4j
class JMXgc
{
	
	def mbConn;			// mbean connection
	def mbServer;		// Catalina type=server mbean
	def mbWebModule;	// Catalina webmodule mbean
	def mbJRT;			// java.lang.Runtime mbean
	boolean isconnected;
	String jmxconn;
	def jmxConnector;
	def opts;
	
	def JMXgc()
	{
		def logcfgfile;
		logcfgfile=System.getProperty("loggingcfg");
		
		if (logcfgfile == null)
		{
			File cwd = new File(new File(".").getCanonicalPath())
			log.warn("loggingcfg system property not found! Setting to default: "+cwd+"/logging.cfg")
			logcfgfile = "logging.cfg"
		}
		Properties log4jprops = new Properties()
		File log4jpropsFile = new File (logcfgfile)

		log4jpropsFile.withInputStream {
			log4jprops.load(it)
		}
		
		PropertyConfigurator.configure(log4jprops);
		
		//this.ACNconnect();
	}
	
	def ACNconnect(String JMXconn)
	{
		this.jmxconn = JMXconn;
		
		log.trace("Entry")
		try
		{
			log.trace("Getting JMXConnection factory object.")
			log.debug("JMX Connection URL: ["+this.jmxconn+"]")
			log.trace("Connector options: "+this.opts)
			this.jmxConnector = JMXConnectorFactory.connect(\
				new JMXServiceURL(this.jmxconn), this.opts )
			log.trace("Getting MBeanServerConnection.")
			
			this.mbConn = this.jmxConnector.getMBeanServerConnection()
			log.trace("Getting GroovyMBean from JMX connection.")
					
		}
		catch (java.net.MalformedURLException e)
		{
			log.error("You do not have the weblogic jar files in the classpath.")
			log.debug(e.toString())
			throw new CannotConnectX("JMX Jar files not installed properly Check the classpath for the weblogic JMX jar files.")
		}
		catch (java.io.IOException e2)
		{
			log.error("Cannot connect to remote JMX server.  jmxconn string: ["+this.jmxconn+"]")
			log.error(e2.toString())
			throw new CannotConnectX("Admin Server unavailable. jmxconn: ["+this.jmxconn+"]")
		}
		
		this.isconnected=true
	}
	
	def ACNdisconnect()
	{
		try
		{
			this.jmxConnector.close()
		}
		catch (IOException e)
		{
			log.debug("Could not close connection: "+e.toString())
		}
		catch (NullPointerException e)
		{
			log.debug("Connection was null. "+e.toString())
			
		}
		finally
		{
			this.isconnected=false
		}
	}
}







