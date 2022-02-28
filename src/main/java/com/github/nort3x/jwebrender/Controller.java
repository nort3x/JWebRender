package com.github.nort3x.jwebrender;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
public class Controller {

    @Autowired
    JWebRenderContext jWebRenderContext;

    private void renderAndSend(
            int width,
            int height,
            int x_offset,
            int y_offset,
            String format,
            double pixelScale,
            long wait,
            HttpServletResponse response,
            InputStream inputStream
    ) throws Exception {

        var formatLower = format.toLowerCase();
        if (formatLower.equals("png") ||
                formatLower.equals("jpg") ||
                formatLower.equals("gif") ||
                formatLower.equals("bmp")
        )
            response.setContentType("image/" + format);
        else
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        jWebRenderContext.doRenderAndDump(width, height, x_offset, y_offset, format.toUpperCase(), pixelScale,wait, inputStream, response.getOutputStream());
    }


    @ApiResponse(
            description = "format supports: PEG, PNG, GIF, BMP and WBMP"
    )
    @PostMapping("/html")
    public void render(@RequestBody byte[] html,
                       @RequestParam int width,
                       @RequestParam int height,
                       @RequestParam int x_offset,
                       @RequestParam int y_offset,
                       @RequestParam String format,
                       @RequestParam double pixelScale,
                       @RequestParam long wait,
                       HttpServletResponse response) throws Exception {

        renderAndSend(width, height, x_offset, y_offset, format, pixelScale, wait, response, new ByteArrayInputStream(html));
    }


    @ApiResponse(
            description = "format supports: PEG, PNG, GIF, BMP and WBMP"
    )
    @PostMapping("/url")
    public void renderUrl(@RequestBody String url,
                          @RequestParam int width,
                          @RequestParam int height,
                          @RequestParam int x_offset,
                          @RequestParam int y_offset,
                          @RequestParam String format,
                          @RequestParam double pixelScale,
                          @RequestParam long wait,
                          HttpServletResponse response) throws Exception {

        renderAndSend(width, height, x_offset, y_offset, format, pixelScale,wait, response, DownloadHtmlPage.download(url));
    }


    @ApiResponse(
            description = "format supports: PEG, PNG, GIF, BMP and WBMP"
    )
    @GetMapping("/url")
    public void renderUrlGet(
            @RequestParam String url,
            @RequestParam int width,
            @RequestParam int height,
            @RequestParam int x_offset,
            @RequestParam int y_offset,
            @RequestParam String format,
            @RequestParam double pixelScale,
            @RequestParam long wait,
            HttpServletResponse response) throws Exception {

        renderUrl(URLDecoder.decode(url, StandardCharsets.UTF_8), width, height, x_offset, y_offset, format, pixelScale,wait, response);
    }
}
