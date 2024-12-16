package cn.academy.ability.api;

// Global extenders for scala ability programming.
public class AbilityAPIExt {
    // Context message IDs
    // Note: Scala treats java's string iteral as non-constants, so we have to awkwardly repeat message id here. Shame!

    public static final String MSG_TERMINATED = "i_term";
    public static final String MSG_MADEALIVE = "i_alive";
    public static final String MSG_TICK = "i_tick";

    public static final String MSG_KEYDOWN = "keydown";
    public static final String MSG_KEYUP = "keyup";
    public static final String MSG_KEYABORT = "keyabort";
    public static final String MSG_KEYTICK = "keytick";
}
