package org.bigml.binding.resources;

import org.bigml.binding.BigMLClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point to create, retrieve, list, update, and delete centroids.
 * 
 * Full API documentation on the API can be found from BigML at:
 * https://bigml.com/developers/centroids
 * 
 * 
 */
public class Centroid extends AbstractResource {

    // Logging
    Logger logger = LoggerFactory.getLogger(Centroid.class);

    /**
     * Constructor
     * 
     */
    public Centroid() {
        this.bigmlApiKey = System.getProperty("BIGML_API_KEY");
        bigmlAuth = "?username=" + this.bigmlUser + ";api_key="
                + this.bigmlApiKey + ";";
        this.devMode = false;
        super.init();
    }

    /**
     * Constructor
     * 
     */
    public Centroid(final String apiUser, final String apiKey,
            final boolean devMode) {
        this.bigmlUser = apiUser != null ? apiUser : System
                .getProperty("BIGML_USERNAME");
        this.bigmlApiKey = apiKey != null ? apiKey : System
                .getProperty("BIGML_API_KEY");
        bigmlAuth = "?username=" + this.bigmlUser + ";api_key="
                + this.bigmlApiKey + ";";
        this.devMode = devMode;
        super.init();
    }

    /**
     * Creates a new centroid.
     * 
     * POST /andromeda/centroid?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io Content-Type: application/json
     * 
     * @param clusterId
     *            a unique identifier in the form cluster/id where id is a
     *            string of 24 alpha-numeric chars for the cluster.
     * @param args
     *            set of parameters for the new centroid. Optional
     * @param waitTime
     *            time (milliseconds) to wait for next check of FINISHED status
     *            for centroid before to start to create the centroid. Optional
     * @param retries
     *            number of times to try the operation. Optional
     * 
     */
    public JSONObject create(final String clusterId, JSONObject inputDataJSON,
            String args, Integer waitTime, Integer retries) {
        if (clusterId == null || clusterId.length() == 0
                || !(clusterId.matches(CLUSTER_RE))) {
            logger.info("Wrong cluster id");
            return null;
        }

        try {
            waitTime = waitTime != null ? waitTime : 3000;
            retries = retries != null ? retries : 10;
            if (waitTime > 0) {
                int count = 0;
                while (count < retries
                        && !BigMLClient.getInstance(this.devMode)
                                .clusterIsReady(clusterId)) {
                    Thread.sleep(waitTime);
                    count++;
                }
            }

            JSONObject requestObject = new JSONObject();
            if (args != null) {
                requestObject = (JSONObject) JSONValue.parse(args);
            }
            requestObject.put("cluster", clusterId);
            requestObject.put("input_data", inputDataJSON);

            return createResource(CENTROID_URL, requestObject.toJSONString());
        } catch (Throwable e) {
            logger.error("Error creating centroid");
            return null;
        }
    }

    /**
     * Retrieves a centroid.
     * 
     * A centroid is an evolving object that is processed until it reaches the
     * FINISHED or FAULTY state, the method will return a JSONObject that
     * encloses the centroid values and state info available at the time it is
     * called.
     * 
     * GET
     * /andromeda/centroid/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io
     * 
     * @param centroidId
     *            a unique identifier in the form centroid/id where id is a
     *            string of 24 alpha-numeric chars.
     * 
     */
    public JSONObject get(final String centroidId) {
        if (centroidId == null || centroidId.length() == 0
                || !centroidId.matches(CENTROID_RE)) {
            logger.info("Wrong centroid id");
            return null;
        }

        return getResource(BIGML_URL + centroidId);
    }

    /**
     * Retrieves a centroid.
     * 
     * A centroid is an evolving object that is processed until it reaches the
     * FINISHED or FAULTY state, the method will return a JSONObject that
     * encloses the centroid values and state info available at the time it is
     * called.
     * 
     * GET
     * /andromeda/centroid/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io
     * 
     * @param centroid
     *            a centroid JSONObject.
     * 
     */
    public JSONObject get(final JSONObject centroid) {
        String centroidId = (String) centroid.get("resource");
        return get(centroidId);
    }

    /**
     * Check whether a centroid's status is FINISHED.
     * 
     * @param centroidId
     *            a unique identifier in the form centroid/id where id is a
     *            string of 24 alpha-numeric chars.
     * 
     */
    public boolean isReady(final String centroidId) {
        return isResourceReady(get(centroidId));
    }

    /**
     * Check whether a centroid's status is FINISHED.
     * 
     * @param centroid
     *            a centroid JSONObject.
     * 
     */
    public boolean isReady(final JSONObject centroid) {
        String resourceId = (String) centroid.get("resource");
        return isReady(resourceId);
    }

    /**
     * Lists all your centroids.
     * 
     * GET /andromeda/centroid?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * Host: bigml.io
     * 
     * @param queryString
     *            query filtering the listing.
     * 
     */
    public JSONObject list(final String queryString) {
        return listResources(CENTROID_URL, queryString);
    }

    /**
     * Updates a centroid.
     * 
     * PUT
     * /andromeda/centroid/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io Content-Type: application/json
     * 
     * @param centroidId
     *            a unique identifier in the form centroid/id where id is a
     *            string of 24 alpha-numeric chars.
     * @param changes
     *            set of parameters to update the centroid. Optional
     * 
     */
    public JSONObject update(final String centroidId, final String changes) {
        if (centroidId == null || centroidId.length() == 0
                || !centroidId.matches(CENTROID_RE)) {
            logger.info("Wrong centroid id");
            return null;
        }
        return updateResource(BIGML_URL + centroidId, changes);
    }

    /**
     * Updates a centroid.
     * 
     * PUT
     * /andromeda/centroid/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io Content-Type: application/json
     * 
     * @param centroid
     *            an centroid JSONObject
     * @param changes
     *            set of parameters to update the centroid. Optional
     * 
     */
    public JSONObject update(final JSONObject centroid, final JSONObject changes) {
        String resourceId = (String) centroid.get("resource");
        return update(resourceId, changes.toJSONString());
    }

    /**
     * Deletes a centroid.
     * 
     * DELETE
     * /andromeda/centroid/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1
     * 
     * @param centroidId
     *            a unique identifier in the form centroid/id where id is a
     *            string of 24 alpha-numeric chars.
     * 
     */
    public JSONObject delete(final String centroidId) {
        if (centroidId == null || centroidId.length() == 0
                || !centroidId.matches(CENTROID_RE)) {
            logger.info("Wrong centroid id");
            return null;
        }
        return deleteResource(BIGML_URL + centroidId);
    }

    /**
     * Deletes a centroid.
     * 
     * DELETE
     * /andromeda/centroid/id?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1
     * 
     * @param centroid
     *            an centroid JSONObject.
     * 
     */
    public JSONObject delete(final JSONObject centroid) {
        String resourceId = (String) centroid.get("resource");
        return delete(resourceId);
    }

}