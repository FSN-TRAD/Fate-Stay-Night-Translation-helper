package fr.fatestaynight.packager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * Spinner used for day, scene and page.
 */
class NumberSpinner extends JSpinner {

    /**
     * @param nb_cols - width of the text field
     * @param def_val - base value
     * @param min - min accepted value
     * @param max - max accepted value
     */
    NumberSpinner(int nb_cols, int def_val, int min, int max) {
        super();
        this.setModel(new SpinnerNumberModel(def_val,min,max,1));
        JFormattedTextField textField = ((JSpinner.DefaultEditor)this.getEditor()).getTextField();
        ((NumberFormatter)textField.getFormatter()).setAllowsInvalid(false);
        textField.setColumns(nb_cols);
    }

    /**
     * Sets the default value to the specified minimum.
     * @param nb_cols - width of the text field
     * @param min - min accepted value
     * @param max - max accepted value
     */
    NumberSpinner(int nb_cols, int min, int max) {
        this(nb_cols, min, min, max);
    }

    /**
     * Sets the minimum value to 0, and maximum value to 1000.
     * @param nb_cols - width of the text field
     * @param def_val - base value
     */
    NumberSpinner(int nb_cols, int def_val) {
        this(nb_cols, def_val, 0, 1000);
    }

    int getIntValue() {
        return (int) super.getValue();
    }
}

/**
 * Routes enumeration.
 * Handles the processing of file name from route, day and scene.
 */
enum Route {

    Fate("セイバールート%s日目-%02d.ks"),
    UBW("凛ルート%s日目-%02d.ks"),
    HF("桜ルート%s日目-%02d.ks"),
    Prologue("プロローグ%d日目.ks"),
    Epilogue_Fate("セイバーエピローグ%s.ks"),
    Epilogue_UBW("凛エピローグ%s.ks"),
    Epilogue_HF("桜エピローグ%s.ks");

	private static final String[] NUMBERS_JAP = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    public final String code;

    Route(String code) {
        this.code = code;
    }

    /**
     * Converts the parameter into its japanese notation.
     * @param n - integer to convert, from 0 to 99
     * @return the string japanese representation of n
     */
    private String convertNum(int n) {
        switch((n/10)%10) {
            case 0 :
                return NUMBERS_JAP[n%10];
            case 1 :
                return NUMBERS_JAP[10] + NUMBERS_JAP[n%10];
            default :
                return NUMBERS_JAP[(n/10)%10] + NUMBERS_JAP[10] + NUMBERS_JAP[n%10];

        }
    }

    /**
     * Computes the script file name from the route, day and scene.
     * @param day
     * @param scene
     * @return the script (*.ks) file name
     */
    public String fileName(int day, int scene) {
        switch(this) {
            case Fate : case UBW : case HF :
                return String.format(this.code, convertNum(day), scene);
            case Prologue :
                return String.format(this.code, day);
            default : //Epilogue
                return String.format(this.code, day > 1 ? Integer.toString(day) : "");
        }
    }

    /**
     * Lists all route names, with '-' instead of '_'.
     * @return the array of route names.
     */
    public static String[] names() {
        return Arrays.stream(Route.values())
                .map(r -> r.name().replaceAll("_", "-"))
                .toArray(String[]::new);
    }
}

/**
 * Language enumeration. Each language is associated with a directory name
 */
enum Language {
    jap("japanese"),
    en("english"),
    en_new("english_new"),
    fr("french");

    public final String dir_name;

    Language(String dir_name) {
        this.dir_name = dir_name;
    }
}

/**
 * Lance l'interface et le programmme principal
 * @author requinDr, loicfr
 */
public class Main extends JFrame {
    private static final String WINDOW_TITLE = "Fate/stay night [Translation helper] - 0.5";
    private static final int DEFAULT_WIDTH = 820;
    private static final int DEFAULT_HEIGHT = 480;
    
    // Composants graphiques
    private JButton start_btn;
    private JTextField fileName_tf;
    private NumberSpinner pageSpinner;
    private JCheckBox hideCode_cb;
    private JTextPane[] textPanels;

    private PageFetcher pageFetch;
    private Language[] languages;
    
    public Main() {
        
        // Mise en page de la fenêtre 
        this.setTitle(WINDOW_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        this.languages = new Language[] {Language.jap, Language.en, Language.en_new};
        
        this.setupTopMenu();

        this.setupTextPanes();
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    // Automatically refresh pages when spinner value is changed
    class SpinnerListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
          updateTexts();
        }
    }

