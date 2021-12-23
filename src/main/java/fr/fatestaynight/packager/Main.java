package fr.fatestaynight.packager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

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
        JCheckBox checkbox = new JCheckBox("Cacher le code");
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

            boolean codeAffiche = true;
            if (checkbox.isSelected()) {
                codeAffiche = false;
            }
            // Crée un objet pageFetch correspondant à la page désirée
            PageFetcher pageFetch = new PageFetcher(tfSearchInFile.getText(), Integer.parseInt(tfSearchInPage.getText()), codeAffiche);

            textOutputJapanese.setText(pageFetch.fetchText("japanese"));

            textOutputEnglish.setText(pageFetch.fetchText("english"));
        });
        
        //Mise en page de la fenêtre 
        this.setTitle("Fate/Stay Night [Translation helper] - 0.3 requinDr");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(780, 400);
        topPane.add(new JLabel(" Nom du fichier : ", SwingConstants.RIGHT));
        topPane.add(tfSearchInFile);
        topPane.add(new JLabel(" Page : ", SwingConstants.RIGHT));
        topPane.add(tfSearchInPage);
        topPane.add(checkbox);
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
