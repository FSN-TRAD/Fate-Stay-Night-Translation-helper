package fr.fatestaynight.packager;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Va chercher le fichier et la page correspondante
 * @author requinDr
 */
public class PageFetcher {
	
    private String fileName;
    private int pageNum;
    private boolean codeAffiche;

	public PageFetcher(String fileName, int pageNum, boolean codeAffiche) {
        if (!fileName.endsWith(".ks")) {
		    fileName = fileName + ".ks";
        }
		this.fileName = fileName;
        if (pageNum <= 0) {
            pageNum = 1;
        }
		this.pageNum = pageNum;
        this.codeAffiche = codeAffiche;
	}

	
	/**
     * Crée les paramètres et exécute la fonction lectureFichier
     * qui va chercher dans le répertoire passé en argumument
     * @param language
     * @return content
     */
	public String fetchText(String language) {
		String content = lectureFichier(fileName, pageNum, language);

        if (!codeAffiche) {
            return cacheCode(content);
        }

		return content;
    }

	/**
     * Lit les lignes d'un fichier texte en les stockant au fur et à mesure dans un String.
     * Le nom du fichier à traiter est passé en argument.
     * @param nomFichier le nom du fichier à traiter.
     * @return contenu   le contenu de la page que l'on veut afficher.
     * @throws IOException
     */
    public static String lectureFichier(String nomFichier, int pageNum, String language) {
        String folder = language + "/";
        String pageStart = "*page" + (pageNum -1) + "|";
		String pageEnd = "*page" + pageNum + "|";
		String contenu = "";
		String ligne;
        int i = 0;

        try {
            BufferedReader fichier = new BufferedReader(new InputStreamReader(
                new FileInputStream(folder + nomFichier), StandardCharsets.UTF_16));

            while ((ligne = fichier.readLine()) != null) {
                contenu = contenu + ligne + "\n";
                i++;
            }

            fichier.close();

            // Corrige tous les sauts de ligne
            contenu = contenu.replaceAll("\\n\\r", "\n");

            // Extrait la page qui nous intéresse
            try {
                contenu = contenu.substring(contenu.lastIndexOf(pageStart), contenu.lastIndexOf(pageEnd));
            } catch (StringIndexOutOfBoundsException e) {
                contenu = "La page " + pageNum + " n'existe pas dans ce fichier.";
            }
        } catch (IOException ex) {
            System.err.println("Problème d'accès au fichier " + nomFichier);
            contenu = "Problème d'accès au fichier " + nomFichier;
        }

		return contenu;
    }

    /**
     * Cache le code de la page
     * @param content
     * @return
     */
    public static String cacheCode(String content) {
        String lines[] = content.split("\\r?\\n");
        String contentWCode = "";
        
        for (int i = 0; i < lines.length; i++) {
            // Supprime les lignes commençant par @ ou *page
            if (!(lines[i].startsWith("@") || lines[i].startsWith("*page"))) {
                // Remplace les [lineX] par un simple cadratin
                lines[i] = lines[i].replaceAll("\\[line\\d]", "—");
                // Supprime les crochets et leur contenu pour les lignes qui en ont
                lines[i] = lines[i].replaceAll("\\[[A-z0123456789]*]", "");
                // Recrée un texte dépourvu de code
                contentWCode = contentWCode + lines[i] + "\n\n";
            }
        }

        return contentWCode;
    }
	
}
