package io.versehub;

import com.google.inject.Guice;
import io.versehub.app.DeoApp;
import io.versehub.app.di.*;
import io.versehub.common.aapi.handler.module.HandlerRegistryModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeoStarterBoot {
  private static final String HANDLER_PACKAGE = "io.versehub.app";

  @SneakyThrows
  public static void main(String[] args) {
    DeoInjector.INSTANCE = Guice.createInjector( //
      new HandlerRegistryModule(HANDLER_PACKAGE), //
            new PgRepoModule(), //
            new DeoHttpModule(), //
            new DeoLoadConfigModule(), //
            new AccountTokenHelperModule()
    );

    DeoApp app = null;
    try {
      app = DeoInjector.INSTANCE.getInstance(DeoApp.class);
      app.startSync();
      log.info("Application started successfully!!!");
    } catch (Throwable e) {
      log.error("Error occurs while starting application {}, stop!!!",
        DeoStarterBoot.class.getName(), e);
      if (app != null)
        app.stopSync();
      Thread.sleep(1000);
      System.exit(1);
    }

  }
}
