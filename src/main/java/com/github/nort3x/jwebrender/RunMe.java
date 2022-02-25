package com.github.nort3x.jwebrender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

//@Component
@Slf4j
public class RunMe implements CommandLineRunner {
    @Autowired
    JWebRenderContext jWebRenderContext;

    @Override
    public void run(String... args) throws Exception {
        log.info("guz");
        jWebRenderContext.doRenderAndDump(1,2,"PNG",1,new ByteArrayInputStream(
                """
                        <html></html>
                        """.getBytes(StandardCharsets.UTF_8)
        ),null);
        log.info("guz2");
    }
}
