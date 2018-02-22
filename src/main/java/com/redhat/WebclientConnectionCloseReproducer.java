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
                .setKeepAlive(false)
//                .setMaxPoolSize(1000)
                ;
        
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
                .end("{\"field189320\":\"NA - GTM\",\"hiringmanager\":{\"profile\":\"https://api.icims.com/customers/2592/people/752739\",\"id\":752739,\"value\":\"Kenneth Langer\"},\"positioncategory\":{\"id\":\"C17514\",\"formattedvalue\":\"Sales\",\"soccode\":\"0\",\"value\":\"Sales\"},\"createddate\":\"2017-12-14 12:01 AM\",\"field17137\":\"Solution Architect\",\"field17236\":\"<ul><li>Educate customers on the capabilities of the Red Hat solutions portfolio and conduct live demonstrations of the solutions</li><li>Build trusted relationships with customers&rsquo; technical staff and leadership</li><li>Identify and qualify opportunities; sell the value of Red Hat solutions</li><li>Manage and carry out complex proofs of concept</li><li>Architect complex solutions that achieve customer requirements</li><li>Gain deep understanding of customer environments and use cases and be the advocate for customer priorities with the BU</li><li>Provide overall technical account management</li></ul>\",\"field356415\":\"No\",\"jobowner\":{\"profile\":\"https://api.icims.com/customers/2592/people/23943\",\"id\":23943,\"value\":\"Jeffrey Bachism\"},\"field18624\":{\"profile\":\"https://api.icims.com/customers/2592/people/652485\",\"id\":652485,\"value\":\"Nick Reed\"},\"field17810\":\"338.NATL.S18\",\"field356535\":\"Q4 FY18\",\"startdate\":\"2018-02-26\",\"field357860\":\" 70,000.00\",\"jobid\":\"60785\",\"qualifications\":\"<ul><li>8+ years of experience as a sales engineer, solutions architect, or equivalent role with large enterprise account</li><li>Recent Telco and network experience including an understanding of key NFV concepts</li><li>Extensive knowledge and experience with emerging technology in areas where Red Hat competes including DevOps, microservices, cloud platforms, software defined storage, container platform and orchestration, and virtualization (including NFV)</li><li>5+ years of experience with Linux in the areas of system administration and integration</li><li>Excellent communication, presentation, and documentation skills</li><li>Ability and willingness to travel up to 50%</li><li>Bachelor&rsquo;s degree in a technical discipline</li></ul><p style=\\\"margin: 0px;\\\">The following is considered a plus:</p><ul><li>Red Hat Certified System Administrator (RHCSA)</li><li>Open source experience</li><li>Experience with Red Hat or competitive solutions</li><li>Experience working in the Telco and Cable ecosystem</li></ul><p style=\\\"color: #666666;\\\"><br />Red Hat is proud to be an equal opportunity workplace and an affirmative action employer. We review applications for employment without regard to their race, color, religion, sex, sexual orientation, gender identity, national origin, ancestry, citizenship, age, uniformed services, genetic information, physical or mental disability, medical condition, marital status, or any other basis prohibited by law.</p><p style=\\\"color: #666666;\\\"><br />Red Hat does not seek or accept unsolicited resumes or CVs from recruitment agencies. We are not responsible for, and will not pay, any fees, commissions, or any other payment related to unsolicited resumes or CVs except as required in a written contract between Red Hat and the recruitment agency or party requesting payment of a fee.</p><p style=\\\"margin: 0px;\\\">&nbsp;</p>\",\"responsibilities\":\"<p style=\\\"margin: 0px;\\\">Use your technical knowledge and excellent communication skills in Red Hat to build trusted relationships and provide technical account management for our customers. The Red Hat North America Telco team is looking for a Senior Solutions Architect to join us in Pennsylvania, USA. In this role, you will guide new business with some of Red Hat&rsquo;s most strategic accounts covering the top CSPs, MSOs, and NEPs in the North America (NA). You&rsquo;ll help our team create solutions and advocate compelling recommendations to Red Hat&rsquo;s NA Telco customers and guide them to adopt our innovative open source technology. To achieve this, you&rsquo;ll need passion for open source, a thorough understanding of business processes, and the ability to identify and create business solutions at the enterprise level. As a Senior Solutions Architect, you will specialize in architecting Red Hat&rsquo;s open source technology in public, private, and hybrid cloud environments.</p>\",\"field129087\":\"had Kickoff call on 12.17\",\"field422012\":\"Not delayed\",\"hiretype\":{\"id\":\"D15002\",\"formattedvalue\":\"Yes\",\"value\":\"Yes\"},\"links\":[{\"rel\":\"self\",\"title\":\"The current profile being viewed.\",\"url\":\"https://api.icims.com/customers/2592/jobs/60785\"}],\"field357850\":\"Cur\",\"overview\":\"<p style=\\\"margin: 0px;\\\">At Red Hat, we connect an innovative community of customers, partners, and contributors to deliver an open source stack of trusted, high-performing solutions. We offer cloud, Linux, middleware, storage, and virtualization technologies, together with award-winning global customer support, consulting, and implementation services. Red Hat is a rapidly growing company supporting more than 90% of Fortune 500 companies.</p>\",\"field17807\":\"Sales.Solution Architecture.Principal Solution Architect.338\",\"field79095\":\"GTM\",\"field330155\":\"Other - Running the Business\",\"field357853\":\" 230,000.00\",\"jobtitle\":\"Senior Solutions Architect\",\"field330156\":\"Multi-product\",\"field357856\":\"Cur\",\"field17763\":\"Backfill headcount for Richard Wang needed to cover Comcast and Charter accounts.  Both critical account that will lack Account SA coverage following Richard's departure on January 5, 2018.\",\"replacing\":\"Richard Wang\",\"field40707\":\"Backfill\",\"field40708\":\"Q4 FY18\",\"jobtype\":{\"id\":\"C8711\",\"formattedvalue\":\"Exempt\",\"value\":\"Exempt\"},\"field17480\":[{\"id\":\"C451508\",\"formattedvalue\":\"Solution Architecture\",\"value\":\"Solution Architecture\"}],\"folder\":{\"id\":\"C40713\",\"formattedvalue\":\"Open\",\"value\":\"Open\"},\"jobnumber\":\"338.NATL.S18\",\"positiontype\":{\"id\":\"D12001\",\"formattedvalue\":\"Fulltime-Regular: Employee\",\"value\":\"Fulltime-Regular: Employee\"},\"field357825\":\"Cur\",\"field357829\":\" 160,000.00\",\"joblocation\":{\"companyid\":81429,\"address\":\"https://api.icims.com/customers/2592/companies/81429/fields/addresses/1370\",\"id\":1370,\"value\":\"Remote Pennsylvania -   Remote, Pennsylvania 17120 United States \"}}");
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
//                    .putHeader(HttpHeaders.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())
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
