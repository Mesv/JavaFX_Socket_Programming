package main;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Main implements Runnable {

    Socket soket = null, socket = null;
    BufferedReader reader = null, oku = null;
    BufferedWriter yaz = null;
    String path = null, url = null, server_ip = null;
    char[] ip = new char[15];
    char[] port = new char[5];
    char[] karsilatirma = new char[11];
    String[] sonuc;
    String[] gelen;
    Timer t;
    File file;
    FileReader fr;
    FileWriter fw;
    int server_port = 0;
    TimerTask task;
    private JFXPanel jfxPanel;
    private WebEngine engine;
    private JFrame frame = new JFrame();
    private JPanel panel = new JPanel(new BorderLayout());
    Toolkit toolkit;

    private void initComponents() {
        jfxPanel = new JFXPanel();

        createScene();

        istemci();
        //sunucu();

        panel.add(jfxPanel, BorderLayout.CENTER);

        frame.getContentPane().add(panel);
    }

    private String[] dosyadanOku() {
        String[] dizi2 = new String[2];
        //adres = "C:\\Users\\MehmetSavasci\\Desktop\\socket.txt";
        path = "C:\\Users\\Duygu_Mehmet\\Desktop\\socket.txt";
        file = new File(path);
        sonuc = new String[2];

        if (file.exists()) {
            try {
                fr = new FileReader(file);
                oku = new BufferedReader(fr);
                sonuc[0] = oku.readLine();
                System.out.printf(sonuc[0]);
                sonuc[1] = oku.readLine();
                System.out.printf(sonuc[1]);

                if (sonuc[0] == null || sonuc[1] == null) {
                    JOptionPane.showMessageDialog(null, "File couldn't be read. Please contact with authorized person.", "Alert Message", JOptionPane.WARNING_MESSAGE);
                    JOptionPane.showMessageDialog(null, "Program will be terminated.", "Alert Message", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                } else {
                    String[] dizi = sonuc[0].split("=");
                    String[] dizi1 = sonuc[1].split("=");
                    /*if (Integer.valueOf(dizi[1]) > 65535 || dizi1[1].length() > 15) {
                        JOptionPane.showMessageDialog(null, "There is no valid ip, or port address. Please contact with authorized person. The program will be terminated prophylactic.", "Error Message", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    } else {*/
                        dizi2[0] = dizi[1];
                        dizi2[1] = dizi1[1];
                    //}
                }

                oku.close();
            } catch (IOException hata) {
                JOptionPane.showMessageDialog(null, "File couldn't be read, so, program will be terminated.");
                System.exit(0);
            }
        } else {
            try {
                file.createNewFile();
                JOptionPane.showMessageDialog(null, "File was created, however, the file cannot be read because of lack of parameter. Please contact with authorized person.", "Uyarı Mesajı", JOptionPane.WARNING_MESSAGE);
                JOptionPane.showMessageDialog(null, "Program will be terminated.", "Alert Message", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            } catch (IOException hata) {
                JOptionPane.showMessageDialog(null, "File couldn't be created, so, the program will be terminated.");
                System.exit(0);
            }
        }
        return dizi2;
    }

    private void dosyayaYaz(String port, String ip) {
        //String adres = "C:\\Users\\MehmetSavasci\\Desktop\\socket.txt";
        String path1 = "C:\\Users\\Duygu_Mehmet\\Desktop\\socket.txt";
        File file = new File(path1);
        try {
            if (file.exists()) {
                fw = new FileWriter(file, false);
                BufferedWriter yaz = new BufferedWriter(fw);
                yaz.write(port);
                yaz.newLine();
                yaz.write(ip);
                yaz.flush();
                yaz.close();
            } else {
                file.createNewFile();
                fw = new FileWriter(file, false);
                BufferedWriter yaz = new BufferedWriter(fw);
                yaz.write(port);
                yaz.newLine();
                yaz.write(ip);
                yaz.flush();
                yaz.close();
            }
        } catch (IOException hata) {
            JOptionPane.showMessageDialog(null, "The operation of writing to the file couldn't be done.");
        }
    }
    
    private void sunucu(){
        try {
            ServerSocket server = new ServerSocket(8000);
            socket = server.accept();
            TimerTask gorev = new TimerTask() {
                @Override
                public void run() {
                    try {
                        BufferedReader okuyucu  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String mesaj = okuyucu.readLine();
                        yeniBaglantiOlustur(mesaj);
                    } catch (Exception e1) {
                        //JOptionPane.showMessageDialog(null, "The website coming from the server couldn't be read.");
                        frame.setTitle("The website coming from the server couldn't be read.");
                    }
                }
            };

            Timer zaman = new Timer();
            zaman.schedule(gorev, 0, 1000);
        } catch (IOException ex) {
            System.out.println("The operation of listening couldn't be initialized.");
        }
    }

    private void istemci() {
        gelen = new String[2];

        try {
            gelen = null;
            gelen = dosyadanOku();

            server_port = Integer.valueOf(gelen[0]);
            server_ip = gelen[1];
            soket = new Socket(server_ip, server_port);
            frame.setTitle("The connection was established with the server.");

            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        reader = new BufferedReader(new InputStreamReader(soket.getInputStream()));
                        url = reader.readLine();
                        yeniBaglantiOlustur(url);
                    } catch (Exception e1) {
                        //JOptionPane.showMessageDialog(null, "The website coming from the server couldn't be read.");
                        frame.setTitle("The website coming from the server couldn't be read.");
                    }
                }
            };

            t = new Timer();
            t.schedule(task, 0, 1000);
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "The connection to the server couldn't be established.", "Error Message", JOptionPane.ERROR_MESSAGE);
            frame.setTitle("The connection to the server couldn't be established.");
        }
    }

    public void yeniBaglantiOlustur(String url) {
        if (url.startsWith("param?")) {
            String[] dizi = url.substring(6).split("&");
            dosyayaYaz(dizi[0].trim(), dizi[1].trim());

            gelen = null;
            gelen = dosyadanOku();
            server_port = Integer.valueOf(gelen[0]);
            server_ip = gelen[1];

            try {
                JOptionPane.showMessageDialog(null, "Please update the information of ip and port at the server side. Otherwise, the connection was continued via number of old ip and port.", "Warning Message", JOptionPane.WARNING_MESSAGE);
                soket = new Socket(server_ip, server_port);
                System.out.println(soket.getPort());
                frame.setTitle("The connection with the server was established again.");
            } catch (IOException ex) {

                JOptionPane.showMessageDialog(null, "The connection with the server was not established again.", "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (url.equals("exit") || url.equals("EXIT")) {
            System.exit(0);
        } else {
            loadURL(url);
            System.out.println(url);
        }
    }

    private void createScene() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                WebView view = new WebView();
                engine = view.getEngine();

                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.setTitle(newValue);
                            }
                        });
                    }
                });

                jfxPanel.setScene(new Scene(view));
            }
        });
    }

    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String tmp = toURL(url);

                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }

                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }

    @Override
    public void run() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        frame.setPreferredSize(dimension);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        //loadURL("http://www.tubitak.gov.tr");

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
    	System.out.printf("I am in the main function."); 
        SwingUtilities.invokeLater(new Main());
    }
}