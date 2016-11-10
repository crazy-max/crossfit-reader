package com.github.crazymax.crossfitreader.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.github.crazymax.crossfitreader.Main;
import com.github.crazymax.crossfitreader.exception.FindDeviceException;
import com.github.crazymax.crossfitreader.exception.ScanCardException;
import com.github.crazymax.crossfitreader.model.OutputCmd;
import com.google.common.base.Strings;

/**
 * Utility class
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class Util {
    
    private static final Logger LOGGER = Logger.getLogger(Util.class);
    
    private static final byte[] APDU_GET_DATA = { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
    
    private Util() {
        super();
    }
    
    /**
     * Return the java process PID
     */
    public static String getPID() {
        return StringUtils.substringBefore(ManagementFactory.getRuntimeMXBean().getName(), "@");
    }
    
    /**
     * Count files in a directory (including files in all subdirectories)
     * @param directory the directory to start in
     * @return the total number of files
     */
    public static int countFilesInDirectory(final File directory) {
        int count = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                count++;
            }
            if (file.isDirectory()) {
                count += countFilesInDirectory(file);
            }
        }
        return count;
    }
    
    /**
     * Retreive a ressource in a jar file
     * @param filename
     */
    public static File getRssFile(final String filename) {
        return new File(Main.appRssPath.toFile(), filename);
    }
    
    /**
     * Retrieve a value from a bundle
     * @param key
     * @return value
     */
    public static String i18n(final String key) {
        String value;
        try {
            value = Resources.BUNDLE.getString(key);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            value = key;
        }
        return value;
    }
    
    public static boolean isProcessRunning(final int pid)
            throws IOException {
        try {
            final List<String> args = new ArrayList<String>();
            args.add("/c");
            args.add("tasklist /FI \"PID eq " + String.valueOf(pid) + "\" | findstr " + String.valueOf(pid));
            
            final OutputCmd outputCmd = Util.execCmd("cmd", args);
            return outputCmd.getExitCode() == 0;
        } catch (IOException e) {
            Util.logError(String.format(Util.i18n("util.error.procrunning"), String.valueOf(pid)), e);
            return false;
        }
    }
    
    public static File getPidFile() {
        return new File(Main.appPath.toFile(), Main.appId + ".pid");
    }
    
    public static void createPidFile() {
        final File pidFile = getPidFile();
        
        try {
            final PrintWriter pw = new PrintWriter(new FileOutputStream(pidFile));
            pw.print(Util.getPID());
            pw.close();
        } catch (FileNotFoundException e) {
            Util.logErrorExit(Util.i18n("util.error.pid"), e);
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(Main.appId + "-shutdown-hook") {
            @Override
            public void run() {
                if (pidFile.exists()) {
                    pidFile.delete();
                }
            }
        });
    }
    
    public static boolean isAppStarted() {
        final File pidFile = getPidFile();
        try {
            if (!pidFile.exists()) {
                return false;
            }
            final int pid = Integer.valueOf(FileUtils.readFileToString(pidFile, "UTF-8"));
            LOGGER.debug("PID found: " + pid);
            if (isProcessRunning(pid)) {
                LOGGER.debug("Process already running: " + pid);
                return true;
            } else {
                pidFile.delete();
                return false;
            }
        } catch (IOException | NumberFormatException e) {
            pidFile.delete();
            return false;
        }
    }
    
    public static CardTerminal getTerminal() throws FindDeviceException {
        List<CardTerminal> terminalList = getTerminalList();
        if (terminalList == null || terminalList.size() <= 0) {
            throw new FindDeviceException("No terminal found");
        }
        
        if (terminalList.size() != 1) {
            throw new FindDeviceException("Only one terminal is allowed per computer");
        }
        
        CardTerminal cardTerminal = terminalList.get(0);
        final String terminalShortName = cardTerminal.getName().substring(0, cardTerminal.getName().length() - 2);
        if (!isValidTerminal(terminalShortName)) {
            throw new FindDeviceException("Device not recognized: " + cardTerminal.getName());
        }
            
        return cardTerminal;
    }
    
    public static boolean isValidTerminal(String name) {
        return !Strings.isNullOrEmpty(name) && name.startsWith("ACS ACR122"); 
    }
    
    public static List<CardTerminal> getTerminalList() {
        refreshTerminalList();
        TerminalFactory factory = TerminalFactory.getDefault();
        if (factory != null) {
            try {
                CardTerminals devices = factory.terminals();
                return devices.list();
            } catch (CardException e) {
                return null;
            }
        }
        return null;
    }
    
    public static String getCardUid(Card card) throws ScanCardException {
        try {
            String result = null;
            
            CardChannel comm = card.getBasicChannel();
            CommandAPDU getData = new CommandAPDU(APDU_GET_DATA);
            ResponseAPDU resp = comm.transmit(getData);
            
            String key = byteArrayToHexString(resp.getBytes());
            if (key != null) {
                result = key.substring(0, key.length() - 4);
            }
            
            LOGGER.info("Card UID: " + result);
            return result;
        } catch (Throwable e) {
            if (!e.getMessage().contains("SCARD_W_REMOVED_CARD")) {
                throw new ScanCardException(e.getMessage(), e);
            }
        }
        return null;
    }
    
    public static boolean isTerminalPlugged() {
        try {
            getTerminal();
            return true;
        } catch (FindDeviceException e) {
            return false;
        }
    }
    
    /**
     * Refresh terminal list
     * http://stackoverflow.com/a/26470094
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void refreshTerminalList() {
        try {
            // Refresh list
            Class pcscterminal = Class.forName("sun.security.smartcardio.PCSCTerminals");
            Field contextId = pcscterminal.getDeclaredField("contextId");
            contextId.setAccessible(true);
            if(contextId.getLong(pcscterminal) != 0L) {
                // First get a new context value
                Class pcsc = Class.forName("sun.security.smartcardio.PCSC");
                Method SCardEstablishContext = pcsc.getDeclaredMethod(
                        "SCardEstablishContext",
                        new Class[] {Integer.TYPE }
                );
                SCardEstablishContext.setAccessible(true);

                Field SCARD_SCOPE_USER = pcsc.getDeclaredField("SCARD_SCOPE_USER");
                SCARD_SCOPE_USER.setAccessible(true);

                long newId = ((Long)SCardEstablishContext.invoke(pcsc, 
                        new Object[] { SCARD_SCOPE_USER.getInt(pcsc) }
                ));
                contextId.setLong(pcscterminal, newId);

                // Then clear the terminals in cache
                TerminalFactory factory = TerminalFactory.getDefault();
                CardTerminals terminals = factory.terminals();
                Field fieldTerminals = pcscterminal.getDeclaredField("terminals");
                fieldTerminals.setAccessible(true);
                Class classMap = Class.forName("java.util.Map");
                Method clearMap = classMap.getDeclaredMethod("clear");

                clearMap.invoke(fieldTerminals.get(terminals));
            }
        } catch (Exception e) {
            // Nothing to do here
            return;
        }
    }
    
    private static String byteArrayToHexString(final byte[] bArray) {
        StringBuffer buffer = new StringBuffer();
        
        for (byte b : bArray) {
            buffer.append(String.format("%02X", b));
        }
        
        return buffer.toString().toUpperCase();
    }
    
    public static void copyToClipboard(final String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }
    
    public static void playSound(final File file) {
        if (file == null || !file.exists()) { 
            LOGGER.error("Sound not found: " + file);
            return;
        } 
 
        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(file);
        } catch (IOException | UnsupportedAudioFileException e) { 
            LOGGER.error(e.getMessage(), e);
            return;
        }
 
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
 
        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[524288];
 
        try { 
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }
        } catch (IOException e) { 
            LOGGER.error(e.getMessage(), e);
            return;
        } finally { 
            auline.drain();
            auline.close();
        }
    }
    
    public static void logError(final String message) {
        logError(message, null);
    }
    
    public static void logError(final String message, final Throwable e) {
        LOGGER.error(message, e);
        showErrorDialog(message);
    }
    
    public static void logErrorExit(final String message) {
        logErrorExit(message, null);
    }
    
    public static void logErrorExit(final String message, final Throwable e) {
        LOGGER.error(message, e);
        showErrorDialog(message);
        System.exit(1);
    }
    
    public static void showErrorDialog(final String message) {
        JOptionPane.showMessageDialog(null, message, Main.appNameVersion + " - " + Util.i18n("common.error"),
                JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInfoDialog(final String title, final String message) {
        JOptionPane.showMessageDialog(null, message,
                Main.appNameVersion + (Strings.isNullOrEmpty(title) ? "" : " - " + title),
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showInfoDialog(final String message) {
        showInfoDialog(null, message);
    }
    
    public static int showYesNoDialog(final String message, final String title) {
        final String newTitle = Main.appNameVersion + " - " + title;
        return JOptionPane.showConfirmDialog(null, message, newTitle, JOptionPane.YES_NO_OPTION);
    }
    
    /**
     * Exec a silent command
     * @param command
     * @throws IOException
     */
    public static OutputCmd execSilent(final String command)
            throws IOException {
        return execSilent(command, null);
    }
    
    /**
     * Exec a silent command
     * @param command
     * @param args
     * @throws IOException
     */
    public static OutputCmd execSilent(final String command, final List<String> args)
            throws IOException {
        final List<String> newArgs = new ArrayList<String>();
        newArgs.add(command);
        if (args != null) {
            for (String arg : args) {
                newArgs.add(arg);
            }
        }
        
        return exec(getRssFile("scripts/execSilent.vbs").getAbsolutePath(), newArgs);
    }
    
    /**
     * Exec a MSI, EXE, BAT, VBS
     * @param fileName
     * @throws IOException
     */
    public static OutputCmd exec(final String fileName)
            throws IOException {
        return exec(fileName, null);
    }
    
    /**
     * Exec a MSI, EXE, BAT, VBS
     * @param fileName
     * @param args
     * @throws IOException
     */
    public static OutputCmd exec(final String fileName, final List<String> args)
            throws IOException {
        return exec(fileName, args, true, false);
    }
    
    /**
     * Exec a system command
     * @param cmd
     * @throws IOException
     */
    public static OutputCmd execCmd(final String cmd)
            throws IOException {
        return execCmd(cmd, null);
    }
    
    /**
     * Exec a system command
     * @param cmd
     * @param args
     * @throws IOException
     */
    public static OutputCmd execCmd(final String cmd, final List<String> args)
            throws IOException {
        return exec(cmd, args, true, true);
    }
    
    /**
     * Exec a MSI, EXE, BAT, VBS
     * @param fileName
     * @param args
     * @param waitFor
     * @param cmd
     * @throws IOException
     */
    public static OutputCmd exec(final String fileName, final List<String> args, final boolean waitFor,
            final boolean cmd)
            throws IOException {
        OutputCmd outputCmd = new OutputCmd();
        
        final List<String> command = new ArrayList<>();
        String filePathStr = fileName;
        
        if (cmd) {
            LOGGER.debug(String.format("Exec command %s", fileName));
        } else {
            final Path filePath = new File(fileName).toPath();
            filePathStr = filePath.toString();
            LOGGER.debug(String.format("Exec %s", filePath));
            if (Files.notExists(filePath)) {
                Util.logError(String.format(Util.i18n("util.error.exec.file"), filePath));
            }
        }
        
        if (fileName.endsWith(".vbs")) {
            command.add("cscript");
            command.add("//Nologo");
        } else if (fileName.endsWith(".bat")) {
            command.add("cmd");
            command.add("/c");
        } else if (fileName.endsWith(".msi")) {
            command.add("msiexec");
            command.add("/i");
        }
        
        command.add(fileName);
        
        if (args != null) {
            for (String arg : args) {
                command.add(arg);
            }
        }
        
        LOGGER.debug(String.format("Full command: %s", command));
        
        try {
            final ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(Main.appPath.toFile());
            Process proc = pb.start();
            
            if (!waitFor) {
                return null;
            }
            
            proc.waitFor();
            outputCmd.setExitCode(proc.exitValue());
            
            BufferedReader readerOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String rowOut = null;
            while ((rowOut = readerOut.readLine()) != null) {
                outputCmd.addOutput(rowOut);
            }
            
            BufferedReader readerErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String rowErr = null;
            while ((rowErr = readerErr.readLine()) != null) {
                outputCmd.addError(rowErr);
            }
            
            LOGGER.debug(String.format("Exit code: %s", outputCmd.getExitCode()));
            LOGGER.trace(String.format("Stderr: %s", outputCmd.printError()));
            LOGGER.trace(String.format("Stdout: %s", outputCmd.printOutput()));
        } catch (IOException | InterruptedException e) {
            LOGGER.error(String.format("Error executing %s", filePathStr), e);
        }
        
        return outputCmd;
    }
    
    public static boolean startsWith(final int number1, final int number2) {
        return startsWith(String.valueOf(number1), String.valueOf(number2));
    }
    
    public static boolean startsWith(final String str1, final String str2) {
        return str1.charAt(0) == str2.charAt(0);
    }
}
