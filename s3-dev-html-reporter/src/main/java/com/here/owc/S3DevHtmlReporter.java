package com.here.owc;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.here.owc.model.*;
import com.here.owc.service.EmrJobMessageService;
import com.here.owc.utils.EmrJobMessageConverter;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class S3DevHtmlReporter {

    static final Logger logger = LoggerFactory.getLogger(S3DevHtmlReporter.class);

    @Autowired
    EmrJobMessageService emrJobMessageService;

    private @Value("${s3-report-root-path}")
    String s3ReportRootPath;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    AmazonS3 s3Client;

    public void run(EmrJobExecution emrJobExecution) {
        List<EmrJobMessage> emrJobMessages = emrJobMessageService.loadByExecutionId(emrJobExecution);
        List<EmrMessage> emrMessages = EmrJobMessageConverter.convertFrom(emrJobMessages);

        cleanS3ReportDir(emrJobExecution);

        List<EmrMessage> errorMessages = filterErrorMessages(emrMessages);
        createErrorReport(emrJobExecution, errorMessages);

        List<EmrMessage> infoMessages = filterInfoMessages(emrMessages);
        createInfoReport(emrJobExecution, infoMessages);

        createMainPageOfReport(emrJobExecution, errorMessages, infoMessages);

    }

    private void cleanS3ReportDir(EmrJobExecution emrJobExecution) {
        AmazonS3URI s3URI = new AmazonS3URI(
                s3ReportRootPath + "/" + emrJobExecution.getClusterId());

        for (S3ObjectSummary file : s3Client.listObjects(s3URI.getBucket(), s3URI.getKey()).getObjectSummaries()) {
            s3Client.deleteObject(s3URI.getBucket(), file.getKey());
        }
    }

    private List<EmrMessage> filterErrorMessages(List<EmrMessage> emrMessages) {
        return emrMessages.stream().filter(m -> m instanceof EmrErrorMessage).collect(
                Collectors.toList());
    }

    private List<EmrMessage> filterInfoMessages(List<EmrMessage> emrMessages) {
        return emrMessages.stream().filter(m -> m instanceof EmrInfoMessage).collect(
                Collectors.toList());
    }

    private void createMainPageOfReport(EmrJobExecution emrJobExecution,
            List<EmrMessage> errorMessages, List<EmrMessage> infoMessages) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("errorCount", errorMessages.size());
        context.setVariable("infoCount", infoMessages.size());
        context.setVariable("emrJobExecution", emrJobExecution);

        createAndUploadPage(emrJobExecution, context, "index", "");
    }

    private void createInfoReport(EmrJobExecution emrJobExecution, List<EmrMessage> infoMessages) {
        List<List<EmrMessage>> errorMessagesChunks = ListUtils.partition(infoMessages, 50);

        createPaginationPages(emrJobExecution, errorMessagesChunks, "emrInfoMessages", "info-report");
    }

    private void createErrorReport(EmrJobExecution emrJobExecution, List<EmrMessage> errorMessages) {
        List<List<EmrMessage>> errorMessagesChunks = ListUtils.partition(errorMessages, 50);

        createPaginationPages(emrJobExecution, errorMessagesChunks, "emrErrorMessages", "error-report");
    }

    private void createPaginationPages(EmrJobExecution emrJobExecution, List<List<EmrMessage>> errorMessagesChunks,
            String emrErrorMessages, String template) {
        for (int i = 0; i < errorMessagesChunks.size(); i++) {

            List<EmrMessage> emrMessage = errorMessagesChunks.get(i);
            Context context = new Context(Locale.getDefault());
            context.setVariable("pages", errorMessagesChunks.size() - 1);
            context.setVariable("page", i);
            context.setVariable(emrErrorMessages, emrMessage);
            String pageNumber = "-" + String.valueOf(i);
            createAndUploadPage(emrJobExecution, context, template, pageNumber);
        }
    }

    private void createAndUploadPage(EmrJobExecution emrJobExecution, Context iContext, String template,
            String pageNumber) {

        final String result = templateEngine.process(template, iContext);
        AmazonS3URI s3URI = new AmazonS3URI(
                s3ReportRootPath + "/" + emrJobExecution.getClusterId() + "/" + template + pageNumber + ".html");

        ObjectMetadata md = new ObjectMetadata();

        InputStream myInputStream = new ByteArrayInputStream(result.getBytes());
        md.setContentType("text/html");
        md.setContentEncoding("UTF-8");

        s3Client.putObject(new PutObjectRequest(s3URI.getBucket(), s3URI.getKey(), myInputStream, md));
        logger.info("You report is available at: {}", s3URI.toString());
    }

}