    /**
     * Initializes the top configuration menu with file and page selection,
     * and other options.
     */
    private void setupTopMenu() {
        JPanel topPane = new JPanel(new GridLayout(1,2));

        JPanel fileSelectPane = new JPanel(new GridLayout(2,1));

        JPanel fileNamePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.fileName_tf = new JTextField("", 16);
        fileNamePane.add(new JLabel(" Nom du fichier :", SwingConstants.RIGHT));
        fileNamePane.add(this.fileName_tf);
        fileSelectPane.add(fileNamePane);

        JPanel fileConstructPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> routeSelect = new JComboBox<>(Route.names());
        routeSelect.setSelectedIndex(0);
        NumberSpinner daySelect = new NumberSpinner(2, 1, 20);
        NumberSpinner sceneSelect = new NumberSpinner(2, 0, 99);
        
        fileConstructPane.add(routeSelect);
        fileConstructPane.add(new JLabel("Jour :", SwingConstants.RIGHT));
        fileConstructPane.add(daySelect);
        fileConstructPane.add(new JLabel("Scène :", SwingConstants.RIGHT));
        fileConstructPane.add(sceneSelect);
        this.pageSpinner = new NumberSpinner(3, 1, 999);
        pageSpinner.addChangeListener(new SpinnerListener()); // check for a value change

        fileConstructPane.add(new JLabel("Page :", SwingConstants.RIGHT));
        fileConstructPane.add(this.pageSpinner);

        fileSelectPane.add(fileConstructPane);
        fileSelectPane.setMinimumSize(fileSelectPane.getSize());
        
        Consumer<Object> updateFileName = (ignoredParam) -> {
            if (this.fileName_tf.hasFocus()) // event provoqué par le soft (non implémenté)
                return;
            Route route = Route.values()[routeSelect.getSelectedIndex()];
            int day = daySelect.getIntValue();
            int scene = sceneSelect.getIntValue();
            this.fileName_tf.setText(route.fileName(day, scene));
        };
        routeSelect.addItemListener(e -> updateFileName.accept(null));
        daySelect.addChangeListener(e -> updateFileName.accept(null));
        sceneSelect.addChangeListener(e -> updateFileName.accept(null));

        updateFileName.accept(null);

        topPane.add(fileSelectPane);

        JPanel navigationPane = new JPanel(new FlowLayout(FlowLayout.LEFT));

        this.hideCode_cb = new JCheckBox("Cacher le code");
        navigationPane.add(hideCode_cb);

        this.start_btn = new JButton("Démarrer");
        this.start_btn.addActionListener(e -> this.updateTexts());
        navigationPane.add(this.start_btn);

        topPane.add(navigationPane);

        this.add(topPane, BorderLayout.NORTH);
    }

    /**
     * Creates one text panel for each language in the {@link #languages} attribute.
     * Each panel has its own radio group to select the language to display.
     */
    private void setupTextPanes() {
        // one panel for each parameter
        int nb_panels = languages.length;
        JPanel[] panes = new JPanel[nb_panels];
        this.textPanels = new JTextPane[nb_panels];

        // add a HTMLEditorKit to the jpane
        HTMLEditorKit kit = new HTMLEditorKit();
        // add a stylesheet for the displayed content
        StyleSheet styleSheet = kit.getStyleSheet();

        for(int i=0; i< nb_panels; i++) {
            panes[i] = new JPanel(new BorderLayout());

            final JTextPane textPane = new JTextPane();
            textPane.setEditorKit(kit);
            styleSheet.addRule("body {margin:4px; text-align:justify;}");
            styleSheet.addRule(".error {color:#922B21;}");
            styleSheet.addRule(".atCode {color:#21618C;}");
            styleSheet.addRule(".bracketCode {color:#196F3D;}");
            textPane.setEditable(false);
            textPane.setContentType("text/html");
            this.textPanels[i] = textPane;

            JScrollPane scrollPane = new JScrollPane(this.textPanels[i]);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(null);
            panes[i].add(scrollPane, BorderLayout.CENTER);
            
            final int index = i; // variable 'final' obligatoire pour les callback;
            ItemListener langChangeListener = e -> {
                if (this.pageFetch != null) {
                    JRadioButton btn = (JRadioButton) e.getSource();
                    if (btn.isSelected()) {
                        Language lang = Language.valueOf(btn.getText().replaceAll("-", "_"));
                        this.languages[index] = lang;
                        textPane.setText("<body>" + pageFetch.fetchText(lang.dir_name) + "</body>");
                    }
                }
            };

            JPanel radioBtnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            ButtonGroup group =  new ButtonGroup();
            for(Language lang : Language.values()) {
                JRadioButton radioBtn = new JRadioButton(lang.name().replaceAll("_", "-"));
                radioBtn.setSelected(languages[i] == lang);
                radioBtnsPanel.add(radioBtn);
                group.add(radioBtn);
                radioBtn.addItemListener(langChangeListener);
            }
            panes[i].add(radioBtnsPanel, BorderLayout.NORTH);
            panes[i].setMinimumSize(new Dimension(100, 80));
        }
        Component lastComponent = panes[0];
        for(int i=1; i < nb_panels; i++) {
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            lastComponent, panes[i]);
            lastComponent = splitPane;
        }

        this.add(lastComponent, BorderLayout.CENTER);
    }

    /**
     * Updates the text in the panels with the selected configuration.
     */
    private void updateTexts() {

        boolean showCode = !this.hideCode_cb.isSelected();

        // Crée un objet pageFetch correspondant à la page désirée
        this.pageFetch = new PageFetcher(fileName_tf.getText(), pageSpinner.getIntValue(), showCode);

        // Remplace le texte des panneaux
        for(int i=0; i < this.textPanels.length; i++) {
            this.textPanels[i].setText("");
            this.textPanels[i].setText(this.pageFetch.fetchText(this.languages[i].dir_name));
        }
    }


    public static void main(String[] args) {
        Main main = new Main();
    }

}
