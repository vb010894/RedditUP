package com.vbsoft.redditup.service;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.vbsoft.redditup.persistence.LogModel;
import com.vbsoft.redditup.persistence.RedditUser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.codeborne.selenide.Selenide.*;

/**
 * Upvote action class.
 *
 * @author Vboy
 */
@Service
@PropertySource({"classpath:up.properties", "classpath:src.properties"})
public class Upvoter {

    /**
     * Login page URL.
     */
    @Value("${search.login.url}")
    private String loginPage;

    /**
     * Submit button XPATH.
     */
    @Value("${search.submit}")
    private String submitXPATH;

    /**
     * Username XPATH.
     */
    @Value("${search.username}")
    private String usernameXPATH;

    /**
     * Password XPATH.
     */
    @Value("${search.password}")
    private String passwordXPATH;

    /**
     * Adult gate XPATH.
     */
    @Value("${search.gate}")
    private String gateXPATH;

    /**
     * Upvote counter XPATH.
     */
    @Value("${search.counter}")
    private String counterXPATH;

    /**
     * Interest window XPATH.
     */
    @Value("${search.interest}")
    private String interestXPATH;

    /**
     * Upvote button XPATH.
     */
    @Value("${search.upvote}")
    private String upvoteXPATH;

    /**
     * Subscribe XPATH.
     */
    @Value("${search.subscribe}")
    private String subscribeXPATH;

    /**
     * Each operation timeout.
     */
    @Value("${upvoter.operation.timeout}")
    private int operationTimeout;

    /**
     * Iteration timeout from.
     */
    @Value("${upvoter.request.timeout.max}")
    private int requestTimeoutMAX;

    /**
     * Iteration timeout to.
     */
    @Value("${upvoter.request.timeout.min}")
    private int requestTimeoutMIN;

    /**
     * Proxy host.
     */
    @Value("${upvoter.proxy.host}")
    private String proxyHost;

    /**
     * Proxy port.
     */
    @Value("${upvoter.proxy.port}")
    private String proxyPort;

    /**
     * User under actions.
     */
    private final RedditUserService user;


    /**
     * Log data.
     */
    private final List<LogModel> logData = new LinkedList<>();

    /**
     * Constructor with user.
     *
     * @param user Current user
     */
    @Autowired
    public Upvoter(RedditUserService user) {
        this.user = user;
    }


    /**
     * Add ACTION to log.
     * @param message ACTION message
     */
    private void addAction(String message) {
        LogModel model = new LogModel();
        model.setDescription(message);
        model.setSuccess(true);
        model.setLogRef("ACTION");
        this.logData.add(model);
    }

    /**
     * Add ERROR to log.
     * @param message Error message
     */
    private void addError(String message) {
        LogModel model = new LogModel();
        model.setDescription(message);
        model.setSuccess(true);
        model.setLogRef("ERROR");
        this.logData.add(model);
    }

    /**
     * Add RESULT to log.
     * @param upCount Upvote count
     */
    private void addResult(int upCount) {
        LogModel model = new LogModel();
        model.setDescription("Task done");
        model.setSuccess(true);
        model.setLogRef("ERROR");
        model.setUpCount(upCount);
        this.logData.add(model);
    }

    /**
     * Main upvote method.
     *
     * @param POSTS Target posts
     * @return List of  runtime throwable
     */
    public List<Throwable> upvote(final List<String> POSTS) {
        List<Throwable> throwsEx = new ArrayList<>();
        this.user
                .getUsers()
                .stream().filter(RedditUser::isEnabled)
                .forEach(usr -> {
                    this.addAction(String.format(
                            "Login action \n User - '%s' \n password - '%s' \n proxy - %s",
                            usr.getUsername(),
                            usr.getPassword(),
                            this.proxyHost +  ":" + this.proxyPort
                    ));

                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--disable-notifications --proxy-server=" + this.proxyHost +  ":" + this.proxyPort);
                    Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

                    try {
                        open(this.loginPage);
                        $(By.xpath(this.usernameXPATH)).shouldBe(Condition.exist).setValue(usr.getUsername());
                        $(By.xpath(this.passwordXPATH)).shouldBe(Condition.exist).setValue(usr.getPassword());
                        $(By.xpath(this.submitXPATH)).submit();
                    } catch (Exception e) {
                        this.addError(e.getMessage());
                    }

                    POSTS.forEach(POST -> {
                        try {
                            this.addAction(String.format(
                                    "work with - '%s'",
                                    POST));
                            upvotePost(POST);
                        } catch (Exception e) {
                            this.addError(String.format(
                                    "message - '%s'",
                                    e.getMessage()
                            ));
                        }
                    });

                    closeWindow();
                    closeWebDriver();
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextLong(this.requestTimeoutMIN, this.requestTimeoutMAX));
                    } catch (InterruptedException e) {
                        this.addError(String.format(
                                "message - '%s'",
                                e.getMessage()
                        ));
                    }
                });

        try {
            this.saveLog();
        } catch (IOException e) {
            throwsEx.add(e);
        }

        return throwsEx;
    }

    private void saveLog() throws IOException {
        File logOutFile = new File("reddit/logs/" + new SimpleDateFormat("yyyy-dd-mm-hh-mm-ss").format(new Date()) + ".json");
        if(!logOutFile.exists())
            if(!logOutFile.createNewFile())
                throw new IOException("Log file wasn't created. File - " + logOutFile.getAbsolutePath());

        ObjectMapper mapper = new JsonMapper();
        mapper.writeValue(logOutFile, this.logData);
        this.logData.clear();
    }

    /**
     * Upvote single post.
     *
     * @param POST Current post
     * @throws InterruptedException when driver's action has problem.
     */
    private void upvotePost(final String POST) throws InterruptedException {
        int upCount = 0;
        Thread.sleep(this.operationTimeout);
        open(POST);
        Thread.sleep(this.operationTimeout);
        try {
            var gate = $(By.xpath(this.gateXPATH));
            if(gate.exists())
                gate.click();

            Thread.sleep(this.operationTimeout);

            var counter = $(By.xpath(this.counterXPATH));
            if(counter.exists())
                upCount = Integer.parseInt(counter.getText());

            Thread.sleep(this.operationTimeout);

            var interest = $(By.xpath(this.interestXPATH));
            if(interest.exists())
                interest.click();

            Thread.sleep(this.operationTimeout);

            SelenideElement up = $(By.xpath(this.upvoteXPATH));
            if (up.exists())
                if (Objects.equals(up.getAttribute("aria-pressed"), "false"))
                    up.click();

            SelenideElement subscribes = $(By.xpath(this.subscribeXPATH));
            if (subscribes.exists())
                subscribes.click();
        } catch (InvalidStateException ex) {
            this.addError(ex.getMessage());
        }

        Thread.sleep(this.operationTimeout);
        this.addResult(upCount);
    }
}
