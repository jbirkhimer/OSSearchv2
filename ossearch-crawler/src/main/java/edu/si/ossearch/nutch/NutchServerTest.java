package edu.si.ossearch.nutch;

import com.google.common.collect.Queues;
import org.apache.nutch.fetcher.FetchNodeDb;
import org.apache.nutch.service.ConfManager;
import org.apache.nutch.service.JobManager;
import org.apache.nutch.service.SeedManager;
import org.apache.nutch.service.impl.*;
import org.apache.nutch.service.model.response.JobInfo;
import org.apache.nutch.service.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author jbirkhimer
 */
public class NutchServerTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

//    private static final String LOCALHOST = "localhost";
//    private static final Integer DEFAULT_PORT = 8081;
    private static final int JOB_CAPACITY = 100;
//    private static final String CMD_HELP = "help";
//    private static final String CMD_PORT = "port";
//    private static final String CMD_HOST = "host";
//    private static Integer port = DEFAULT_PORT;
//    private static String host = LOCALHOST;
    private static FetchNodeDb fetchNodeDb;
    private static NutchServerTest server;

    static {
        server = new NutchServerTest();
    }

    private long started;
    private boolean running;
//    private JAXRSServerFactoryBean sf;
    private ConfManager configManager;
    private JobManager jobManager;
    private SeedManager seedManager;

    private NutchServerTest() {
        configManager = new ConfManagerImpl();
        seedManager = new SeedManagerImpl();
        BlockingQueue<Runnable> runnables = Queues.newArrayBlockingQueue(JOB_CAPACITY);
        NutchServerPoolExecutor executor = new NutchServerPoolExecutor(10, JOB_CAPACITY, 1, TimeUnit.HOURS, runnables);
        jobManager = new JobManagerImpl(new JobFactory(), configManager, executor);
        fetchNodeDb = FetchNodeDb.getInstance();

//        sf = new JAXRSServerFactoryBean();
//        BindingFactoryManager manager = sf.getBus().getExtension(BindingFactoryManager.class);
//        JAXRSBindingFactory factory = new JAXRSBindingFactory();
//        factory.setBus(sf.getBus());
//        manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, factory);
//        sf.setResourceClasses(getClasses());
//        sf.setResourceProviders(getResourceProviders());
//        sf.setProvider(new JacksonJaxbJsonProvider());

    }

    public static NutchServerTest getInstance() {
        return server;
    }

    protected static void startServer() {
        server.start();
    }

    private void start() {
        LOG.info("Starting NutchServerTest...");
//        try{
//            String address = "http://" + host + ":" + port;
//            sf.setAddress(address);
//            sf.create();
//        }catch(Exception e){
//            throw new IllegalStateException("Server could not be started", e);
//        }

        started = System.currentTimeMillis();
        running = true;
        LOG.info("Started Nutch Server at {}", started);
    }

    private List<Class<?>> getClasses() {
        List<Class<?>> resources = new ArrayList<>();
        resources.add(JobResource.class);
        resources.add(ConfigResource.class);
        resources.add(DbResource.class);
        resources.add(AdminResource.class);
        resources.add(SeedResource.class);
        resources.add(ReaderResouce.class);
        resources.add(ServicesResource.class);
        return resources;
    }

//    private List<ResourceProvider> getResourceProviders() {
//        List<ResourceProvider> resourceProviders = new ArrayList<>();
//        resourceProviders.add(new SingletonResourceProvider(getConfManager()));
//        return resourceProviders;
//    }

    public ConfManager getConfManager() {
        return configManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public SeedManager getSeedManager() {
        return seedManager;
    }

    public FetchNodeDb getFetchNodeDb() {
        return fetchNodeDb;
    }

    public boolean isRunning() {
        return running;
    }

    public long getStarted() {
        return started;
    }

//    public static void main(String[] args) throws ParseException {
//        CommandLineParser parser = new PosixParser();
//        Options options = createOptions();
//        CommandLine commandLine = parser.parse(options, args);
//        if (commandLine.hasOption(CMD_HELP)) {
//            HelpFormatter formatter = new HelpFormatter();
//            formatter.printHelp("NutchServerTest", options, true);
//            return;
//        }
//
//        if (commandLine.hasOption(CMD_PORT)) {
//            port = Integer.parseInt(commandLine.getOptionValue(CMD_PORT));
//        }
//
//        if (commandLine.hasOption(CMD_HOST)) {
//            host = commandLine.getOptionValue(CMD_HOST);
//        }
//
//        startServer();
//    }

//    private static Options createOptions() {
//        Options options = new Options();
//
//        OptionBuilder.withDescription("Show this help");
//        options.addOption(OptionBuilder.create(CMD_HELP));
//
//        OptionBuilder.withArgName("port");
//        OptionBuilder.hasOptionalArg();
//        OptionBuilder.withDescription("The port to run the Nutch Server. Default port 8081");
//        options.addOption(OptionBuilder.create(CMD_PORT));
//
//        OptionBuilder.withArgName("host");
//        OptionBuilder.hasOptionalArg();
//        OptionBuilder.withDescription("The host to bind the Nutch Server to. Default is localhost.");
//        options.addOption(OptionBuilder.create(CMD_HOST));
//
//        return options;
//    }

    public boolean canStop(boolean force) {
        if (force)
            return true;

        Collection<JobInfo> jobs = getJobManager().list(null, JobInfo.State.RUNNING);
        return jobs.isEmpty();
    }

//    protected static void setPort(int port) {
//        org.apache.nutch.service.NutchServerTest.port = port;
//    }
//
//    public int getPort() {
//        return port;
//    }

    public void stop() {
        System.exit(0);
    }
}
