package com.example.starter;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RestAPIVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RestAPIVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    super.start(startPromise);
    LOG.info("Deployed {}!", RestAPIVerticle.class.getName());
    startHTTPServer(startPromise);

  }
  private void startHTTPServer(Promise<Void> startPromise) {
    Router restApi = Router.router(vertx);
    restApi.route().failureHandler(handleFailure());

    JsonObject address = config().getJsonObject("addresses");

//    AssetsRestApi.attach(restApi);
    restApi.post("/upload").handler(BodyHandler.create().setMergeFormAttributes(true).setDeleteUploadedFilesOnEnd(true));
    restApi.post("/upload").handler(ctx -> {
      LOG.info("upload route");
      List<FileUpload> uploads = ctx.fileUploads();
      for (FileUpload fileUpload : uploads) {
        vertx.eventBus().send(address.getString("upload"), vertx.fileSystem().readFileBlocking(fileUpload.uploadedFileName()), new DeliveryOptions().addHeader("filename", fileUpload.fileName()));
      }
      ctx.response().setStatusCode(200).end("File uploaded successfully");
    });
//    restApi.post("/upload").handler(ctx -> {
//
//      EventBus eb = vertx.eventBus();
//      eb.publish("uploadfile","uploading the file");
//
//      LOG.info("upload handler !");
//      List<FileUpload> uploads = ctx.fileUploads();
//      for (FileUpload fileUpload : uploads) {
//        System.out.println("Uploaded file : " + fileUpload.fileName());
//        String uploadedFileName = fileUpload.uploadedFileName();
//        String destination = "C:\\uploads\\" + fileUpload.fileName();
//        try {
//          Buffer uploaded = vertx.fileSystem().readFileBlocking(uploadedFileName);
//
//          vertx.fileSystem().writeFileBlocking(destination, uploaded);
//
//
//          HttpServerResponse response = ctx.response();
//          response.setStatusCode(200).end("File uploaded successfully");
//        } catch (Exception e) {
//
//          LOG.error("Error uploading file: " + fileUpload.fileName(), e);
//          ctx.fail(500);
//          return;
//        }
//      }
//    });
    restApi.get("/download").handler(context -> {

      String fileName = context.request().getParam("fileName");
      LOG.info("download handler {}!", RestAPIVerticle.class.getName());

      vertx.eventBus().request(address.getString("download"),fileName,asyncResult ->{
        if (asyncResult.succeeded()) {
          // File download succeeded, response received
          Message<Object> message = asyncResult.result();
          byte[] fileData = (byte[]) message.body();

          // Set response headers
          context.response()
            .putHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
            .putHeader("Content-Type", "application/octet-stream")
            .end(Buffer.buffer(fileData));
        } else {
          // File download failed
          context.fail(404); // Not Found
        }
      });

    });
    restApi.get("/allfiles").handler(context -> {

//      String fileName = context.request().getParam("fileName");
      LOG.info("allfile get handler {}!", RestAPIVerticle.class.getName());
      vertx.eventBus().request(address.getString("allFiles"),"allfiles",result->{
        if (result.succeeded()) {
          Message<Object> message = result.result();
          JsonArray filenames = (JsonArray) message.body();

          // Iterate over the elements of the JsonArray and process them accordingly
          List<String> namelist = new ArrayList<>();
          for (Object obj : filenames) {
            namelist.add(obj.toString());
          }

          HttpServerResponse response = context.response();
          response.setStatusCode(200);
          response.end(new JsonArray(namelist).toBuffer());
        } else {
       LOG.error("error occured");
        }
      });

    });
    restApi.delete("/delete").handler(context -> {
      LOG.info("delete handler {}!", RestAPIVerticle.class.getName());
      String fileName = context.request().getParam("fileName");

      String filePath = "C:\\uploads\\" + fileName;
      vertx.fileSystem().delete(filePath, result -> {
        if (result.succeeded()) {

          HttpServerResponse response = context.response();
          response.end("Deleted succesfully");
        } else {
          // Failed to read the file
          context.fail(404); // Not Found
        }
      });
    });



        vertx.createHttpServer()
          .requestHandler(restApi)
          .exceptionHandler(err -> LOG.error("HTTP Server error : ",err))
          .listen(config().getInteger("port",8080), http -> {
            if (http.succeeded()) {
              startPromise.complete();
              LOG.info("HTTP server started on port {}",config().getInteger("port",8080));
            } else {
              startPromise.fail(http.cause());
            }
          });
//        System.out.println(config.getString("path"));
//        System.out.println(config.getString("port"));


  }

  private static Handler<RoutingContext> handleFailure() {
    return errContext -> {
      if (errContext.response().ended()) {
//      Ignore completed response
        return;
      }
      LOG.error("Route Error : ", errContext.failure());
      errContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
