package fr.ecn.ombre.core.shadows;

import fr.ecn.ombre.model.Face;

public abstract class ShadowDrawing {

	/**
	 * Method that compute the shadow of a face on the floor
	 * 
	 * @param face
	 * @return
	 */
	public abstract Face drawShadow(Face face);

}
