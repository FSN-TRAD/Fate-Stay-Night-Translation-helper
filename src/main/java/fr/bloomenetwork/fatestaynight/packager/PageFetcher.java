package fr.bloomenetwork.fatestaynight.packager;

import java.io.IOException;
import java.io.InputStream;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
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

	public PageFetcher(String fileName, int pageNum) {
        if (!fileName.endsWith(".ks")) {
		    fileName = fileName + ".ks";
        }
		this.fileName = fileName;
        if (pageNum <= 0) {
            pageNum = 1;
        }
		this.pageNum = pageNum;
	}

	
	/**
     * Crée les paramètres et exécute la fonction lectureFichier
     * qui va chercher dans le répertoire japanese
     */
	public void fetchJapanese() {
        String language = "japanese";
		String content = lectureFichier(fileName, pageNum, language);
		System.out.println(content);
    }

    /**
     * Crée les paramètres et exécute la fonction lectureFichier
     * qui va chercher dans le répertoire english
     */
	public void fetchEnglish() {
        String language = "english";
		String content = lectureFichier(fileName, pageNum, language);
		System.out.println(content);
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
        String pageStart = "*page" + (pageNum -1)+ "|";
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
                System.err.println("La page " + pageNum + " n'existe pas dans ce fichier.");
                System.out.println("[INFO] Affichage du fichier complet.\n");
            }

        } catch (IOException ex) {
            System.err.println("Problème d'accès au fichier " + nomFichier);
        }

		return contenu;
    }
	
	
}
