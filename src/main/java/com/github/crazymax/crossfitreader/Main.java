package com.github.crazymax.crossfitreader;

import java.awt.SystemTray;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.github.crazymax.crossfitreader.device.Device;
import com.github.crazymax.crossfitreader.processus.ConfigProc;
import com.github.crazymax.crossfitreader.tray.SysTray;
import com.github.crazymax.crossfitreader.util.Util;

/**
 * Main class
 * @author crazy-max
 * @license MIT License
 \* @link https://github.com/crazy-max/crossfit-reader
 */
public class Main
        extends JPanel {
    
    private static final long serialVersionUID = -721149712768531919L;

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    
    public static boolean envDev = false;
    
    public static Logger rootLogger = Logger.getRootLogger();
    
    public static String appId = "crossfit-reader";
    public static String appName = "Crossfit Reader";
    public static String appDesc = "Card reader for ACR122U device affiliate to Crossfit Nancy";
    public static String appVersion = "DEV";
    public static String appGuid = "{}";
    public static String appAuthor = "crazy-max";
    public static String appUrl = "https://github.com/crazy-max/crossfit-reader";
    
    public static String appNameVersion = appName + " " + appVersion;
    public static Path appPath;
    public static Path appJarPath;
    public static Path appLogsPath;
    public static Path appRssPath;
    public static String appPid;
    
    public static Device device;
    
    public static void main(final String[] args) {
        // Default logger
        final ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setName("console");
        consoleAppender.setTarget("System.out");
        consoleAppender.setLayout(new PatternLayout("%d{ISO8601} %5p %c - %m%n"));
        consoleAppender.activateOptions();
        rootLogger.addAppender(consoleAppender);
        
        if (!SystemUtils.IS_OS_WINDOWS) {
            LOGGER.info("Must be run on Windows...");
            return;
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.error(Util.i18n("common.error.lnf"), e);
        }
        
        try {
            initApp();
        } catch (Exception e) {
            LOGGER.error(Util.i18n("common.error.initapp"), e);
        }
        
        LOGGER.info("--------------------------------");
        LOGGER.info("Starting app...");
        logInfos();
        LOGGER.info("---");
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main main = new Main();
                main.setOpaque(true);
                
                // Start app
                try {
                    if (!SystemTray.isSupported()) {
                        Util.logErrorExit(Util.i18n("main.error.systray"));
                    }
                    if (!Util.isAppStarted()) {
                        SysTray.getInstance().init();
                    }
                } catch (Throwable t) {
                    LOGGER.error(t.getMessage(), t);
                    System.exit(1);
                }
            }
        });
    }
    
    public Main() {
        try {
            ConfigProc.getInstance().loadConfig();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }
    
    private static void initApp()
            throws IOException {
        final String className = Main.class.getSimpleName() + ".class";
        final String classPath = Main.class.getResource(className).toString();
        final String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        final String rssFilter = "com/github/crazymax/crossfitreader/ext/";
        
        appPid = Util.getPID();
        appPath = Paths.get(System.getProperty("user.dir"));
        if (!classPath.startsWith("jar")) {
            envDev = true;
            appPath = appPath.resolve(".dev");
            if (!appPath.toFile().exists()) {
                appPath.toFile().mkdir();
            }
        }
        
        appLogsPath = appPath.resolve("logs");
        if (!appLogsPath.toFile().exists()) {
            appLogsPath.toFile().mkdir();
        }
        
        if (envDev) {
            appRssPath = Paths.get(System.getProperty("user.dir"))
                    .resolve("src")
                    .resolve("main")
                    .resolve("resources")
                    .resolve(rssFilter);
            return;
        }
        
        // Log4j
        final RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setName("file");
        fileAppender.setLayout(new PatternLayout("%d{ISO8601} %5p %c - %m%n"));
        fileAppender.setFile(appPath + File.separator + "logs" + File.separator + appId + ".log");
        fileAppender.setAppend(true);
        fileAppender.setEncoding("UTF-8");
        fileAppender.setThreshold(Level.INFO);
        fileAppender.activateOptions();
        rootLogger.addAppender(fileAppender);
        
        // Extract resources
        appRssPath = appPath.resolve("ext");
        if (!Files.exists(appRssPath)) {
            try {
                if (!appRssPath.toFile().exists()) {
                    appRssPath.toFile().mkdir();
                }
                
                final JarFile jarFile = new JarFile(jarPath);
                final FileInputStream fin = new FileInputStream(jarPath);
                final JarInputStream jin = new JarInputStream(new BufferedInputStream(fin));
                
                BufferedOutputStream dest = null;
                JarEntry je = null;
                
                while ((je = jin.getNextJarEntry()) != null) {
                    final String name = je.getName().replaceFirst(rssFilter, "");
                    if (!je.getName().startsWith(rssFilter) && !StringUtils.isEmpty(name)) {
                        continue;
                    }
                    
                    LOGGER.debug(String.format("Extracting %s", name));
                    final String destPath = appRssPath + File.separator + name;
                    
                    if (je.isDirectory()) {
                        final File folder = new File(destPath);
                        if (!folder.isDirectory()) {
                            folder.mkdirs();
                        }
                    } else {
                        int count;
                        final byte data[] = new byte[2048];
                        final FileOutputStream fos = new FileOutputStream(destPath);
                        dest = new BufferedOutputStream(fos, 2048);
                        while ((count = jin.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                        fos.close();
                    }
                    jin.closeEntry();
                }
                jin.close();
                jarFile.close();
            } catch (IOException e) {
                Util.logErrorExit(Util.i18n("main.error.extractjar"), e);
            }
        }
        
        // Extract infos from MANIFEST
        String manifestPath = null;
        InputStream manifestStream = null;
        try {
            manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
            manifestStream = new URL(manifestPath).openStream();
            final Manifest manifest = new Manifest(manifestStream);
            final Attributes attr = manifest.getMainAttributes();
            appId = attr.getValue("Project-Id");
            appName = attr.getValue("Project-Name");
            appDesc = attr.getValue("Project-Desc");
            appVersion = attr.getValue("Project-Version") + "." + attr.getValue("Project-Release");
            appGuid = attr.getValue("Project-Guid");
            appAuthor = attr.getValue("Project-Author");
            appUrl = attr.getValue("Project-Url");
            appNameVersion = appName + " " + appNameVersion;
        } finally {
            if (manifestStream != null) {
                manifestStream.close();
            }
        }
    }
    
    private static void logInfos() {
        final String[] infos = {
                "Java:        " + System.getProperty("java.version"),
                "Envdev:      " + (envDev ? "yes" : "no"),
                "Id:          " + appId,
                "Name:        " + appName,
                "Desc:        " + appDesc,
                "Version:     " + appVersion,
                "NameVersion: " + appNameVersion,
                "GUID:        " + appGuid,
                "Author:      " + appAuthor,
                "Url:         " + appUrl,
                "Path:        " + appPath,
                "Ext Path:    " + appRssPath,
                "PID:         " + appPid
        };
        
        for (final String info : infos) {
            LOGGER.info(info);
        }
    }
}
