/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

import weka.core.Instance;
import weka.core.Instances;
import weka.clusterers.SimpleKMeans;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import objectStructures.User;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.logging.Level;
import processes.LogFactory;
import processes.GlobalParam;
/**
 *
 * @author Sajith
 */
public class DataClusterHandler {

    private static final Logger LOGGER = LogFactory.getNewLogger(DataClusterHandler.class.getName());

    private SimpleKMeans dataGraph;
    private Instances userPoints;
    private boolean clusterInitiated = false;
    private String[] clusters;

    public String[] getRelatedUsers(User user) {
        Instance userInstance = toInstance(user, userPoints);
        int clusterNumber = getRelativeCluster(userInstance);
        String[] userIDs = clusters[clusterNumber].split(",");
        return userIDs;
    }

    public void buildGraph(String filePath) {

        LOGGER.log(Level.WARNING, "May throw exception for poorly formated data files");
        try {
            userPoints = new Instances(getDataFile(filePath));
            int numberOfClusters = clusterCount(userPoints);
            int maxSeedAmount = seedAmount(userPoints);
            initiateClusters(maxSeedAmount, numberOfClusters);
            if (clusterInitiated) {
                LOGGER.log(Level.WARNING, "May throw exeption for instances type");
                try {
                    dataGraph.buildClusterer(userPoints);
                    saveClusterInforamtion();
                    LOGGER.log(Level.INFO, "Clustering was completed");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error@ClusterProcess_initiateCluster", e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error@ClusterProcess_initiateCluster", e);
        }

    }

    private void initiateClusters(int randomSeedMax, int clusterCount) {
        if (!clusterInitiated) {
            dataGraph = new SimpleKMeans();
            dataGraph.setPreserveInstancesOrder(true);
            dataGraph.setSeed(randomSeedMax);

            LOGGER.log(Level.WARNING, "May throw exception if the cluster count is a negative value");
            try {
                dataGraph.setNumClusters(clusterCount);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error@ClusterProcess_initiateCluster", e);
                clusterCount = ((-1) * clusterCount) + 1;
                initiateClusters(randomSeedMax, clusterCount);
            }
            clusterInitiated = true;
        }
    }

    private void saveClusterInforamtion() {
        this.clusters = new String[dataGraph.getNumClusters()];

        for (int index = 0; index < userPoints.numInstances(); index++) {
            Instance i = userPoints.instance(index);
            int clusterID = getRelativeCluster(i);
            if (clusters[clusterID] == null) {
                clusters[clusterID] = "" + (int) i.value(0);
            } else {
                clusters[clusterID] = clusters[clusterID] + "," + (int) i.value(0);
            }
        }
    }

    private int getRelativeCluster(Instance targetUserData) {
        int clusterID = 0;
        try {
            clusterID = dataGraph.clusterInstance(targetUserData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error@ClusterProcess_getRelativePoints", e);
        }
        return clusterID;
    }

    private BufferedReader getDataFile(String filePath) {
        BufferedReader inputReader = null;

        LOGGER.log(Level.WARNING, "May throw file not found exception for given path " + filePath);
        try {
            inputReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error@ClusterProcess_getDataFile", e);
        }
        return inputReader;
    }

    private Instance toInstance(User user, Instances dataSet) {
        Instance tempInstance = new Instance(userPoints.numAttributes());
        tempInstance.setDataset(userPoints);
        String userDataString = user.getUserID() + user.getTasteString(LastFMDataHandler.getTagCount());
        String[] dataArray = userDataString.split(",");
        for (int index = 0; index < dataArray.length; index++) {
            tempInstance.setValue(index, Integer.parseInt(dataArray[index]));
        }
        return tempInstance;
    }

    private int seedAmount(Instances dataPoints) {
        return dataPoints.numInstances();
    }

    private int clusterCount(Instances dataPoints) {
        int totalDataCount = dataPoints.numInstances();
        return (int) totalDataCount / 1;
    }

}
