package com.redhat;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class WebclientConnectionCloseReproducer extends AbstractVerticle {
    
    WebClient client;
    
    @Override
    public void start(Future<Void> startFuture) {
        WebClientOptions clientOptions = new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080)
//                .setKeepAlive(false)
                .setMaxPoolSize(50);
        
        client = WebClient.create(vertx, clientOptions);
        startHttpServer(startFuture);
    }
    
    private void startHttpServer(Future<Void> startFuture) {
        Router router = Router.router(vertx);
        router.get("/testroute").handler(ctx -> {
            if (isInGoodMood()) {
                sendSuccess(ctx);
            }
            else {
                sendFailure(ctx);
            }
        });
        
        vertx.createHttpServer().requestHandler(router::accept).listen(8080, "0.0.0.0", httpServerRes -> {
            if (httpServerRes.succeeded()) {
                startRequests();
                startFuture.complete();
            }
            else {
                startFuture.fail(httpServerRes.cause());
            }
        });
    }
    
    private boolean isInGoodMood() {
        return new Random().nextBoolean();
    }
    
    private void sendSuccess(RoutingContext ctx) {
        ctx.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .setStatusMessage(HttpResponseStatus.OK.reasonPhrase())
                .putHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload,max-age=7776000")
                .putHeader("X-FRAME-OPTIONS", "SAMEORIGIN")
                .putHeader("Cache-Control", "no-cache,no-store,no-transform,must-revalidate,max-age=0,s-maxage=0")
                .putHeader("Content-Language", "en-US,us")
                .putHeader("X-Content-Type-Options", "nosniff")
                .putHeader("Date", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME))
                .putHeader("X-TRANSACTION", "{" + UUID.randomUUID() + "}")
                .putHeader("X-RateLimit-Limit", "10000")
                .putHeader("X-RateLimit-Remaining", Integer.toString(new Random().nextInt(10000)))
                .putHeader("X-RateLimit-Reset", "31908")
                .putHeader("Vary", "Accept-Encoding")
                .putHeader("Content-Type", "application/json;charset=UTF-8")
                .putHeader("Transfer-Encoding", "chunked")
                .end("{\"field\": \"value\"}");
    }
    
    private void sendFailure(RoutingContext ctx) {
        ctx.response()
                .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .setStatusMessage(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase())
                .putHeader("Cache-Control", "no-cache,no-store,no-transform,must-revalidate,max-age=0,s-maxage=0")
                .putHeader("Content-Language", "en-US,us")
                .putHeader("X-Error", "0:IOError  while Proxying Request")
                .putHeader("X-Content-Type-Options", "nosniff")
                .putHeader("Date", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME))
                .putHeader("Strict-Transport-Security", "max-age=7776000")
                .putHeader("Content-Type", "application/json;charset=UTF-8")
                .putHeader("X-Cnection", "close")
                .putHeader("Vary", "Accept-Encoding")
                .putHeader("Transfer-Encoding", "chunked")
                .bodyEndHandler(end -> ctx.response().close())
                .end("{\"errors\":[{\"errorMessage\":\"IOError  while Proxying Request\",\"errorCode\":0}]}");
    }
    
    private void startRequests() {
        for (int i = 0; i < 1000; i++) {
            client.get("/testroute")
                    .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
                    .putHeader(HttpHeaders.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())
                    .send(res -> {
                        if (res.succeeded()) {
                            JsonObject body = res.result().bodyAsJsonObject();
                            if (body.containsKey("errors")) {
                                JsonObject errorObj = body.getJsonArray("errors").getJsonObject(0);
                                System.out.println("Error ( " + errorObj.getInteger("errorCode") + "): " + errorObj.getString("errorMessage"));
                            }
                        }
                        else {
                            System.out.println("Failed to send request");
                            res.cause().printStackTrace();
                        }
                    });
        }
    }
}
