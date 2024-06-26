package io.github.maccoycookies.mccache;

/**
 * mc cache plugin
 */
public interface McPlugin {

    void init();

    void startup();

    void shutdown();

}
