package org.ndexbio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.log.Log;

@Path("/v1")
public class MessageResource {
	
	static private String filePostFix = "_tmp.cx";

  @SuppressWarnings("static-method")
  @GET
  @Path("/status")
  @Produces("application/json")
  public Map<String,String> printMessage() {
     Map<String,String> result = new HashMap<>();
     result.put("status", "online");
     return result;
  }
  
	@POST
	@Path("/")
	@Produces("text/plain")
	@Consumes(MediaType.APPLICATION_JSON)

    public static Response mytest (@Context HttpServletRequest request) throws IOException, URISyntaxException {
	   ServletInputStream in = request.getInputStream();
	   
	   long fileId =App.getCounter();
	   
	   //write content to file
	   String cxFilePath = App.getDataFilePathPrefix() + fileId + filePostFix;
	   
	   try (OutputStream outputStream = new FileOutputStream(cxFilePath)) {
		   IOUtils.copy(in, outputStream);
		   outputStream.close();
	   } 
	   
	   URI l = new URI (App.getHostPrefix() + fileId);

	   return Response.created(l).entity(l).build();	
	}
  

	
	@GET
	@Path("/{networkid}")

	public static Response getCompleteNetworkAsCX(	@PathParam("networkid") final long networkId)
			throws IllegalArgumentException {

		Log.getRootLogger().info("Getting file " + networkId);
    	
 		String cxFilePath = App.getDataFilePathPrefix() + networkId + filePostFix;

    	try {
			FileInputStream in = new FileInputStream(cxFilePath)  ;
		
			ResponseBuilder r = Response.ok();
			return 	r.type( MediaType.APPLICATION_JSON_TYPE).entity(in).build();
		} catch (FileNotFoundException e) {
			Log.getRootLogger().warn("Can't find file: {}]", e.getMessage());
			return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).build();
		}
		
	}  

  
}	