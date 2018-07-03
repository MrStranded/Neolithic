package main.engine;

import main.data.Data;
import main.data.proto.Value;
import main.environment.world.Entity;

import java.awt.*;

/**
 * Created by michael1337 on 27/10/17.
 */
public class EntityValueProcessor {

	public static Color getEntityColor(Entity entity) {
		Color c = new Color(0,0,0);
		if (entity == null) return c;

		Value color = Data.getContainer(entity.getId()).tryToGet("color");
		int r = 0, g = 0, b = 0;
		if (color != null) {
			r = color.tryToGetInt(0);
			g = color.tryToGetInt(1);
			b = color.tryToGetInt(2);
			c = new Color(r,g,b);
		}
		return c;
	}

}
