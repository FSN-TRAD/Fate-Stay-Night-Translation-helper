package fr.fatestaynight.packager;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Va chercher le fichier et la page correspondante
 * @author requinDr
 */
public class PageFetcher {
	
    private String fileName;
    private int pageNum;
    private boolean showCode;

	public PageFetcher(String fileName, int pageNum, boolean showCode) {
        if (!fileName.endsWith(".ks")) {
		    fileName = fileName + ".ks";
        }
		this.fileName = fileName;
        if (pageNum <= 0) {
            pageNum = 1;
        }
		this.pageNum = pageNum;
        this.showCode = showCode;
	}

	
	/**
     * Crée les paramètres et exécute la fonction lectureFichier
     * qui va chercher dans le répertoire passé en argumument
     * @param language
     * @return content
     */
	public String fetchText(String language) {
		String content = lectureFichier(fileName, pageNum, language);

        if (!showCode) {
            return hideCode(content);
        }

        return styleContent(content);
    }

    public static String lectureFichier(String nomFichier, int pageNum, String language, Charset charset) throws IOException {
        String fullName = language + "/" + nomFichier;
        String pageStart = "*page" + (pageNum -1) + "|";
		String pageEnd = "*page" + pageNum + "|";
		String content = "";
		String line;

        BufferedReader file = new BufferedReader(new InputStreamReader(
            new FileInputStream(fullName), charset));
        
        // saute toutes les lignes jusqu'à la fin de la page précédente
        do {
            line = file.readLine();
        } while ( (line != null) && (line.indexOf(pageStart) == -1) );

        if (line != null) {
            // sauvegarde les lignes jusqu'à la fin de la page
            while (((line = file.readLine()) != null) && (line.indexOf(pageEnd) == -1)) {
                content += line + "<br>";
            }
        }
        file.close();
        
        if (line == null) {
            return null;
        }

		return content;
    }

	/**
     * Lit les lignes d'un fichier texte en les stockant au fur et à mesure dans un String.
     * Le nom du fichier à traiter est passé en argument.
     * @param nomFichier le nom du fichier à traiter.
     * @return content   le contenu de la page que l'on veut afficher.
     * @throws IOException
     */
    public static String lectureFichier(String nomFichier, int pageNum, String language) {
        try {
            String content = lectureFichier(nomFichier, pageNum, language, StandardCharsets.UTF_16);
            if (content == null)
                content = lectureFichier(nomFichier, pageNum, language, StandardCharsets.UTF_8);
            if (content == null)
                content = "<span class='error'>La page " + pageNum + " n'existe pas dans le fichier " + language
                        + "/" + nomFichier + ".</span>";
            return content;
        } catch (IOException e) {
            String content = "<span class='error'>Problème d'accès au fichier " + nomFichier + "</span>";
            System.err.println(content);
            return content;
        }
    }

    /**
     * Hide the page script parts
     * @param content
     * @return the content without the code
     */
    public static String hideCode(String content) {
        String lines[] = content.split("<br>");
        String contentWCode = "";
        
        for (int i = 0; i < lines.length; i++) {
            // Supprime les lignes commençant par @ ou *page
            if (!(lines[i].startsWith("@") || lines[i].startsWith("*page"))) {
                // Remplace les [lineX] par un simple cadratin
                lines[i] = lines[i].replaceAll("\\[line\\d]", "—");
                // Supprime les crochets et leur contenu pour les lignes qui en ont
                lines[i] = lines[i].replaceAll("\\[[A-z0123456789]*]", "");
                // Recrée un texte dépourvu de code
                contentWCode = contentWCode + lines[i] + "<br><br>";
            }
        }

        return contentWCode;
    }

    /**
     * Style the page content
     * @param content
     * @return contentStyled
     */
    public static String styleContent(String content) {
        String lines[] = content.split("<br>");
        String contentStyled = "";
        
        for (int i = 0; i < lines.length; i++) {
            // Define the class for lines beginning with @
            if (lines[i].startsWith("@")) {
                lines[i] = "<span class='atCode'>" + lines[i] + "</span>";
            }
            // Define the class for [text]
            if (lines[i].contains("[")) {
                lines[i] = lines[i].replaceAll("\\[", "<span class='bracketCode'>[");
                lines[i] = lines[i].replaceAll("\\]", "]</span>");
            }
            contentStyled = contentStyled + lines[i] + "<br>";
        }

        return contentStyled;
    }
	
}
