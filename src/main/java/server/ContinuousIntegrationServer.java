package server;

import buildtools.Storage;
import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

/**
 Skeleton of a server.ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer {


    // prints output, errors and exit value of bash executions

    private static final Logger logger = LoggerFactory.getLogger(ContinuousIntegrationServer.class);
    public static Storage storage = new Storage();

    public static void main(String[] args) {

        Server server = new Server(8018);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "server/resources");


        //printBash(commands);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            logger.error("Error occurred while starting Jetty", ex);
            System.exit(1);
        }

        finally {
            server.destroy();
        }
    }
}
