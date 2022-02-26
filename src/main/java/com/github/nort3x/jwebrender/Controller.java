package com.github.nort3x.jwebrender;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
public class Controller {

    @Autowired
    JWebRenderContext jWebRenderContext;

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
                       @RequestParam double pixelScale, HttpServletResponse response) throws Exception {

        var formatLower = format.toLowerCase();
        if (    formatLower.equals("png") ||
                formatLower.equals("jpg") ||
                formatLower.equals("gif") ||
                formatLower.equals("bmp")
        )
            response.setContentType("image/"+format);
        else
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);


        jWebRenderContext.doRenderAndDump(width, height, x_offset, y_offset, format.toUpperCase(), pixelScale, new ByteArrayInputStream(html), response.getOutputStream());
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
                       @RequestParam double pixelScale, HttpServletResponse response) throws Exception {

        var formatLower = format.toLowerCase();
        if (    formatLower.equals("png") ||
                formatLower.equals("jpg") ||
                formatLower.equals("gif") ||
                formatLower.equals("bmp")
        )
            response.setContentType("image/"+format);
        else
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            jWebRenderContext.doRenderAndDump(width, height, x_offset, y_offset, format.toUpperCase(), pixelScale, DownloadHtmlPage.download(url), response.getOutputStream());
    }
}
