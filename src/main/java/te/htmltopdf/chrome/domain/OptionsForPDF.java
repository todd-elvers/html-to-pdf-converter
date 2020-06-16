package te.htmltopdf.chrome.domain;

/**
 * The options Chrome accepts for printing.
 */
public class OptionsForPDF {

    private final Boolean landscape;
    private final Boolean displayHeaderFooter;
    private final Boolean printBackground;
    private final Boolean ignoreInvalidPageRanges;
    private final Boolean preferCSSPageSize;

    private final Double scale;
    private final Double paperWidth;
    private final Double paperHeight;
    private final Double marginTop;
    private final Double marginBottom;
    private final Double marginLeft;
    private final Double marginRight;

    private final String pageRanges;
    private final String headerTemplate;
    private final String footerTemplate;

    // Uses builder pattern
    private OptionsForPDF(
            Boolean landscape,
            Boolean displayHeaderFooter,
            Boolean printBackground,
            Boolean ignoreInvalidPageRanges,
            Boolean preferCSSPageSize,
            Double scale,
            Double paperWidth,
            Double paperHeight,
            Double marginTop,
            Double marginBottom,
            Double marginLeft,
            Double marginRight,
            String pageRanges,
            String headerTemplate,
            String footerTemplate
    ) {
        this.landscape = landscape;
        this.displayHeaderFooter = displayHeaderFooter;
        this.printBackground = printBackground;
        this.ignoreInvalidPageRanges = ignoreInvalidPageRanges;
        this.preferCSSPageSize = preferCSSPageSize;
        this.scale = scale;
        this.paperWidth = paperWidth;
        this.paperHeight = paperHeight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.pageRanges = pageRanges;
        this.headerTemplate = headerTemplate;
        this.footerTemplate = footerTemplate;
    }

    public Boolean getLandscape() {
        return landscape;
    }

    public Boolean getDisplayHeaderFooter() {
        return displayHeaderFooter;
    }

    public Boolean getPrintBackground() {
        return printBackground;
    }

    public Boolean getIgnoreInvalidPageRanges() {
        return ignoreInvalidPageRanges;
    }

    public Boolean getPreferCSSPageSize() {
        return preferCSSPageSize;
    }

    public Double getScale() {
        return scale;
    }

    public Double getPaperWidth() {
        return paperWidth;
    }

    public Double getPaperHeight() {
        return paperHeight;
    }

    public Double getMarginTop() {
        return marginTop;
    }

    public Double getMarginBottom() {
        return marginBottom;
    }

    public Double getMarginLeft() {
        return marginLeft;
    }

    public Double getMarginRight() {
        return marginRight;
    }

    public String getPageRanges() {
        return pageRanges;
    }

    public String getHeaderTemplate() {
        return headerTemplate;
    }

    public String getFooterTemplate() {
        return footerTemplate;
    }

    public static class Builder {
        private Boolean landscape;
        private Boolean displayHeaderFooter;
        private Boolean printBackground;
        private Boolean ignoreInvalidPageRanges;
        private Boolean preferCSSPageSize;
        private Double scale;
        private Double paperWidth;
        private Double paperHeight;
        private Double marginTop;
        private Double marginBottom;
        private Double marginLeft;
        private Double marginRight;
        private String pageRanges;
        private String headerTemplate;
        private String footerTemplate;

        /**
         * Creates a new {@link Builder} instance from an existing instance, copying
         * all properties along the way.
         *
         * @param builder the {@link Builder} instance to copy
         * @return the copy of the {@link Builder} instance
         */
        public static Builder fromBuilder(Builder builder) {
            return new Builder()
                    .setLandscape(builder.landscape)
                    .setDisplayHeaderFooter(builder.displayHeaderFooter)
                    .setPrintBackground(builder.printBackground)
                    .setIgnoreInvalidPageRanges(builder.ignoreInvalidPageRanges)
                    .setPreferCSSPageSize(builder.preferCSSPageSize)
                    .setScale(builder.scale)
                    .setPaperWidth(builder.paperWidth)
                    .setPaperHeight(builder.paperHeight)
                    .setMarginTop(builder.marginTop)
                    .setMarginBottom(builder.marginBottom)
                    .setMarginLeft(builder.marginLeft)
                    .setMarginRight(builder.marginRight)
                    .setPageRanges(builder.pageRanges)
                    .setHeaderTemplate(builder.headerTemplate)
                    .setFooterTemplate(builder.footerTemplate);
        }

        public Builder setLandscape(Boolean landscape) {
            this.landscape = landscape;
            return this;
        }

        public Builder setDisplayHeaderFooter(Boolean displayHeaderFooter) {
            this.displayHeaderFooter = displayHeaderFooter;
            return this;
        }

        public Builder setPrintBackground(Boolean printBackground) {
            this.printBackground = printBackground;
            return this;
        }

        public Builder setIgnoreInvalidPageRanges(Boolean ignoreInvalidPageRanges) {
            this.ignoreInvalidPageRanges = ignoreInvalidPageRanges;
            return this;
        }

        public Builder setPreferCSSPageSize(Boolean preferCSSPageSize) {
            this.preferCSSPageSize = preferCSSPageSize;
            return this;
        }

        public Builder setScale(Double scale) {
            this.scale = scale;
            return this;
        }

        public Builder setPaperWidth(Double paperWidth) {
            this.paperWidth = paperWidth;
            return this;
        }

        public Builder setPaperHeight(Double paperHeight) {
            this.paperHeight = paperHeight;
            return this;
        }

        public Builder setMarginTop(Double marginTop) {
            this.marginTop = marginTop;
            return this;
        }

        public Builder setMarginBottom(Double marginBottom) {
            this.marginBottom = marginBottom;
            return this;
        }

        public Builder setMarginLeft(Double marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder setMarginRight(Double marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        public Builder setPageRanges(String pageRanges) {
            this.pageRanges = pageRanges;
            return this;
        }

        public Builder setHeaderTemplate(String headerTemplate) {
            this.headerTemplate = headerTemplate;
            return this;
        }

        public Builder setFooterTemplate(String footerTemplate) {
            this.footerTemplate = footerTemplate;
            return this;
        }

        /**
         * @return an immutable {@link OptionsForPDF} instance with all the properties that
         * were set on the {@link Builder} instance.
         */
        public OptionsForPDF build() {
            return new OptionsForPDF(
                    landscape,
                    displayHeaderFooter,
                    printBackground,
                    ignoreInvalidPageRanges,
                    preferCSSPageSize,
                    scale,
                    paperWidth,
                    paperHeight,
                    marginTop,
                    marginBottom,
                    marginLeft,
                    marginRight,
                    pageRanges,
                    headerTemplate,
                    footerTemplate
            );
        }
    }
}