package com.github.kntmr.nulab_api_app.http;

import com.github.kntmr.nulab_api_app.config.ApplicationConfig;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.BacklogClientFactory;
import com.nulabinc.backlog4j.conf.BacklogConfigure;
import com.nulabinc.backlog4j.conf.BacklogJpConfigure;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;

public class BaseClient {

    @Autowired
    private ApplicationConfig conf;
    @Autowired
    private MyBacklogHttpClient myBacklogHttpClient;

    /**
     * BacklogConfigure を設定して BacklogClient を取得する
     *
     * @return
     * @throws MalformedURLException
     */
    public BacklogClient getBacklogClient() throws MalformedURLException {
        BacklogConfigure configure = new BacklogJpConfigure(conf.getSpaceId()).apiKey(conf.getApiKey());
        //BacklogClient client = new BacklogClientFactory(configure).newClient();
        BacklogClient client = new BacklogClientFactory(configure, myBacklogHttpClient).newClient();
        return client;
    }

}
