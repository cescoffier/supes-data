package org.acme;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JsoupHelper {

    private final WebClient client;

    public JsoupHelper(Vertx vertx) {
        client = WebClient.create(vertx);
    }

    public WebClient client() {
        return client;
    }

    public String download(String url) {
        return client.getAbs(url)
                .timeout(30000)
                .putHeader("User-Agent", "curl/7.64.1")
                .putHeader("Accept", "*/*")
                .sendAndAwait()
                .bodyAsString();
    }

    public Document downloadAndParse(String url) {
        String content = download(url);
        return Jsoup.parse(content);
    }

}
