package fr.ecn.ombre.activities.result;

import android.graphics.Bitmap;

/*--------------------------------------------------------------------------------------------
 * 				Classe conversion
 * Classe charg�e de convertir les grandeurs obtenues (hauteur, azimut) en coordonn�es (x,y)
 ---------------------------------------------------------------------------------------------*/

/**
 * 
 * @author Claire Cervera
 */
public class Conversion {

	public int X;
	public int Y;
	public double AzimutRelatif;

	// Certains param�tres sont internes � l'appareil photo utilis�

	public double calculAzimutRelatif(double Azimut, double DirectionVue) {

		double AzimutPositif;

		// Il s'agire tout d'abord de convertir l'azimut
		// en un angle pris dans le sens horaire par rapport
		// � la direction du Nord, comme pour DirectionVue
		AzimutPositif = Azimut + 180;

		// Calcul de l'azimut Relative: elle se calcule positivement dans le
		// sens trigonom�trique
		if (AzimutPositif <= DirectionVue) {
			AzimutRelatif = -AzimutPositif + DirectionVue;
		} else {
			AzimutRelatif = 360 - (AzimutPositif - DirectionVue);
		}
		return AzimutRelatif;
	}

	public Conversion(double Hauteur, double Azimut, double DirectionVue,
			Bitmap image, double focale) {
		// Nous calculons tout d'abord la position du Soleil en mm sur la photo:
		double positionX;
		double positionY;
		// Les param�tres de la photo hauteur*largeur:
		double largeur = 0;
		double hauteur = 0;

		int pixelLargeur = image.getWidth();
		int pixelHauteur = image.getHeight();

		// Si on est en mode paysage, la largeur est plus grande que la hauteur
		if (pixelLargeur > pixelHauteur) {
			hauteur = 24;
			largeur = 36;
		}
		// Si on est en mode portrait, la hauteur est plus grande que la largeur
		if (pixelLargeur < pixelHauteur) {
			hauteur = 36;
			largeur = 24;
		}

		System.out.println("Hauteur:" + Hauteur);

		// Nous calculons l'azimut relative, c'est � dire l'angle entre la
		// direction du soleil et la direction de la vue
		AzimutRelatif = calculAzimutRelatif(Azimut, DirectionVue);
		System.out.println("AzimutRelative:" + AzimutRelatif);
		// A priori, jusque l� le programme fonctionne bien!

		// On convertit en position X, Y sur une feuille 24*36...
		positionX = focale * Math.tan(AzimutRelatif * Math.PI / 180);
		positionY = focale * Math.tan(Hauteur * Math.PI / 180)
				/ Math.cos(AzimutRelatif * Math.PI / 180);

		// Si on suppose qu'on est en mode paysage

		// On repositionne les deux coordonn�es: on place le point (0,0) en haut
		// � gauche.
		positionX = -positionX + largeur / 2;
		positionY = -positionY + hauteur / 2;
		System.out.println("positionX:" + positionX);
		System.out.println("positionY:" + positionY);
		System.out.println("hauteur:" + hauteur);
		System.out.println("largeur:" + largeur);
		System.out.println("pixelLargeur:" + pixelLargeur);
		System.out.println("pixelHauteur:" + pixelHauteur);
		// On convertit en nombres de pixels sur l'image:
		X = (int) (pixelLargeur * ((double) (positionX / largeur)));
		Y = (int) (pixelHauteur * ((double) (positionY / hauteur)));
		System.out.println("X:" + X);
		System.out.println("Y:" + Y);
	}

}
