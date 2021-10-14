package fr.bloomenetwork.fatestaynight.packager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;

import java.awt.event.*;

/**
 * Lance l'interface et le programmme principal
 * @author requinDr
 */
public class Main extends JFrame {
    
    //Paramètres
    private String searchInFile = "";
    private int searchInPage = 1;
    
    //Composants graphiques
    private JButton startButton;
    private JTextField searchInFileTextField;
    private JTextField searchInPageTextField;
    private JTextArea textOutputJapanese;
    private JTextArea textOutputEnglish;
    
    public Main() {
        
        //Configuration des divers éléments graphiques
        startButton = new JButton("Démarrer");
        searchInFileTextField = new JTextField(searchInFile);
        searchInPageTextField = new JTextField(searchInPage);

        textOutputJapanese = new JTextArea();
        textOutputJapanese.setRows(15);
        textOutputJapanese.setEditable(false);
        textOutputJapanese.setLineWrap(true);
        textOutputEnglish = new JTextArea();
        textOutputEnglish.setRows(15);
        textOutputEnglish.setEditable(false);
        textOutputEnglish.setLineWrap(true);
        JPanel topPane = new JPanel();
        
        //Listener sur le bouton qui exécute le programme
        startButton.addActionListener(e -> {
            //Vide les TextArea jp et en
            textOutputJapanese.selectAll();
            textOutputJapanese.replaceSelection("");
            textOutputEnglish.selectAll();
            textOutputEnglish.replaceSelection("");
            PageFetcher pageFetch = new PageFetcher(searchInFileTextField.getText(), Integer.parseInt(searchInPageTextField.getText()));
            
            System.setOut(new PrintStreamCapturer(textOutputJapanese, System.out));
            System.setErr(new PrintStreamCapturer(textOutputJapanese, System.err, "[ERREUR] "));
            pageFetch.fetchJapanese();
            System.setOut(new PrintStreamCapturer(textOutputEnglish, System.out));
            System.setErr(new PrintStreamCapturer(textOutputEnglish, System.err, "[ERREUR] "));
            pageFetch.fetchEnglish();
        });
        
        //Mise en page de la fenêtre 
        this.setTitle("Fate/Stay Night Translation - 0.1 requinDr");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(780, 360);
        topPane.add(new JLabel(" Nom du fichier : "));
        topPane.add(searchInFileTextField);
        topPane.add(new JLabel(" Page : "));
        topPane.add(searchInPageTextField);
        topPane.add(new JLabel(""));
        topPane.add(startButton);
        topPane.setLayout(new GridLayout(3, 2)); 
        

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
        this.add(splitPane, BorderLayout.SOUTH);
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    public static void main(String[] args) {
        Main main = new Main();
    }

}
