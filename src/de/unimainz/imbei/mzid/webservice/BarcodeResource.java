package de.unimainz.imbei.mzid.webservice;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import javax.ws.rs.core.Response.Status;

import de.unimainz.imbei.mzid.exceptions.BarcodeGenerationException;

/**
 * A resource to generate barcode images. To be addressed using functions.
 * 
 * @author Martin Lablans
 *
 */
public class BarcodeResource {
	
	@GET
	@Path("/{type}/{string}")
	public Response getBarcode(
			@PathParam("type") String type,
			@PathParam("string") String string,
			@DefaultValue("300") @QueryParam("res") int resolution
			){
		//1. Determine type
		AbstractBarcodeBean bean;
		type = type.toLowerCase();
		if(type.equals("datamatrix")){
			bean = new DataMatrixBean();
		} else if(type.equals("code128")){
			bean = new Code128Bean();
		} else {
			return Response
				.status(Status.BAD_REQUEST)
				.type(MediaType.TEXT_PLAIN)
				.entity("Barcode type \"" + type + "\" unsupported.")
				.build();
		}
		
		//2. Render
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
		//	bean.setModuleWidth(UnitConv.in2mm(1.0f / resolution)); //makes the narrow bar width exactly one pixel
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(
		            out, "image/x-png", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
			
			bean.generateBarcode(canvas, string);
			canvas.finish();
			out.close();
		} catch (Exception e) {
			throw new BarcodeGenerationException();
		}
		
		//3. Respond
		return Response
			.ok(out.toByteArray(), "image/png").build();
	}
	
	@GET
	public Response usage(){
		return Response
				.status(Status.BAD_REQUEST)
				.entity("Need to supply the barcode type and the string to be coded.")
				.build();
	}
}
