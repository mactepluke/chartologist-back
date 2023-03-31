package com.syngleton.chartomancy.view;

import com.syngleton.chartomancy.controller.DataController;
import com.syngleton.chartomancy.controller.PatternController;
import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Log4j2
public class InteractiveShell implements Runnable {

    private final DataController dataController;
    private final PatternController patternController;

    public InteractiveShell(DataController dataController,
                            PatternController patternController) {
        this.dataController = dataController;
        this.patternController = patternController;
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
                case 1 -> {
                    log.info(dataController.load("./data/Bitfinex_BTCUSD_d.csv"));
                }
                case 2 -> {
                    log.info(dataController.analyse());
                }
                case 3 -> {
                    dataController.printGraph();
                }
                case 4 -> {
                    patternController.create();
                }
                case 9 -> {
                    log.info("*** EXITING PROGRAM ***");
                    continueApp = false;
                }
                default -> log.info("Unsupported option. Please enter a number corresponding to the provided menu.");
            }
        }
    }

    private void loadMenu() {
        log.info("*** PLEASE SELECT AN OPTION ***");
        log.info("1 Load data file");
        log.info("2 Analyse loaded data");
        log.info("3 Print loaded data");
        log.info("4 Create basic pattern");
        log.info("9 Exit program");
    }

    public int readSelection() {
        Scanner scan = new Scanner(System.in);
        int input = -1;

        try {
            input = Integer.parseInt(scan.nextLine());
        } catch (Exception e) {
            log.error("Error reading input. Please enter valid number.");
        }
        return input;
    }
}
