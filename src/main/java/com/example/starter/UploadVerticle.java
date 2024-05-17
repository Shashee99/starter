package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadVerticle extends AbstractVerticle {
 private static final Logger log = LogManager.getLogger(UploadVerticle.class);
//  private static final String UPLOADS_DIRECTORY = "C:\\uploads\\";
//private static final String UPLOADS_DIRECTORY = "/home/shashika/uploads/";
//private static final String UPLOADS_DIRECTORY = "/home/ec2-user/uploads/";
  @Override
  public void start() {
    JsonObject address = config().getJsonObject("addresses");



        log.info("Deployed {}!", UploadVerticle.class.getName());
        vertx.eventBus().consumer(address.getString("upload"), message -> {
          Buffer buffer = (Buffer) message.body(); // Get the Buffer object
          String filename = message.headers().get("filename");

          log.info("file name {}",filename);

          byte[] fileData = buffer.getBytes();

          saveFile(filename, fileData,config().getString("location"));
        });


  }

  private void saveFile(String filename, byte[] fileData,String location) {
    try {
      Path filePath = Paths.get(location + filename);
      Path fp = Files.write(filePath, fileData);
      log.info("File saved {}",fp.toString());
      log.info("File saved successfully at : {} ", filePath);
    } catch (IOException e) {
      e.printStackTrace();
      log.error("Error occurred");
    }
  }
}
