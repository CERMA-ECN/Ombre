package fr.ecn.ombre.android;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import android.graphics.Bitmap;

import fr.ecn.ombre.android.image.BitmapImage;
import fr.ecn.ombre.android.utils.ImageLoader;
import fr.ecn.ombre.core.image.Image;
import fr.ecn.ombre.core.model.Couple;
import fr.ecn.ombre.core.model.Face;
import fr.ecn.ombre.core.model.ImageInfos;
import fr.ecn.ombre.core.model.Point;
import fr.ecn.ombre.core.shadows.ShadowDrawing;
import fr.ecn.ombre.core.shadows.ShadowDrawingException;
import fr.ecn.ombre.core.shadows.ShadowDrawingFactory;

public class ResultController {
	
	/**
	 * A callable that create a ResultController Object
	 * 
	 * @author jerome
	 *
	 */
	public static class ResultCallable implements Callable<ResultController> {
		
		protected ImageInfos imageInfos;
		protected Calendar time;
		protected boolean shadowsOnWalls;
		protected boolean expendToStreet;
		
		/**
		 * @param imageInfos
		 * @param time
		 * @param shadowsOnWalls
		 * @param expendToStreet
		 */
		public ResultCallable(ImageInfos imageInfos, Calendar time, boolean shadowsOnWalls,
				boolean expendToStreet) {
			super();
			this.imageInfos = imageInfos;
			this.time = time;
			this.shadowsOnWalls = shadowsOnWalls;
			this.expendToStreet = expendToStreet;
		}

		public ResultController call() throws Exception {
			return new ResultController(imageInfos, time, shadowsOnWalls, expendToStreet);
		}
		
	}
	
	protected ImageInfos imageInfos;
	protected Calendar time;

	protected Bitmap bitmap;
	
	protected Point sunPosition;
	
	protected List<Face> shadows;

	public ResultController(ImageInfos imageInfos, Calendar time, boolean shadowsOnWalls, boolean expendToStreet) throws ShadowDrawingException {
		this.imageInfos = imageInfos;
		this.time = time;
		
		this.bitmap = ImageLoader.loadResized(imageInfos.getPath(), 600);
		
		this.calculOmbre(imageInfos, time, shadowsOnWalls, expendToStreet);
	}

	protected void calculOmbre(ImageInfos imageInfos, Calendar time, boolean shadowsOnWalls, boolean expendToStreet) throws ShadowDrawingException {
		this.shadows = new LinkedList<Face>();
		
		// test si la geometrie n'est pas vide:
		if (imageInfos.getFaces().isEmpty()) {
			throw new ShadowDrawingException("Vous devez rentrer au moins une face");
		}

		Image image = new BitmapImage(this.bitmap);
		
		ShadowDrawingFactory sdf = new ShadowDrawingFactory(image, imageInfos, time);

		ShadowDrawing shadowDrawing = sdf.getShadowDrawing();
		
		this.sunPosition = sdf.getSunPosition();
		
		// =============================================================//
		// CALCUL DE L'OMBRE //
		// =============================================================//
		
		// calcul proprement dit:
		for (Face f : imageInfos.getFaces()) {

			// premier calcul de l'ombre au sol, avec test si l'inclinaison du
			// rayon n'est pas bonne.. ( pour cas limite ou pente rayon<pente
			// fuyante==>bug!)
			Face faceOmbre = shadowDrawing.drawShadow(f);
			// si l'ombre n'est pas dans le bon sens, on s'arrete la.
			if (!faceOmbre.isOutside()) {
				continue;
			}
			
			this.shadows.add(f);
			
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
						Couple[] vectPointOmbreF2 = shadowDrawing.calculOmbreMur(f, faceOmbre, f2);
						List<Face> ombre = shadowDrawing.determinationOmbreMur(f, faceOmbre, f2, vectPointOmbreF2,
								sdf.getCoupleSoleil());
						this.shadows.addAll(ombre);
					}
				} // fin boucle sur autres faces que f
			} else { // si pas de "wall", on rajoute juste l'ombre si elle est
				this.shadows.add(faceOmbre);
			} // fin if "wall"
		} // fin boucle de départ sur les faces.
		
		//Expend shadows to street if requested
		if (expendToStreet) {
			List<Face> faces = new LinkedList<Face>();
			for (Face face : this.shadows) {
				faces.add(face.expandToStreet(image));
			}
			this.shadows.addAll(faces);
		}
	}
	
	/**
	 * @return the imageInfos
	 */
	public ImageInfos getImageInfos() {
		return imageInfos;
	}

	/**
	 * @return the time
	 */
	public Calendar getTime() {
		return time;
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
	 * @return the ombres
	 */
	public List<Face> getShadows() {
		return shadows;
	}

}
