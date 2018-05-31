package com.here.owc;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.*;
import com.amazonaws.services.cloudformation.model.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmrJobReportingDeploy {

    public static String stackName;
    private static final Logger logger = LoggerFactory.getLogger(EmrJobReportingDeploy.class);

    enum Action {
        CREATE,
        DELETE,
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2 && args.length == 0) {
            throw new IllegalArgumentException(
                    "Input args can't be null. Must be <create> <stackName> <s3_deploy_path> | <delete> <stackName>");
        }

        Action action = Action.valueOf(args[0].toUpperCase());
        stackName = args[1];

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        AmazonCloudFormation stackbuilder = AmazonCloudFormationClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_EAST_1)
                .build();

        logger.info("===========================================");
        logger.info("Getting Started with AWS CloudFormation");
        logger.info("===========================================\n");

        try {
            if (Action.CREATE.equals(action)) {
                if (args.length == 4 && !args[3].isEmpty()) {
                    String s3TargetPath = args[3];
                    S3Uploader s3Uploader = new S3Uploader(credentialsProvider);
                    String currentDir = System.getProperty("user.dir");

                    List<String> excludeList = Arrays.asList("emr-job-reporting-assembly");
                    s3Uploader.uploadRecursive(s3TargetPath, currentDir,
                            excludeList);
                }

                // Create a stack
                CreateStackRequest createRequest = new CreateStackRequest();
                List<Parameter> parameters = new ArrayList<>();
                if (args.length >= 3) {
                    String parametersFilePath = args[2];
                    logger.info("Loading parameters from {}", parametersFilePath);
                    parameters = loadParameters(parametersFilePath);
                }
                createRequest.setParameters(parameters);
                createRequest.setStackName(stackName);
                InputStream resourceAsStream = EmrJobReportingDeploy.class.getClassLoader().getResourceAsStream(
                        "aws/" + stackName + ".yaml");
                createRequest.setTemplateBody(convertStreamToString(
                        resourceAsStream));
                logger.info("Creating a stack called {}.", createRequest.getStackName());
                stackbuilder.createStack(createRequest);

                // Wait for stack to be created
                // Note that you could use SNS notifications on the CreateStack call to track the progress of the stack creation
                logger.info(
                        "Stack creation completed, the stack {} completed with {}", stackName, waitForCompletion(
                                stackbuilder, stackName));
            } else if (Action.DELETE.equals(action)) {

                // Delete the stack
                DeleteStackRequest deleteRequest = new DeleteStackRequest();
                deleteRequest.setStackName(stackName);
                logger.info("Deleting the stack called {}.", deleteRequest.getStackName());
                stackbuilder.deleteStack(deleteRequest);

                // Wait for stack to be deleted
                // Note that you could used SNS notifications on the original CreateStack call to track the progress of the stack deletion
                logger.info(
                        "Stack creation completed, the stack {} completed with ", stackName,
                        waitForCompletion(stackbuilder, stackName));
            }
        } catch (AmazonServiceException ase) {
            logger.info("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS CloudFormation, but was rejected with an error response for some reason.");
            logger.info("Error Message:    {}", ase.getMessage());
            logger.info("HTTP Status Code: {}", ase.getStatusCode());
            logger.info("AWS Error Code:   {}", ase.getErrorCode());
            logger.info("Error Type:       {}", ase.getErrorType());
            logger.info("Request ID:       {}", ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS CloudFormation, "
                    + "such as not being able to access the network.");
            logger.info("Error Message: {}", ace.getMessage());
        }
    }

    private static List<Parameter> loadParameters(String parametersFilePath) throws IOException {
        try (InputStream propertiesFileStream = new FileInputStream(parametersFilePath)) {
            Properties properties = new Properties();
            properties.load(propertiesFileStream);
            return properties.entrySet().stream().map(e -> {
                Parameter parameter = new Parameter();
                parameter.setParameterKey((String) e.getKey());
                parameter.setParameterValue(String.valueOf(e.getValue()));
                logger.info("Loading parameter key: {} value: {} ", e.getKey(), e.getValue());
                return parameter;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.EMPTY_LIST;
    }

    // Convert a stream into a single, newline separated string
    public static String convertStreamToString(InputStream in) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringbuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringbuilder.append(line + "\n");
        }
        in.close();
        return stringbuilder.toString();
    }

    // Wait for a stack to complete transitioning
    // End stack states are:
    //    CREATE_COMPLETE
    //    CREATE_FAILED
    //    DELETE_FAILED
    //    ROLLBACK_FAILED
    // OR the stack no longer exists
    public static String waitForCompletion(AmazonCloudFormation stackbuilder, String stackName) throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(stackName);
        Boolean completed = false;
        String stackStatus = "Unknown";
        String stackReason = "";

        logger.info("Waiting");

        while (!completed) {
            List<Stack> stacks = null;
            try {
                stacks = stackbuilder.describeStacks(wait).getStacks();
            } catch (Exception e) {

            }
            if (stacks == null || stacks.isEmpty()) {
                completed = true;
                stackStatus = "NO_SUCH_STACK";
                stackReason = "Stack has been deleted";
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString()) ||
                            stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString())) {
                        completed = true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                    }
                }
            }

            // Not done yet so sleep for 10 seconds.
            if (!completed) {
                Thread.sleep(10000);
            }
        }

        // Show we are done
        logger.info("done\n");

        return stackStatus + " (" + stackReason + ")";
    }

}
