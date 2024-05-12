package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RestAPIVerticle.class);
  private static final String UPLOADS_DIRECTORY = "C:\\uploads\\";
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
    vertx.eventBus().consumer("download.file",hdl ->{
      String fileName = hdl.body().toString();
      System.out.println(fileName);
   String filePath = "C:\\uploads\\" + fileName;
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
