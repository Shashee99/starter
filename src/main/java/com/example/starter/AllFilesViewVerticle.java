package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AllFilesViewVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RestAPIVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
    LOG.info("Deployed {}!", AllFilesViewVerticle.class.getName());
    JsonObject address = config().getJsonObject("addresses");
    vertx.eventBus().consumer(address.getString("allFiles"),message -> {
//      String filePath = "C:\\uploads";
      String filePath = config().getString("location");
      final JsonArray filenames = new JsonArray();

      vertx.fileSystem().readDir(filePath, result -> {
        if (result.succeeded()) {
          List<String> filesnames = result.result();
          filesnames.forEach(filenames::add);
//          HttpServerResponse response = context.response();
//          response.setStatusCode(200);
//          response.end(filenames.toBuffer());
          System.out.println(filesnames.toString());
          message.reply(filenames);


        } else {
    LOG.error("error occured");
//          context.fail(404); // Not Found
        }
      });
    });
  }
}
