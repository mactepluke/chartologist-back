package com.syngleton.chartomancy.devtools;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import com.syngleton.chartomancy.data.GenericData;
import com.syngleton.chartomancy.model.User;
import com.syngleton.chartomancy.model.patterns.PatternSettingsDTO;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import com.syngleton.chartomancy.service.dataloading.DataService;
import com.syngleton.chartomancy.service.patterns.PatternFactory;
import com.syngleton.chartomancy.service.patterns.PatternSettings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Scanner;

@Log4j2
public class InteractiveShell implements Runnable {

    private final DataController dataController;
    private final PatternController patternController;
    private final User devToolsUser;
    private static final String UNSUPPORTED_OPTION = "Unsupported option. Please enter a number corresponding to the provided menu.";

    public InteractiveShell(DataController dataController,
                            PatternController patternController,
                            User devToolsUser) {
        this.dataController = dataController;
        this.patternController = patternController;
        this.devToolsUser = devToolsUser;
    }

    @Override
    public void run() {
        log.info("*** SHELL LAUNCHED ***");
        loadInterface();
    }

    private void loadInterface() {
        boolean continueApp = true;

        while (continueApp) {
            loadMenu();
            int option = readSelection();
            switch (option) {
                case 1 -> devToolsUser.getGenericData().setGraph(dataController.load("./data/Bitfinex_BTCUSD_d.csv").getBody());
                case 2 -> log.info(dataController.analyse());
                case 3 -> dataController.printGraph(devToolsUser.getGenericData().getGraph());
                case 4 -> createMenu(PatternTypes.BASIC);
                case 5 -> createMenu(PatternTypes.PREDICTIVE);
                case 6 -> patternController.printPatterns(devToolsUser.getGenericData().getPatterns());
                case 9 -> {
                    log.info("*** EXITING PROGRAM ***");
                    continueApp = false;
                }
                default -> log.info(UNSUPPORTED_OPTION);
            }
        }
    }

    private void loadMenu() {
        log.info("*** PLEASE SELECT AN OPTION ***");
        log.info("1 Load data file");
        log.info("2 Analyse loaded data");
        log.info("3 Print loaded data");
        log.info("4 Create BASIC patterns");
        log.info("5 Create PREDICTIVE patterns");
        log.info("6 Print patterns");
        log.info("9 Exit program");
    }

    private int readSelection() {
        Scanner scan = new Scanner(System.in);
        int input = -1;

        while (input == -1) {
            log.info("Reading input:");
            try {
                input = Integer.parseInt(scan.nextLine());
            } catch (Exception e) {
                log.error("Error reading input. Please enter valid number.");
            }
        }
        return input;
    }

    private void createMenu(PatternTypes chosenType) {
        PatternSettings.Autoconfig chosenConfigStrategy = null;
        int granularity = 0;
        int length = 0;

        boolean choiceMade;

        log.info("*** SELECT A CONFIGURATION STRATEGY ***");
        log.info("1 Use default settings");
        log.info("2 Minimize pattern length and granularity");
        log.info("3 Maximize pattern length and granularity");
        log.info("4 Select pattern length and granularity with safety check");
        log.info("5 Select pattern length and granularity without safety check (!!NOT RECOMMENDED!!");

        choiceMade = false;

        while (!choiceMade) {
            int option = readSelection();
            choiceMade = true;

            switch (option) {
                case 1 -> chosenConfigStrategy = PatternSettings.Autoconfig.USE_DEFAULTS;
                case 2 -> chosenConfigStrategy = PatternSettings.Autoconfig.MINIMIZE;
                case 3 -> chosenConfigStrategy = PatternSettings.Autoconfig.MAXIMIZE;
                case 4 -> {
                    log.info("*** SELECT A GRANULARITY ***");
                    granularity = readSelection();
                    log.info("*** SELECT A LENGTH ***");
                    length = readSelection();
                    chosenConfigStrategy = PatternSettings.Autoconfig.NONE;
                }
                case 5 -> {
                    log.info("*** SELECT A GRANULARITY ***");
                    granularity = readSelection();
                    log.info("*** SELECT A LENGTH ***");
                    length = readSelection();
                    chosenConfigStrategy = PatternSettings.Autoconfig.BYPASS_SAFETY_CHECK;
                }
                default -> {
                    log.info(UNSUPPORTED_OPTION);
                    choiceMade = false;
                }
            }
        }
        devToolsUser.getGenericData().setPatterns(
                patternController.create(new PatternSettingsDTO(
                                chosenType,
                                chosenConfigStrategy,
                                granularity,
                                length,
                                "Interactive Shell"),
                        devToolsUser
                ).getBody());
    }

}