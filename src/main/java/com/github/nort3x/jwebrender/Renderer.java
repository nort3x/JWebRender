package com.github.nort3x.jwebrender;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.transform.Transform;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Renderer {
    int width;
    int height;

    int offsetX;
    int offsetY;

    double pixelScale;
    InputStream inputStream;
    OutputStream outputStream;

    String format;
    Runnable onFinish;
    Consumer<Exception> onError;

    public Renderer(int width, int height, int xOffset, int yOffset, String format, double pixelScale, InputStream inputStream, OutputStream outputStream, Runnable onFinish,Consumer<Exception> onError) {
        this.width = width;
        this.height = height;
        this.pixelScale = pixelScale;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.onFinish = onFinish;
        this.format = format;
        this.offsetX = xOffset;
        this.offsetY = yOffset;
        this.onError = onError;
    }

    public void render(Stage primaryStage) {
        WebView wv = new WebView();

        wv.setMinWidth(width);
        wv.setMaxWidth(width);
        wv.setPrefWidth(width);

        wv.setPrefHeight(height);
        wv.setMinHeight(height);
        wv.setMaxHeight(height);


        CountDownLatch cdl =  new CountDownLatch(1);

        wv.getEngine().setJavaScriptEnabled(true);
        wv.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                if(newValue.equals(Worker.State.SUCCEEDED))
                    cdl.countDown();
            }
        });
        try {
            wv.getEngine().loadContent(new String(inputStream.readAllBytes()));
        }catch (Exception e){
            onError.accept(e);
        }
        HBox p  = new HBox();
        p.getChildren().add(wv);
        HBox.setHgrow(wv, Priority.ALWAYS);
        Scene s = new Scene(p,1,1);
        primaryStage.setScene(s);
        primaryStage.show();
        primaryStage.toBack();

        wv.getEngine().setUserStyleSheetLocation(getClass().getClassLoader().getResource("style.css").toExternalForm());

        new Thread(()->{

            try {
                cdl.await();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Platform.runLater(()->{

                int im_width = (int)Math.rint(pixelScale*(wv.getWidth()- offsetX));
                int im_height =  (int)Math.rint(pixelScale*(wv.getHeight() - offsetY));
                SnapshotParameters sp = new SnapshotParameters();
                sp.setViewport(new Rectangle2D(offsetX*pixelScale, offsetY*pixelScale,im_width,im_height));
                sp.setTransform(Transform.scale(pixelScale, pixelScale));
                BufferedImage bufferedImage = new BufferedImage(im_width,im_height,BufferedImage.TYPE_INT_ARGB);


                SwingFXUtils.fromFXImage(wv.snapshot(sp,new WritableImage(im_width, im_height)), bufferedImage);
                SwingFXUtils.toFXImage(bufferedImage, new WritableImage(im_width, im_height));
                try {
                    ImageIO.write(bufferedImage, format, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    onFinish.run();
                }

            });

        }).start();
    }
}
