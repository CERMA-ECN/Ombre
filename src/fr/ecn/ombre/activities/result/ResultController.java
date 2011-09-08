package fr.ecn.ombre.activities.result;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

import com.ei3info.gsun.Calculs;
import com.ei3info.gsun.PositionUtilisateur;
import com.ei3info.gsun.Temps;

import fr.ecn.ombre.image.utils.ImageLoader;
import fr.ecn.ombre.model.Couple;
import fr.ecn.ombre.model.Face;
import fr.ecn.ombre.model.ImageInfos;
import fr.ecn.ombre.model.Point;

public class ResultController {

	protected Bitmap bitmap;
	
	protected Point sunPosition;
	protected int yHorizon;
	
	protected List<Face> selectedFaces;
	protected List<Face> shadows;

	public ResultController(ImageInfos imageInfos, Calendar time, boolean shadowsOnWalls, boolean expendToStreet) {
		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);

		this.calculOmbre(imageInfos, time, shadowsOnWalls, expendToStreet);
	}

	protected void calculOmbre(ImageInfos imageInfos, Calendar time, boolean shadowsOnWalls, boolean expendToStreet) {
		this.selectedFaces = new LinkedList<Face>();
		this.shadows = new LinkedList<Face>();
		
		// test si la geometrie n'est pas vide:
		if (imageInfos.getFaces().isEmpty()) {
			throw new RuntimeException("La géométrie est vide !");
		}

		Bitmap image = this.bitmap;

		// recuperation des parametres:
		// focale par d�faut(en mm):
		double focale = 37;
		// position GPS:
		// position par défaut:
		PositionUtilisateur position = new PositionUtilisateur(47.25, -1.55); // Nantes
		// lecture de la position:
		position.setLatitude(imageInfos.getLatitude().getDecimalDegrees());
		position.setLongitude(imageInfos.getLongitude().getDecimalDegrees());
		Log.i("Ombre", "PositionUtilisateur : " + position.getLatitude() + ", " + position.getLongitude());

		// jour, mois, heure, direction photo :
		int Jour = time.get(Calendar.DAY_OF_MONTH);
		int Mois = time.get(Calendar.MONTH) + 1;
		int Heure = time.get(Calendar.HOUR_OF_DAY);
		double dir = imageInfos.getOrientation();
		Temps date = new Temps(Jour, Mois);
		date.setHeure(Heure);
		Log.i("Ombre", "Jour : " + Jour + "; Mois : " + Mois + "; Heure : " + Heure + "; Dir : "
				+ dir);
		// =============================================================//
		// CONVERSION date--> position soleil
		// Utilisation des classes des ei3...
		// =============================================================//

		// calcul azimut, hauteur:
		Calculs calcul = new Calculs(position, date);

		// Message d'erreur s'il n'y a pas de soleil a l'heure d�sir�e:
		if ((date.getHeure() < calcul.getHeureLever())
				|| (date.getHeure() > calcul.getHeureCoucher())) {
			throw new RuntimeException("Le soleil n'est pas levé à cette heure-ci !");
		}
		
		// conversion Hauteur/Azimut en une position image du soleil:
		Conversion positionSoleil = new Conversion(calcul.getHauteurSolaire(), calcul.getAzimut(),
				dir, image, focale);
		Log.i("Ombre", "Coordonnées soleil : "+positionSoleil.X+" , "+positionSoleil.Y);
		
		// =============================================================//
		// CALCUL DE L'OMBRE //
		// =============================================================//

		/**
		 * ====================================================================
		 * =============* Premier Cas: Soleil de face ou de dos
		 * ==================
		 * ================================================================
		 */

		// POSITION SOLEIL
		Point Soleil = new Point(positionSoleil.X, positionSoleil.Y);
		//Saving sun position for drawing
		this.sunPosition = Soleil;
		// projection du soleil sur la ligne d'horizon
		int YHorizon = image.getHeight() / 2;
		this.yHorizon = YHorizon;

		// si le soleil est de face et plus bas qu'un point de la géométrie,
		// ou de dos et plus haut qu'un pt de la geometrie,
		// on envoie un message d'erreur:
		if (((positionSoleil.Y >= this.getHighest(imageInfos.getFaces())) && (positionSoleil.Y < YHorizon))
		// si le soleil est de face et trop bas
				|| ((positionSoleil.Y <= this.getLowest(imageInfos.getFaces())) && (positionSoleil.Y > YHorizon)))
		// si le soleil est de dos et trop bas
		{
			throw new RuntimeException(
					"Le soleil est trop bas à cette heure-ci pour la géométrie considérée.");
		}
		Point projeteSoleilHorizon = new Point(Soleil.getX(), YHorizon);
		Couple coupleSoleil = new Couple(Soleil, projeteSoleilHorizon);
		
		// calcul proprement dit:
		List<Face> ombre = this.shadows;
		for (Face f : imageInfos.getFaces()) {

			// premier calcul de l'ombre au sol, avec test si l'inclinaison du
			// rayon n'est pas bonne.. ( pour cas limite ou pente rayon<pente
			// fuyante==>bug!)
			Face faceOmbre = f.calculOmbreDirect(coupleSoleil);
			// si l'ombre n'est pas dans le bon sens, on s'arrete la.
			if (!faceOmbre.isOutside()) {
				continue;
			}
			
			this.selectedFaces.add(f);
			
			/**
			 * On ne fait la suite que si la case "wall" est cochée, autrement
			 * dit, que si on veut voir l'ombre sur les murs
			 * 
			 * Si l'on a une seul face le calcul est innutile
			 */
			if (shadowsOnWalls && imageInfos.getFaces().size() > 1) {
				// on boucle sur les faces autres que f :
				for (Face f2 : imageInfos.getFaces()) {
					if (f2 != f) {
						// On calcule les points qui de l'ombre qui se
						// retrouvent sur la face f2 de la géométrie, et
						// on les met dans vectPointOmbreF2
						List<Couple> vectPointOmbreF2 = new LinkedList<Couple>();
						f.calculOmbreMur(vectPointOmbreF2, f2, faceOmbre);
						f.determinationOmbreMur(vectPointOmbreF2, faceOmbre, f2, ombre,
								coupleSoleil);

					}
				} // fin boucle sur autres faces que f
			} else { // si pas de "wall", on rajoute juste l'ombre si elle est
				ombre.add(faceOmbre);
			} // fin if "wall"
		} // fin boucle de départ sur les faces.

		/**
		 * ====================================================================
		 * =============* Deuxième Cas: Soleil de côté (ensoleillement frontal)
		 * ==>ABANDONNE car quasiment inutile!( cas ou azimutRelatif=90degres,
		 * ou hauteur=90degres et nécessiterait de changer beaucoup de méthodes
		 * si on tient compte des ombres sur les murs...
		 * 
		 * Construction de l'ombre de base facile ( cf. site internet de
		 * référence) Faisable si quelqu'un veut reprendre ce cas...
		 * ============
		 * ==========================================================
		 * ============
		 */
		/*
		 * blablabla...
		 */
		
		//Expend shadows to street if requested
		if (expendToStreet) {
			List<Face> faces;
			
			faces = new LinkedList<Face>();
			for (Face face : this.selectedFaces) {
				faces.add(face.expandToStreet(image));
			}
			this.selectedFaces.addAll(faces);
			
			faces = new LinkedList<Face>();
			for (Face face : this.shadows) {
				faces.add(face.expandToStreet(image));
			}
			this.shadows.addAll(faces);
		}
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
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * @return the position of the sun
	 */
	public Point getSunPosition() {
		return sunPosition;
	}

	/**
	 * @return the selectedFaces
	 */
	public List<Face> getSelectedFaces() {
		return selectedFaces;
	}

	/**
	 * @return the ombres
	 */
	public List<Face> getShadows() {
		return shadows;
	}

}
