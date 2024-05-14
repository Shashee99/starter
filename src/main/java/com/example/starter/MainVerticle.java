package com.example.starter;


import io.vertx.core.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static void main(String[] args) {
//    var vertex = Vertx.vertx();
//    vertex.exceptionHandler(err ->{
//      LOG.error("Unhandled : ",err);
//    });
//    vertex.deployVerticle(new MainVerticle(), ar ->{
//      if (ar.failed()){
//        LOG.error("Failed to deploy : ",ar.cause());
//        return;
//      }
//      LOG.info("Deployed {}!", MainVerticle.class.getName());
//    });
//    vertex.deployVerticle(new UploadVerticle());
//    vertex.deployVerticle(new DownloadVerticle());
//    vertex.deployVerticle(new AllFilesViewVerticle());


  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(RestAPIVerticle.class.getName()
      )
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        LOG.info("Deployed {} with {}",RestAPIVerticle.class.getName(),id);
        startPromise.complete();
      });
    vertx.deployVerticle(new UploadVerticle(),new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER)).onSuccess(
      id->{
        LOG.info("Deployed {} with {}",UploadVerticle.class.getName(),id);
      }
    );
    vertx.deployVerticle(new DownloadVerticle(),new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER)).onSuccess(id->{
      LOG.info("Deployed {} with {}",DownloadVerticle.class.getName(),id);
    });;
    vertx.deployVerticle(new AllFilesViewVerticle(),new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER)).onSuccess(id->{
      LOG.info("Deployed {} with {}",AllFilesViewVerticle.class.getName(),id);
    });
  }

//  private static int processors() {
//    return Math.max(1,Runtime.getRuntime().availableProcessors());
//  }


}
