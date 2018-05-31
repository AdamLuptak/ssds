package com.here.owc;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Class Upload files to S3.
 */
public class S3Uploader {
    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);
    AtomicReference<AWSCredentialsProvider> credentialsProvider = new AtomicReference<>();

    public S3Uploader(AWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider.set(credentialsProvider);
    }

    public S3Uploader(String awsAccessKey, String awsSecretKey) {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        this.credentialsProvider.set(new AWSStaticCredentialsProvider(
                basicAWSCredentials));

    }

    /**
     * Upload multiples files to s3.
     * @param s3TargetPath
     * @param sourcePaths
     * @throws S3UploaderException
     */
    public void upload(String s3TargetPath, List<String> sourcePaths) throws S3UploaderException {
        try {
            CompletableFuture<Void> allFuturesDone = uploadAsync(s3TargetPath, sourcePaths);
            allFuturesDone.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new S3UploaderException(e);
        }
    }

    /**
     * Upload all files to s3 preserves the folder structure.
     * @param s3TargetPath
     * @param sourcePath
     * @param excludeList
     * @throws S3UploaderException
     */
    public void uploadRecursive(String s3TargetPath, String sourcePath, List<String> excludeList)
            throws S3UploaderException {
        try {
            List<String> sourcePaths = Files.walk(
                    Paths.get(sourcePath))
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        // exclude files from exclude list
                        String fileName = p.getFileName().toString();
                        return excludeList.stream().noneMatch(fileName::startsWith);
                    })
                    .map(f -> getRelativePath(sourcePath, f))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNullOrEmpty(sourcePaths)) {
                String message = String.format("SourcePath: %s is empty", sourcePath);
                throw new S3UploaderException(message);
            }

            CompletableFuture<Void> allFuturesDone = uploadAsync(s3TargetPath, sourcePaths);
            allFuturesDone.get();
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new S3UploaderException(e);
        }
    }

    private String getRelativePath(String sourcePath, Path f) {
        return Paths.get(sourcePath).relativize(f.toAbsolutePath()).toString().replaceAll("\\\\", "/");
    }

    /**
     * Upload files async return CompletableFuture of CompletableFutures.
     * @param s3TargetPath
     * @param sourcePaths
     * @return CompletableFuture of CompletableFutures
     */
    public CompletableFuture<Void> uploadAsync(String s3TargetPath, List<String> sourcePaths) {
        List<CompletableFuture> completableFutureList = sourcePaths.stream().map(
                p -> CompletableFuture.runAsync(() -> uploadFileToS3(s3TargetPath, p)))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]));
    }

    private void uploadFileToS3(String s3TargetPath, String sourcePath) {
        logger.info("Uploading to s3: {} file: {}", s3TargetPath, sourcePath);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider.get()).withRegion(
                Regions.US_EAST_1).build();
        AmazonS3URI s3URI = new AmazonS3URI(s3TargetPath + "/" + sourcePath);
        s3Client.putObject(s3URI.getBucket(), s3URI.getKey(), new File(sourcePath));
    }
}
