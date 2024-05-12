package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(UploadVerticle.class);
  private static final String UPLOADS_DIRECTORY = "C:\\uploads\\";

  @Override
  public void start() {
    LOG.info("Deployed {}!", UploadVerticle.class.getName());
    vertx.eventBus().consumer("upload.file", message -> {
      Buffer buffer = (Buffer) message.body(); // Get the Buffer object
      String filename = message.headers().get("filename");


      byte[] fileData = buffer.getBytes();

      saveFile(filename, fileData);
    });
  }

  private void saveFile(String filename, byte[] fileData) {
    try {
      Path filePath = Paths.get(UPLOADS_DIRECTORY + filename);
      Files.write(filePath, fileData);
      LOG.info("File saved successfully at: {} ", filePath);
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("Error occurred");
    }
  }
}
