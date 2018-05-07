package org.ndexbio;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;

import ch.qos.logback.classic.Level;

import org.eclipse.jetty.util.log.Log;


public class App 
{
	

	 static final String APPLICATION_PATH = "/tempfile";
	 static final String CONTEXT_ROOT = "/";
	  
	 static long counter = 0;
	 
	 static String dataFilePathPrefix ;
	 
	 static String hostPrefix;
	 
	  public App() {}

	  public static void main( String[] args ) throws Exception
	  {
			
	    try
	    {
	      run();
	    }
	    catch (Throwable t)
	    {
	      t.printStackTrace();
	    }
	  }
	  
	  
	  static synchronized long getCounter() {
		  return counter ++;
	  }
	  
	  static String getDataFilePathPrefix() {
		  return dataFilePathPrefix;
	  }
	  
	  static String getHostPrefix () { return hostPrefix;}

	  public static void run() throws Exception
	  {
		System.out.println("You can use -Dhost.prefix=localhost:8286 -Dndex.tempfileport=8286 and -Dndex.tempfileRepo=/opt/ndex/tempfile/ to set runtime parameters.");
		ch.qos.logback.classic.Logger rootLog = 
        		(ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLog.setLevel(Level.INFO);
		
		//We are configuring a RolloverFileOutputStream with file name pattern  and appending property
		RolloverFileOutputStream os = new RolloverFileOutputStream("logs/queries_yyyy_mm_dd.log", true);
		
		//We are creating a print stream based on our RolloverFileOutputStream
		PrintStream logStream = new PrintStream(os);

		//We are redirecting system out and system error to our print stream.
		System.setOut(logStream);
		System.setErr(logStream);	  		
		
		
		String portStr = System.getProperty("ndex.tempfileport", "8286")  ;
		dataFilePathPrefix = System.getProperty("ndex.fileRepoPrefix", "/opt/ndex/tempfile/");
		FileUtils.cleanDirectory(new File(dataFilePathPrefix)); 
		hostPrefix = System.getProperty("host.prefix", "localhost:8286");
	    final int port = Integer.valueOf(portStr);
	    final Server server = new Server(port);
	    
	    rootLog.info("Server started on port " + portStr  + ", with network data repo at " + dataFilePathPrefix);

	    // Setup the basic Application "context" at "/".
	    // This is also known as the handler tree (in Jetty speak).
	    final ServletContextHandler context = new ServletContextHandler(
	      server, CONTEXT_ROOT);

	    // Setup RESTEasy's HttpServletDispatcher at "/api/*".
	    final ServletHolder restEasyServlet = new ServletHolder(
	      new HttpServletDispatcher());
	    restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix",
	      APPLICATION_PATH);
	    restEasyServlet.setInitParameter("javax.ws.rs.Application",
	      "org.ndexbio.TempFileServer");
	    context.addServlet(restEasyServlet, APPLICATION_PATH + "/*");

	    // Setup the DefaultServlet at "/".
	    final ServletHolder defaultServlet = new ServletHolder(
	      new DefaultServlet());
	    context.addServlet(defaultServlet, CONTEXT_ROOT);

	    server.start();
	  //Now we are appending a line to our log 
	  	Log.getRootLogger().info("Embedded Jetty logging started.", new Object[]{});
	    
	    System.out.println("Server started on port " + port + ", with network data repo at " + dataFilePathPrefix);
	    server.join();
	    
	  } 
}
