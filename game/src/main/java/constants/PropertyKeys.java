package constants;

import engine.parser.tokenization.Token;

public enum PropertyKeys {

    INHERITED_CONTAINERS    ("inherited"),
    SOLUTIONS               ("solutions"),
    KNOWLEDGE               ("knowledge"),
    DRIVES                  ("drives"),

    NAME                    ("name"),
    PLOT                    ("plot"),
    RUN_TICK_SCRIPT         ("runTicks"),
    TICKS                   ("ticks"),
    MESH                    ("mesh"),
    OPACITY                 ("opacity"),
    PREFERS                 ("plot"),

    TEMPLATE                ("template"),
    LAYOUT                  ("layout"),
    TEXT                    ("text"),

    TOP_COLOR               ("topColor"),
    TOP_COLOR_DEVIATION     ("topColorVariance"),
    SIDE_COLOR              ("sideColor"),
    SIDE_COLOR_DEVIATION    ("sideColorVariance"),
    PREFERRED_HEIGHT        ("preferredHeight"),
    PREFERRED_HEIGHT_BLUR   ("preferredHeightBlur"),

    ;

    // ###################################################################################
    // ################################ Functionality ####################################
    // ###################################################################################

    private String key;

    PropertyKeys(String key) {
        this.key = key;
    }

    public boolean equals(Token token) {
        return this.key.equals(token.getValue());
    }

    public String key() {
        return key;
    }
}
