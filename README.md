[![Publish Docker image](https://github.com/nort3x/JWebRender/actions/workflows/docker-image.yml/badge.svg)](https://github.com/nort3x/JWebRender/actions/workflows/docker-image.yml)
[![Java CI with Gradle](https://github.com/nort3x/JWebRender/actions/workflows/gradle.yml/badge.svg)](https://github.com/nort3x/JWebRender/actions/workflows/gradle.yml)
# JWebRender
simple REST service to capture snapshots from html pages

## Supported-Options:

* scaling (pixelScaleFactor)
* view port offset (offsetX,offsetY)
* dimension (width, height)
* format (png,jpg,gif,bmp)

## deploy
docker: [nort3x/jwebrender](https://hub.docker.com/r/nort3x/jwebrender) <br/>
cli:
```bash
# map port 8080 to your desired port
# cli:
docker run -p 7878:8080 nort3x/jwebrender
```
compose:
```yml
version: "3.5"
services:
  jwebrender-service:
    image: nort3x/jwebrender
    ports:
      - "7878:8080"
    restart: always
```

## End-Points

| Path     | Description   |
|----------|:-------------:|
| /swagger | swagger console |
| /html    |    render html content and take snapshot   |
| /url     | render url and take snapshot |

## How Does it Works?
it's a naive combanition of `JavaFX` and `SpringBoot`<br/>
on your request it renders content in JavaFX `WebView` engine <br/>
for running in headless mode (which it doesn't) it will create a virtual display docker with gtk libgl1-mesa and xvfb

