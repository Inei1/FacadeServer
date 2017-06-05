/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.web;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.LoggingContext;
import org.terasology.engine.paths.PathManager;
import org.terasology.engine.subsystem.common.ConfigurationSubsystem;
import org.terasology.web.io.GsonMessageBodyHandler;
import org.terasology.web.servlet.AboutServlet;
import org.terasology.web.servlet.LogServlet;
import org.terasology.web.servlet.WsEventServlet;


/**
 */
public final class ServerMain {

    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

    private static final String ARG_ENGINE_DIR = "-homedir=";
    private static final String ARG_ENGINE_SERVER_PORT = "-serverPort=";

    private ServerMain() {
        // no instances
    }

    public static void main(String[] args) throws Exception {

        handleArgs(args);
        setupLogging();

        String portEnv = System.getenv("PORT");
        if (portEnv == null) {
            portEnv = "8080";
            logger.warn("Environment variable 'PORT' not defined - using default {}", portEnv);
        }
        Integer port = Integer.valueOf(portEnv);

        // this is mostly for I18nMap, but can have an influence on other
        // string formats. Note that metainfo.ftl explicitly sets the locale to
        // define the date format.
        Locale.setDefault(Locale.ENGLISH);

        Server server = createServer(port,
                new LogServlet(),
                new AboutServlet());

        server.start();
        logger.info("Web server started on port {}!", port);

        EngineRunner.runEngine();

        server.join();
    }

    private static void handleArgs(String[] args) {
        Path homePath = Paths.get(""); //use current directory as default
        for (String arg: args) {
            if (arg.startsWith(ARG_ENGINE_DIR)) {
                homePath = Paths.get(arg.substring(ARG_ENGINE_DIR.length()));
            } else if (arg.startsWith(ARG_ENGINE_SERVER_PORT)) {
                System.setProperty(ConfigurationSubsystem.SERVER_PORT_PROPERTY, arg.substring(ARG_ENGINE_SERVER_PORT.length()));
            } else {
                System.err.println("Unrecognized command line argument \"" + arg + "\"");
                System.exit(1);
            }
        }
        try {
            PathManager.getInstance().useOverrideHomePath(homePath);
        } catch (IOException e) {
            System.err.println("Failed to access the engine data directory: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void setupLogging() {
        Path path = PathManager.getInstance().getLogPath();
        if (path == null) {
            path = Paths.get("logs");
        }

        LoggingContext.initialize(path);
    }

    private static Server createServer(int port, Object... annotatedObjects) throws Exception {
        Server server = new Server(port);

        ResourceHandler logFileResourceHandler = new ResourceHandler();
        logFileResourceHandler.setDirectoriesListed(true);
        logFileResourceHandler.setResourceBase("logs");

        ContextHandler logContext = new ContextHandler("/logs"); // the server uri path
        logContext.setHandler(logFileResourceHandler);

        ResourceHandler webResourceHandler = new ResourceHandler();
        webResourceHandler.setDirectoriesListed(false);
        webResourceHandler.setResourceBase("web");

        ContextHandler webContext = new ContextHandler("/");     // the server uri path
        webContext.setHandler(webResourceHandler);

        ResourceConfig rc = new ResourceConfig();
        rc.register(new GsonMessageBodyHandler());               // register JSON serializer
        rc.register(FreemarkerMvcFeature.class);

        for (Object servlet : annotatedObjects) {
            rc.register(servlet);
        }

        ServletContextHandler jerseyContext = new ServletContextHandler(ServletContextHandler.GZIP);
        jerseyContext.setResourceBase("templates");
        jerseyContext.addServlet(new ServletHolder(new ServletContainer(rc)), "/*");
        jerseyContext.addServlet(new ServletHolder(WsEventServlet.class), "/events/*");

        HandlerList handlers = new HandlerList();
        handlers.addHandler(logContext);
        handlers.addHandler(webContext);
        handlers.addHandler(jerseyContext);

        server.setHandler(handlers);

        return server;
    }
}
