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

@Component
@Slf4j
public class JWebRenderContext extends Application {

    Stage priStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.priStage = primaryStage;

        var p = new HBox();
        p.setAlignment(Pos.CENTER);
        p.getChildren().add(new Label("JWebRender!"));
        var s = new Scene(p);
        primaryStage.setScene(s);
        primaryStage.show();
    }

    @PostConstruct
    void construct() throws InterruptedException {
        var t = new Thread(()->{
            launch(this.getClass());
        });
        t.setDaemon(false);
        t.start();
        Thread.sleep(1000);
    }




    public void doRenderAndDump(int width, int height, String format, double pixelScale, InputStream inps, OutputStream oups) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);

        Platform.runLater(()->{

            try {
                Stage s = new Stage();
                s.initOwner(priStage);
                new Renderer(
                        width,
                        height,
                        format,
                        pixelScale,
                        inps,
                        oups,
                        () -> {
                            s.close();
                            log.debug("render completed");
                            cdl.countDown();
                        }
                ).render(s);
            }catch (IOException e){
                log.warn("io exception",e);
            }
        });
        cdl.await();
    }
}
