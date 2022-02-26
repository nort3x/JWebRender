package com.github.nort3x.jwebrender;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

// don't want to add libraries for single thing
public class DownloadHtmlPage {

    public static InputStream download(String url_) throws IOException {
        URL url = new URL(url_);
        var is = url.openStream();
        return is;
    }
}
