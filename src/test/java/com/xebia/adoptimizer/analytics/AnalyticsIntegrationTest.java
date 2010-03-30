package com.xebia.adoptimizer.analytics;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.data.analytics.AccountEntry;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author albertsikkema
 */
public class AnalyticsIntegrationTest {
    private static final String CLIENT_USERNAME = "a.sikkema80@gmail.com";
    private static final String CLIENT_PASS = "xxx";

    @Test
    public void shouldInvokeAnalyticsAndGetSomething() throws ServiceException, IOException {
        // Service Object to work with the Google Analytics Data Export API.
        AnalyticsService analyticsService = new AnalyticsService("gaExportAPI_acctSample_v2.0");

        // ClientLogin Authorization.
        analyticsService.setUserCredentials(CLIENT_USERNAME, CLIENT_PASS);

        URL queryUrl = new URL("https://www.google.com/analytics/feeds/accounts/default?max-results=50");
        AccountFeed accountFeed = analyticsService.getFeed(queryUrl, AccountFeed.class);
        print(accountFeed);
    }
                                                                                                    
    private void print(AccountFeed accountFeed) {
        System.out.println("-------- Account Feed Results --------");
        for (AccountEntry entry : accountFeed.getEntries()) {
            System.out.println(
                            "\nAccount Name  = " + entry.getProperty("ga:accountName") +
                            "\nProfile Name  = " + entry.getTitle().getPlainText() +
                            "\nProfile Id    = " + entry.getProperty("ga:profileId") +
                            "\nTable Id      = " + entry.getTableId().getValue());
        }
    }
}