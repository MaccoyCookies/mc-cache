package io.github.maccoycookies.mccache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * plugins entrypoint
 */
@Component
public class McApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    List<McPlugin> plugins;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent applicationReadyEvent) {
            for (McPlugin plugin : plugins) {
                plugin.init();
                plugin.startup();
            }
        } else if (event instanceof ContextClosedEvent contextClosedEvent) {
            for (McPlugin plugin : plugins) {
                plugin.shutdown();
            }
        }
    }
}
