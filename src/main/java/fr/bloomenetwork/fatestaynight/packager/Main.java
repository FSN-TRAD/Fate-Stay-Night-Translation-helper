package fr.bloomenetwork.fatestaynight.packager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import java.awt.event.*;

/**
 * Lance l'interface et le programmme principal
 * @author requinDr
 */
public class Main extends JFrame {
    
    //Composants graphiques
    private JButton startButton;
    private JTextField tfSearchInFile;
    private JTextField tfSearchInPage;
    private JTextArea textOutputJapanese;
    private JTextArea textOutputEnglish;
    
    public Main() {
        
        //Configuration des divers éléments graphiques
        startButton = new JButton("Démarrer");
        tfSearchInFile = new JTextField("", 16);
        tfSearchInPage = new JTextField("", 4);

        textOutputJapanese = new JTextArea();
        textOutputJapanese.setRows(15);
        textOutputJapanese.setEditable(false);
        textOutputJapanese.setLineWrap(true);
        textOutputJapanese.setWrapStyleWord(true);
        textOutputEnglish = new JTextArea();
        textOutputEnglish.setRows(15);
        textOutputEnglish.setEditable(false);
        textOutputEnglish.setLineWrap(true);
        textOutputEnglish.setWrapStyleWord(true);
        JPanel topPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        //Listener sur le bouton qui exécute le programme
        startButton.addActionListener(e -> {
            //Vide les TextArea jp et en
            textOutputJapanese.selectAll();
            textOutputJapanese.replaceSelection("");
            textOutputEnglish.selectAll();
            textOutputEnglish.replaceSelection("");
            PageFetcher pageFetch = new PageFetcher(tfSearchInFile.getText(), Integer.parseInt(tfSearchInPage.getText()));
            
            System.setOut(new PrintStreamCapturer(textOutputJapanese, System.out));
            System.setErr(new PrintStreamCapturer(textOutputJapanese, System.err, "[ERREUR] "));
            pageFetch.fetchJapanese();
            System.setOut(new PrintStreamCapturer(textOutputEnglish, System.out));
            System.setErr(new PrintStreamCapturer(textOutputEnglish, System.err, "[ERREUR] "));
            pageFetch.fetchEnglish();
        });
        
        //Mise en page de la fenêtre 
        this.setTitle("Fate/Stay Night [Translation helper] - 0.2 requinDr");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(780, 380);
        topPane.add(new JLabel(" Nom du fichier : ", SwingConstants.RIGHT));
        topPane.add(tfSearchInFile);
        topPane.add(new JLabel(" Page : ", SwingConstants.RIGHT));
        topPane.add(tfSearchInPage);
        topPane.add(startButton);

        JScrollPane scrollPaneJapanese = new JScrollPane(textOutputJapanese);
        scrollPaneJapanese.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneJapanese.setBorder(null);

        JScrollPane scrollPaneEnglish = new JScrollPane(textOutputEnglish);
        scrollPaneEnglish.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneEnglish.setBorder(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        scrollPaneJapanese, scrollPaneEnglish);
        splitPane.setResizeWeight(0.5);

        this.add(topPane, BorderLayout.NORTH);
        this.add(splitPane);
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    public static void main(String[] args) {
        Main main = new Main();
    }

}
