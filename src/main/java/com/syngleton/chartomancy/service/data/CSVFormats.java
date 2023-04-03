package com.syngleton.chartomancy.service.data;

public enum CSVFormats {
    CRYPTO_DATA_DOWNLOAD(
            "unix,date,symbol,open,high,low,close,Volume ...,Volume ...",
            ",",
            0,
            2,
            3,
            4,
            5,
            6,
            7
    );

    public final String formatHeader;
    public final String delimiter;
    public final int unixPosition;
    public final int symbolPosition;
    public final int openPosition;
    public final int highPosition;
    public final int lowPosition;
    public final int closePosition;
    public final int volumePosition;


    CSVFormats(String formatHeader,
               String delimiter,
               int unixPosition,
               int symbolPosition,
               int openPosition,
               int highPosition,
               int lowPosition,
               int closePosition,
               int volumePosition
    ) {
        this.formatHeader = formatHeader;
        this.delimiter = delimiter;
        this.unixPosition = unixPosition;
        this.symbolPosition = symbolPosition;
        this.openPosition = openPosition;
        this.highPosition = highPosition;
        this.lowPosition = lowPosition;
        this.closePosition = closePosition;
        this.volumePosition = volumePosition;
    }

}
