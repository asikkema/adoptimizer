package com.xebia.adoptimizer.analytics;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.data.analytics.*;
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
    private static final String CLIENT_USERNAME = "xxx";
    private static final String CLIENT_PASS = "xxx";
    private AccountFeed accountFeed;

    @Test
    public void shouldInvokeAnalyticsAndGetSomething() throws ServiceException, IOException {
        // Service Object to work with the Google Analytics Data Export API.
        AnalyticsService analyticsService = new AnalyticsService("gaExportAPI_acctSample_v2.0");

        // ClientLogin Authorization.
        analyticsService.setUserCredentials(CLIENT_USERNAME, CLIENT_PASS);

        URL queryUrl = new URL("https://www.google.com/analytics/feeds/accounts/default?max-results=50");
        accountFeed = analyticsService.getFeed(queryUrl, AccountFeed.class);
        printFeedDetails();
    }
                                                                                                    
  /**
   * Prints the important Google Analytics related data in the Account Feed.
   */
  public void printFeedDetails() {
    System.out.println("\n-------- Important Feed Data --------");
    System.out.println(
        "\nFeed Title     = " + accountFeed.getTitle().getPlainText() +
        "\nTotal Results  = " + accountFeed.getTotalResults() +
        "\nStart Index    = " + accountFeed.getStartIndex() +
        "\nItems Per Page = " + accountFeed.getItemsPerPage() +
        "\nFeed ID        = " + accountFeed.getId());
  }

  /**
   * Prints the advanced segments for this user.
   */
  public void printAdvancedSegments() {
    System.out.println("\n-------- Advanced Segments --------");
    if (!accountFeed.hasSegments()) {
      System.out.println("No advanced segments found");
    } else {
      for (Segment segment : accountFeed.getSegments()) {
        System.out.println(
            "\nSegment Name       = " + segment.getName() +
            "\nSegment ID         = " + segment.getId() +
            "\nSegment Definition = " + segment.getDefinition().getValue());
      }
    }
  }

  /**
   * Prints custom variable information for the first profile that has custom
   * variables configured.
   */
  public void printCustomVarForOneEntry() {
    System.out.println("\n-------- Custom Variables --------");
    if (accountFeed.getEntries().isEmpty()) {
      System.out.println("No entries found.");
    } else {
      // Go through each entry to see if any has a Custom Variable defined.
      for (AccountEntry entry : accountFeed.getEntries()) {
        if (entry.hasCustomVariables()) {
          for (CustomVariable customVariable : entry.getCustomVariables()) {
            System.out.println(
                "\nCustom Variable Index = " + customVariable.getIndex() +
                "\nCustom Variable Name  = " + customVariable.getName() +
                "\nCustom Variable Scope = " + customVariable.getScope());
          }
          return;
        }
      }
      System.out.println("\nNo custom variables defined for this user");
    }
  }

  /**
   * Prints all the goal information for one profile.
   */
  public void printGoalsForOneEntry() {
    System.out.println("\n-------- Goal Configuration --------");
    if (accountFeed.getEntries().isEmpty()) {
      System.out.println("No entries found.");
    } else {
      // Go through each entry to see if any have Goal information.
      for (AccountEntry entry : accountFeed.getEntries()) {
        if (entry.hasGoals()) {
          for (Goal goal : entry.getGoals()) {
            // Print common information for all Goals in this profile.
            System.out.println("\n----- Goal -----");
            System.out.println(
                "\nGoal Number = " + goal.getNumber() +
                "\nGoal Name   = " + goal.getName() +
                "\nGoal Value  = " + goal.getValue() +
                "\nGoal Active = " + goal.getActive());
            if (goal.hasDestination()) {
              printDestinationGoal(goal.getDestination());
            } else if (goal.hasEngagement()) {
              printEngagementGoal(goal.getEngagement());
            }
          }
          return;
        }
      }
    }
  }

  /**
   * Prints the important information for destination goals including all the
   * configured steps if they exist.
   * @param destination the destination goal configuration.
   */
  public void printDestinationGoal(Destination destination) {
    System.out.println("\n\t----- Destination Goal -----");
    System.out.println(
        "\n\tExpression      = " + destination.getExpression() +
        "\n\tMatch Type      = " + destination.getMatchType() +
        "\n\tStep 1 Required = " + destination.getStep1Required() +
        "\n\tCase Sensitive  = " + destination.getCaseSensitive());

    // Print goal steps.
    if (destination.hasSteps()) {
      System.out.println("\n\t----- Destination Goal Steps -----");
      for (Step step : destination.getSteps()) {
        System.out.println(
            "\n\tStep Number = " + step.getNumber() +
            "\n\tStep Name   = " + step.getName() +
            "\n\tStep Path   = " + step.getPath());
      }
    }
  }

  /**
   * Prints the important information for Engagement Goals.
   * @param engagement The engagement goal configuration.
   */
  public void printEngagementGoal(Engagement engagement) {
    System.out.println("\n\t----- Engagement Goal -----");
    System.out.println(
        "\n\tGoal Type       = " + engagement.getType() +
        "\n\tGoal Comparison = " + engagement.getComparison() +
        "\n\tGoal Threshold  = " + engagement.getThresholdValue());
  }

  /**
   * Prints the important Google Analytics related data in each Account Entry.
   */
  public void printAccountEntries() {
    System.out.println("\n-------- First 1000 Profiles In Account Feed --------");
    if (accountFeed.getEntries().isEmpty()) {
      System.out.println("No entries found.");
    } else {
      for (AccountEntry entry : accountFeed.getEntries()) {
        System.out.println(
          "\nWeb Property Id = " + entry.getProperty("ga:webPropertyId") +
          "\nAccount Name    = " + entry.getProperty("ga:accountName") +
          "\nAccount ID      = " + entry.getProperty("ga:accountId") +
          "\nProfile Name    = " + entry.getTitle().getPlainText() +
          "\nProfile ID      = " + entry.getProperty("ga:profileId") +
          "\nTable Id        = " + entry.getTableId().getValue() +
          "\nCurrency        = " + entry.getProperty("ga:currency") +
          "\nTimeZone        = " + entry.getProperty("ga:timezone") +
          (entry.hasCustomVariables() ? "\nThis profile has custom variables" : "") +
          (entry.hasGoals() ? "\nThis profile has goals" : ""));
      }
    }
  }
}