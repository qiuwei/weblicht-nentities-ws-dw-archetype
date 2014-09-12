#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.resources;

import ${package}.core.NamedEntitiesTool;
import eu.clarin.weblicht.wlfxb.api.TextCorpusProcessor;
import eu.clarin.weblicht.wlfxb.api.TextCorpusProcessorException;
import eu.clarin.weblicht.wlfxb.io.TextCorpusStreamed;
import eu.clarin.weblicht.wlfxb.io.WLFormatException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("annotate")
public class NamedEntitiesResource {

    private static final String TEXT_TCF_XML = "text/tcf+xml";
    private static final String FALL_BACK_MESSAGE = "Data processing failed";
    private static final String TEMP_FILE_PREFIX = "references-output-temp";
    private static final String TEMP_FILE_SUFFIX = ".xml";

    private TextCorpusProcessor tool;
    public NamedEntitiesResource() {
        try {
            tool = new NamedEntitiesTool();

        } catch (TextCorpusProcessorException ex) {
            throw new WebApplicationException(createResponse(ex, Response.Status.INTERNAL_SERVER_ERROR));
        }
    }

    @Path("bytes")
    @POST
    @Consumes(TEXT_TCF_XML)
    @Produces(TEXT_TCF_XML)
    public Response processWithBytesArray(final InputStream input) {
        // prepare the storage for TCF output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // process incoming TCF and output resulting TCF with new annotation layer(s) added
        process(input, output, tool);
        // if no exceptions occur to this point, return OK status and TCF output
        // with the added annotation layer(s)
        return Response.ok(output.toByteArray()).build();
    }


    @Path("stream")
    @POST
    @Consumes(TEXT_TCF_XML)
    @Produces(TEXT_TCF_XML)
    public StreamingOutput processWithStreaming(final InputStream input) {

        // prepare temporary file and temprary output stream for writing TCF
        OutputStream tempOutputData = null;
        File tempOutputFile = null;
        try {
            tempOutputFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            tempOutputData = new BufferedOutputStream(new FileOutputStream(tempOutputFile));
        } catch (IOException ex) {
            if (tempOutputData != null) {
                try {
                    tempOutputData.close();
                } catch (IOException e) {
                    throw new WebApplicationException(createResponse(ex, Response.Status.INTERNAL_SERVER_ERROR));
                }
            }
            if (tempOutputFile != null) {
                tempOutputFile.delete();
            }
            throw new WebApplicationException(createResponse(ex, Response.Status.INTERNAL_SERVER_ERROR));
        }

        // process incoming TCF and output resulting TCF with new annotation layer(s) added
        process(input, tempOutputData, tool);

        // if there were no errors reading and writing TCF data, the resulting
        // TCF can be sent as StreamingOutput from the TCF output temporary file
        return new StreamingTempFileOutput(tempOutputFile);

    }



    private void process(final InputStream input, OutputStream output, TextCorpusProcessor tool) {

        TextCorpusStreamed textCorpus = null;
        try {
            // create TextCorpus object from the client request input,
            // only required annotation layers will be read into the object
            textCorpus = new TextCorpusStreamed(input, tool.getRequiredLayers(), output, false);
            // process TextCorpus and create new annotation layer(s) with your tool
            tool.process(textCorpus);
        } catch (TextCorpusProcessorException ex) {
            throw new WebApplicationException(createResponse(ex, Response.Status.INTERNAL_SERVER_ERROR));
        } catch (WLFormatException ex) {
            throw new WebApplicationException(createResponse(ex, Response.Status.BAD_REQUEST));
        } catch (Exception ex) {
            throw new WebApplicationException(createResponse(ex, Response.Status.INTERNAL_SERVER_ERROR));
        } finally {
            try {
                if (textCorpus != null) {
                    // it's important to close the TextCorpusStreamed, otherwise
                    // the TCF XML output will not be written to the end
                    textCorpus.close();
                }
            } catch (Exception ex) {
                throw new WebApplicationException(createResponse(ex, Response.Status.INTERNAL_SERVER_ERROR));
            }
        }


    }
    /* if exception message is provided, use it as it is;
     * if exception message is null, use fall back message
     * (needs to be non-empty String in order to prevent
     * HTTP container generated html message) */
    private Response createResponse(Exception ex, Response.Status status) {
        String message = ex.getMessage();
        if (message == null) {
            message = FALL_BACK_MESSAGE;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, message, ex);
        return Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
}
