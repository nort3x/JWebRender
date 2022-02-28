package com.github.nort3x.jwebrender;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class JWebRenderContext extends Application {

    Stage priStage;
    HBox p = new HBox();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.priStage = primaryStage;


        p.setAlignment(Pos.CENTER);
        p.getChildren().add(new Label("JWebRender!"));
        var s = new Scene(p);
        primaryStage.setScene(s);
        primaryStage.show();
    }

    @PostConstruct
    void construct() throws InterruptedException {
        var t = new Thread(() -> {
            launch(this.getClass());
        });
        t.setDaemon(false);
        t.start();
        Thread.sleep(1000);
    }



    public void doRenderAndDump(int width, int height, int xOffset, int yOffset, String format, double pixelScale,long wait, InputStream inps, OutputStream oups) throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        AtomicReference<Exception> exceptionAtomicReference = new AtomicReference<>(null);
        Platform.runLater(() -> {

            Stage s = new Stage();
            s.initOwner(priStage);
            new Renderer(
                    width,
                    height,
                    xOffset,
                    yOffset,
                    format,
                    pixelScale,
                    wait,
                    inps,
                    oups,
                    () -> {
                        s.close();
                        log.debug("render completed");
                        cdl.countDown();
                    },
                    (e) -> {
                        s.close();
                        log.warn("render exception", e);
                        exceptionAtomicReference.set(e);
                    }
            ).render(s);
        });
        cdl.await();
        if(exceptionAtomicReference.get() != null)
            throw exceptionAtomicReference.get();
    }
}
