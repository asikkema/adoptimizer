package com.xebia.adoptimizer.adwords;

import com.google.api.adwords.lib.AdWordsService;
import com.google.api.adwords.lib.AdWordsServiceLogger;
import com.google.api.adwords.lib.AdWordsUser;
import com.google.api.adwords.lib.ServiceAccountantManager;
import com.google.api.adwords.v13.*;
import org.junit.Test;

/**
 *
 */
public class AdWordsDoodle {
    
       @Test
    public void shouldRetrieveSomeAdwords() throws Exception {
        try {
            // Log SOAP XML request and response.
            AdWordsServiceLogger.log();

            // Get AdWordsUser from "~/adwords.properties".
            AdWordsUser user = new AdWordsUser();

            // Get TrafficEstimatorService.
            TrafficEstimatorInterface trafficEstimatorService =
                    user.getService(AdWordsService.V13.TRAFFIC_ESTIMATOR_SERVICE);

            // Set the attributes of the keywords to be estimated.
            KeywordRequest keywordRequest = new KeywordRequest();
            keywordRequest.setText("sale");
            keywordRequest.setMaxCpc(new Long("1000000"));
            keywordRequest.setType(KeywordType.Broad);
            // Make an array of the KeywordRequest(s).
            KeywordRequest[] keywordList = {keywordRequest};

            AdGroupRequest adGroupRequest = new AdGroupRequest();
            adGroupRequest.setKeywordRequests(keywordList);
            adGroupRequest.setMaxCpc(new Long("1000000"));
            AdGroupRequest[] adGroupList = {adGroupRequest};

            GeoTarget geoTarget = new GeoTarget();
            geoTarget.setTargetAll(true);

            CampaignRequest campaignRequest = new CampaignRequest();
            campaignRequest.setAdGroupRequests(adGroupList);
            campaignRequest.setGeoTargeting(geoTarget);
            campaignRequest.setLanguageTargeting(new String[]{"en"});
            campaignRequest.setNetworkTargeting(new NetworkType[]{NetworkType.SearchNetwork});
            CampaignRequest[] campaignRequests = {campaignRequest};

            CampaignEstimate[] estimates =
                    trafficEstimatorService.estimateCampaignList(campaignRequests);

            // Send the request to the TrafficEstimator service.
            int i = 0;
            for (CampaignEstimate cpEstimate : estimates) {
                for (AdGroupEstimate agEstimate : cpEstimate.getAdGroupEstimates()) {
                    for (KeywordEstimate estimate : agEstimate.getKeywordEstimates()) {
                        System.out.println("Keyword: " + keywordList[i].getText()
                                + "\nLower average position is " + estimate.getLowerAvgPosition()
                                + ".\nUpper average position is " + estimate.getUpperAvgPosition()
                                + ".\nLower clicks per day is " + estimate.getLowerClicksPerDay()
                                + ".\nUpper clicks per day is " + estimate.getUpperClicksPerDay()
                                + ".\nLower cpc is " + estimate.getLowerCpc() + ".\nUpper cpc is "
                                + estimate.getUpperCpc() + ".");
                        i++;
                    }
                }
            }

            // Determining how much quota all these operations have consumed.
            System.out.println("Total Quota unit cost for this run: "
                    + ServiceAccountantManager.getInstance().getTotalUnitCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
