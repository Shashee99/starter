package com.example.starter;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//@Log4j2
public class DownloadVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RestAPIVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
    LOG.info("Deployed {}!", DownloadVerticle.class.getName());
    JsonObject address = config().getJsonObject("addresses");


        vertx.eventBus().consumer(address.getString("download"),hdl ->{
          String fileName = hdl.body().toString();
          String filePath = config().getString("location") + fileName;
          vertx.fileSystem().readFile(filePath, result -> {
            if (result.succeeded()) {
              byte[] fileData = result.result().getBytes();

              // Send the file data back as a reply to the request
              hdl.reply(fileData);
            } else {
              LOG.error("error occured");
              hdl.fail(404, "File not found");
            }
          });

        });
      }


}
