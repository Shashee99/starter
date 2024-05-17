package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;

public class AllFilesViewVerticle extends AbstractVerticle {
 private static final Logger log = LogManager.getLogger(RestAPIVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
    log.info("Deployed {}!", AllFilesViewVerticle.class.getName());
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
    log.error("error occured");
//          context.fail(404); // Not Found
        }
      });
    });
  }
}
