package fr.ecn.ombre.core.shadows;

import java.util.Calendar;
import java.util.List;

import com.ei3info.gsun.Calculs;
import com.ei3info.gsun.PositionUtilisateur;
import com.ei3info.gsun.Temps;

import fr.ecn.ombre.core.image.Image;
import fr.ecn.ombre.core.model.Couple;
import fr.ecn.ombre.core.model.Face;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.model.Point;

/**
 * A class that generate a ShadowDrawing object based on the image infos and the date requested
 * 
 * @author jerome
 *
 */
public class ShadowDrawingFactory {
	
	protected ShadowDrawing shadowDrawing;
	
	protected Point sunPosition = null;
	protected Couple coupleSoleil = null;

	public ShadowDrawingFactory(Image image, ImageInfos imageInfos, Calendar time) throws ShadowDrawingException {
		// focale par défaut(en mm):
		double focal = 37;
		
		// position GPS:
		// position par défaut:
		PositionUtilisateur position = new PositionUtilisateur(47.25, -1.55); // Nantes
		// lecture de la position:
		position.setLatitude(imageInfos.getLatitude().getDecimalDegrees());
		position.setLongitude(imageInfos.getLongitude().getDecimalDegrees());

		// jour, mois, heure :
		int Jour = time.get(Calendar.DAY_OF_MONTH);
		int Mois = time.get(Calendar.MONTH) + 1;
		int Heure = time.get(Calendar.HOUR_OF_DAY);
		Temps date = new Temps(Jour, Mois);
		date.setHeure(Heure);
		
		// =============================================================//
		// CONVERSION date--> position soleil
		// Utilisation des classes des ei3...
		// =============================================================//

		// calcul azimut, hauteur:
		Calculs calcul = new Calculs(position, date);

		// Message d'erreur s'il n'y a pas de soleil a l'heure désirée:
		if ((date.getHeure() < calcul.getHeureLever())
				|| (date.getHeure() > calcul.getHeureCoucher())) {
			throw new ShadowDrawingException("Le soleil n'est pas levé à cette heure-ci !");
		}
		
		//RelativeAzimuth computation
		double relativeAzimuth = imageInfos.getOrientation() - (calcul.getAzimut() + 180);
		
		//Getting relativeAzimuth in [-180 180] range
		relativeAzimuth %= 360;
		if (relativeAzimuth > 180) {
			relativeAzimuth -= 360;
		}
		
		//Creating the ShadowDrawing Object depending on the cases
		if (Math.abs(relativeAzimuth) == 90) {
			this.shadowDrawing = new ShadowDrawingSide(relativeAzimuth, calcul.getHauteurSolaire());
		} else {
			//We get the sun position
			this.sunPosition = calculateSunPosition(relativeAzimuth, calcul.getHauteurSolaire(), image.getHeight(), image.getWidth(), focal);
			
			double yHorizon = (double)image.getHeight()/2;
			
			// si le soleil est de face et plus bas qu'un point de la géométrie,
			// ou de dos et plus haut qu'un pt de la geometrie,
			// on envoie un message d'erreur:
			if (((this.sunPosition.getY() >= getHighest(imageInfos.getFaces())) && (this.sunPosition.getY() < yHorizon))
			// si le soleil est de face et trop bas
					|| ((this.sunPosition.getY() <= getLowest(imageInfos.getFaces())) && (this.sunPosition.getY() > yHorizon)))
			// si le soleil est de dos et trop bas
			{
				throw new ShadowDrawingException(
						"Le soleil est trop bas à cette heure-ci pour la géométrie considérée.");
			}
			
			Point projeteSoleilHorizon = new Point(this.sunPosition.getX(), yHorizon);
			
			this.coupleSoleil = new Couple(this.sunPosition, projeteSoleilHorizon);
			
			this.shadowDrawing = new ShadowDrawingFront(this.coupleSoleil);
		}
	}
	
	protected Point calculateSunPosition(double relativeAzimuth, double altitude, int imageHeight, int imageWidth, double focal) {
		// Nous calculons tout d'abord la position du Soleil en mm sur la photo:
		double positionX;
		double positionY;
		// Les paramètres de la photo hauteur*largeur:
		double largeur = 0;
		double hauteur = 0;

		// Si on est en mode paysage, la largeur est plus grande que la hauteur
		if (imageWidth > imageHeight) {
			hauteur = 24;
			largeur = 36;
		}
		// Si on est en mode portrait, la hauteur est plus grande que la largeur
		if (imageWidth < imageHeight) {
			hauteur = 36;
			largeur = 24;
		}

		// On convertit en position X, Y sur une feuille 24*36...
		positionX = focal * Math.tan(relativeAzimuth * Math.PI / 180);
		positionY = focal * Math.tan(altitude * Math.PI / 180)
				/ Math.cos(relativeAzimuth * Math.PI / 180);

		// On repositionne les deux coordonnées: on place le point (0,0) en haut
		// à gauche.
		positionX = -positionX + largeur / 2;
		positionY = -positionY + hauteur / 2;
		System.out.println("positionX:" + positionX);
		System.out.println("positionY:" + positionY);
		System.out.println("hauteur:" + hauteur);
		System.out.println("largeur:" + largeur);
		System.out.println("pixelLargeur:" + imageWidth);
		System.out.println("pixelHauteur:" + imageHeight);
		// On convertit en nombres de pixels sur l'image:
		double x = (double)imageWidth * (positionX / largeur);
		double y = (double)imageHeight * (positionY / hauteur);
		
		return new Point(x, y);
	}

	/**
	 * Return the highest point in a list a faces
	 * 
	 * @param faces
	 * @return
	 */
	protected double getHighest(List<Face> faces) {
		double y = Float.MAX_VALUE;
		for (Face face : faces) {
			for (Couple couple : face.getCouples()) {
				if (couple.getPointAir().getY() < y) {
					y = couple.getPointAir().getY();
				}
			}
			// NOTE: le point est plus haut si ( en coordonnées image ) y est
			// plus petit!
		}
		return y;
	}

	/**
	 * Return the lowest point in a list a faces
	 * 
	 * @param faces
	 * @return
	 */
	protected double getLowest(List<Face> faces) {
		double y = Float.MIN_VALUE;
		for (Face face : faces) {
			for (Couple couple : face.getCouples()) {
				if (couple.getPointSol().getY() > y) {
					y = couple.getPointSol().getY();
				}
			}
			// NOTE: le point est plus bas si ( en coordonnées image ) y est
			// plus grand!
		}
		return y;
	}
	
	/**
	 * @return the shadowDrawing
	 */
	public ShadowDrawing getShadowDrawing() {
		return shadowDrawing;
	}

	/**
	 * @return the sunPosition
	 */
	public Point getSunPosition() {
		return sunPosition;
	}

	/**
	 * @return the coupleSoleil
	 */
	public Couple getCoupleSoleil() {
		return coupleSoleil;
	}
}
