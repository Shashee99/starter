package com.example.starter;


import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;


import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setOptional(true)
      .setConfig(new JsonObject().put("path", "src/main/resources/my-config.json"));
//    ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);

    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig().onComplete(r ->{
      if(r.failed())
      {
        LOG.info("error occured");
      }
      else {
        JsonObject config = r.result();
        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(RestAPIVerticle.class.getName(),deploymentOptions
          )
          .onFailure(startPromise::fail)
          .onSuccess(id -> {
            LOG.info("Deployed {} with {}",RestAPIVerticle.class.getName(),id);

            vertx.deployVerticle(new UploadVerticle(),new DeploymentOptions().setConfig(config).setThreadingModel(ThreadingModel.WORKER)).onSuccess(
              id1->{
                LOG.info("Deployed {} with {}",UploadVerticle.class.getName(),id1);
              }
            );
            vertx.deployVerticle(new DownloadVerticle(),new DeploymentOptions().setConfig(config).setThreadingModel(ThreadingModel.WORKER)).onSuccess(id2 -> {
              LOG.info("Deployed {} with {}",DownloadVerticle.class.getName(),id2);
            });;
            vertx.deployVerticle(new AllFilesViewVerticle(),new DeploymentOptions().setConfig(config).setThreadingModel(ThreadingModel.WORKER)).onSuccess(id3 -> {
              LOG.info("Deployed {} with {}",AllFilesViewVerticle.class.getName(),id3);
            });
            startPromise.complete();
          });
      }
    });
  }
}
