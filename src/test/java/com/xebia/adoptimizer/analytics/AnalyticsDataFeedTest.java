package com.xebia.adoptimizer.analytics;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AnalyticsDataFeedTest {
    private static final String TABLE_ID = "INSERT_TABLE_ID_HERE";

    public DataFeed feed;
    private Properties props;

    @Before
    public void readProperties() throws IOException {
        InputStream stream = AnalyticsIntegrationTest.class.getClassLoader().getResourceAsStream("googleauth.properties");
        props = new Properties();
        props.load(stream);
    }

    /**
     * Creates a new service object, attempts to authorize using the Client Login
     * authorization mechanism and requests data from the Google Analytics API.
     */
    @Test
    public void shouldGetDataFromAnalyticsDataFeed() throws Exception {
        // Configure GA API.
        AnalyticsService as = new AnalyticsService("gaExportAPI_acctSample_v2.0");

        // Client Login Authorization.
        as.setUserCredentials(props.getProperty("google.user"), props.getProperty("google.password"));

        // GA Data Feed query uri.
        String baseUrl = "https://www.google.com/analytics/feeds/data";
        DataQuery query = new DataQuery(new URL(baseUrl));
        query.setIds(TABLE_ID);
        query.setDimensions("ga:source,ga:medium");
        query.setMetrics("ga:visits,ga:bounces");
        query.setSegment("gaid::-11");
        query.setFilters("ga:medium==referral");
        query.setSort("-ga:visits");
        query.setMaxResults(5);
        query.setStartDate("2008-10-01");
        query.setEndDate("2008-10-31");
        URL url = query.getUrl();
        System.out.println("URL: " + url.toString());

        // Send our request to the Analytics API and wait for the results to
        // come back.
        feed = as.getFeed(url, DataFeed.class);
        
        printFeedData();
        
    }

    /**
     * Prints the important Google Analytics relates data in the Data Feed.
     */
    public void printFeedData() {
        System.out.println("\n-------- Important Feed Information --------");
        System.out.println(
                "\nFeed Title      = " + feed.getTitle().getPlainText() +
                        "\nFeed ID         = " + feed.getId() +
                        "\nTotal Results   = " + feed.getTotalResults() +
                        "\nSart Index      = " + feed.getStartIndex() +
                        "\nItems Per Page  = " + feed.getItemsPerPage() +
                        "\nStart Date      = " + feed.getStartDate().getValue() +
                        "\nEnd Date        = " + feed.getEndDate().getValue());
    }

    /**
     * Prints the important information about the data sources in the feed.
     * Note: the GA Export API currently has exactly one data source.
     */
    public void printFeedDataSources() {
        DataSource gaDataSource = feed.getDataSources().get(0);
        System.out.println("\n-------- Data Source Information --------");
        System.out.println(
                "\nTable Name      = " + gaDataSource.getTableName().getValue() +
                        "\nTable ID        = " + gaDataSource.getTableId().getValue() +
                        "\nWeb Property Id = " + gaDataSource.getProperty("ga:webPropertyId") +
                        "\nProfile Id      = " + gaDataSource.getProperty("ga:profileId") +
                        "\nAccount Name    = " + gaDataSource.getProperty("ga:accountName"));
    }

    /**
     * Prints all the metric names and values of the aggregate data. The
     * aggregate metrics represent the sum of the requested metrics across all
     * of the entries selected by the query and not just the rows returned.
     */
    public void printFeedAggregates() {
        System.out.println("\n-------- Aggregate Metric Values --------");
        Aggregates aggregates = feed.getAggregates();
        for (Metric metric : aggregates.getMetrics()) {
            System.out.println(
                    "\nMetric Name  = " + metric.getName() +
                            "\nMetric Value = " + metric.getValue() +
                            "\nMetric Type  = " + metric.getType() +
                            "\nMetric CI    = " + metric.getConfidenceInterval().toString());
        }
    }

    /**
     * Prints segment information if the query has an advanced segment defined.
     */
    public void printSegmentInfo() {
        if (feed.hasSegments()) {
            System.out.println("\n-------- Advanced Segments Information --------");
            for (Segment segment : feed.getSegments()) {
                System.out.println(
                        "\nSegment Name       = " + segment.getName() +
                                "\nSegment ID         = " + segment.getId() +
                                "\nSegment Definition = " + segment.getDefinition().getValue());
            }
        }
    }

    /**
     * Prints all the important information from the first entry in the
     * data feed.
     */
    public void printDataForOneEntry() {
        System.out.println("\n-------- Important Entry Information --------\n");
        if (feed.getEntries().isEmpty()) {
            System.out.println("No entries found");
        } else {
            DataEntry singleEntry = feed.getEntries().get(0);

            // Properties specific to all the entries returned in the feed.
            System.out.println("Entry ID    = " + singleEntry.getId());
            System.out.println("Entry Title = " + singleEntry.getTitle().getPlainText());

            // Iterate through all the dimensions.
            for (Dimension dimension : singleEntry.getDimensions()) {
                System.out.println("Dimension Name  = " + dimension.getName());
                System.out.println("Dimension Value = " + dimension.getValue());
            }

            // Iterate through all the metrics.
            for (Metric metric : singleEntry.getMetrics()) {
                System.out.println("Metric Name  = " + metric.getName());
                System.out.println("Metric Value = " + metric.getValue());
                System.out.println("Metric Type  = " + metric.getType());
                System.out.println("Metric CI    = " + metric.getConfidenceInterval().toString());
            }
        }
    }

    /**
     * Get the data feed values in the feed as a string.
     *
     * @return {String} This returns the contents of the feed.
     */
    public String getEntriesAsTable() {
        if (feed.getEntries().isEmpty()) {
            return "No entries found";
        }
        DataEntry singleEntry = feed.getEntries().get(0);
        List<String> feedDataNames = new ArrayList<String>();
        StringBuffer feedDataValues = new StringBuffer("\n-------- All Entries In A Table --------\n");

        // Put all the dimension and metric names into an array.
        for (Dimension dimension : singleEntry.getDimensions()) {
            feedDataNames.add(dimension.getName());
        }
        for (Metric metric : singleEntry.getMetrics()) {
            feedDataNames.add(metric.getName());
        }

        // Put the values of the dimension and metric names into the table.
        for (DataEntry entry : feed.getEntries()) {
            for (String dataName : feedDataNames) {
                feedDataValues.append(String.format("\n%s \t= %s",
                        dataName, entry.stringValueOf(dataName)));
            }
            feedDataValues.append("\n");
        }
        return feedDataValues.toString();
    }
}
