package com.nirjavafunc.functions;

import java.util.*;
import java.net.URI;
import com.microsoft.azure.serverless.functions.annotation.*;
import com.microsoft.azure.serverless.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    @FunctionName("hello")
    public HttpResponseMessage<String> hello(
            @HttpTrigger(name = "req", methods = {"get", "post"}, authLevel = AuthorizationLevel.ANONYMOUS, route="hello/{name}") HttpRequestMessage<Optional<String>> request,
            @QueueOutput(queueName = "javaout", connection = "AzureWebJobsStorage", name = "queue") OutputBinding<String> queue, 
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        URI ur = request.getUri();
        String pth = ur.getPath();
        String[] params = pth.split("/");
        String name = null;
        if(params.length==4)
            name=params[3];

        if (name == null) {
            queue.setValue("no name passed");
            return request.createResponse(400, "Please pass a name on the URL path");
        } else {
            queue.setValue(name);
            return request.createResponse(200, "Hello, " + name);
        }
    }
    @FunctionName("copy")
    @StorageAccount("AzureWebJobsStorage")
    @BlobOutput(name = "$return", path = "samples-output-java/{name}")
    public static String copy(@BlobTrigger(name = "blob", path = "samples-input-java/{name}") String content, final ExecutionContext context) {
        context.getLogger().info(content);
        return content;
    }
}
