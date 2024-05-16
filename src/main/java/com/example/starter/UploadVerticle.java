package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(UploadVerticle.class);
  private static final String UPLOADS_DIRECTORY = "C:\\uploads\\";
//private static final String UPLOADS_DIRECTORY = "/home/shashika/uploads/";
//private static final String UPLOADS_DIRECTORY = "/home/ec2-user/uploads/";
  @Override
  public void start() {
    JsonObject address = config().getJsonObject("addresses");



        LOG.info("Deployed {}!", UploadVerticle.class.getName());
        vertx.eventBus().consumer(address.getString("upload"), message -> {
          Buffer buffer = (Buffer) message.body(); // Get the Buffer object
          String filename = message.headers().get("filename");

      LOG.info("file name {}",filename);

          byte[] fileData = buffer.getBytes();

          saveFile(filename, fileData,config().getString("location"));
        });


  }

  private void saveFile(String filename, byte[] fileData,String location) {
    try {
      Path filePath = Paths.get(location + filename);
      Path fp = Files.write(filePath, fileData);
      LOG.info("File saved {}",fp.toString());
      LOG.info("File saved successfully at : {} ", filePath);
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("Error occurred");
    }
  }
}
