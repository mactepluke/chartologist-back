package co.syngleton.chartomancer.external_api_requesting;

import javax.naming.ConfigurationException;

interface RequestingServiceFactory {
    DataRequestingService getDataRequestingService() throws ConfigurationException;
}
