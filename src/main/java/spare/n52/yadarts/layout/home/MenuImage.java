/**
 * Copyright 2014 the staff of 52°North Initiative for Geospatial Open
 * Source Software GmbH in their free time
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spare.n52.yadarts.layout.home;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class MenuImage {

	private Image image;
	private Image activeImage;
	private int xStart;
	private int yStart;
	private int[] scaledBounds;
	
	public MenuImage(String fileName, Display d, int xStart, int yStart) {
		this(new Image(d, MenuImage.class.getResourceAsStream(
				String.format("/images/%s.png", fileName))),
				new Image(d,MenuImage.class.getResourceAsStream(
						String.format("/images/%s_active.png", fileName))), xStart, yStart);
	}
	
	public MenuImage(Image image, Image activeImage, int xStart, int yStart) {
		this.image = image;
		this.activeImage = activeImage;
		this.xStart = xStart;
		this.yStart = yStart;
	}

	public Image getImage() {
		return image;
	}
	
	public Image getActiveImage() {
		return activeImage;
	}
	
	public int[] getScaledBounds() {
		return scaledBounds;
	}

	public void calculateScaledBounds(double targetScale, int globalXStart, int globalYStart) {
		this.scaledBounds = new int[4];
		this.scaledBounds[0] = globalXStart + (int) (xStart/targetScale);
		this.scaledBounds[1] = globalYStart + (int) (yStart/targetScale);
		this.scaledBounds[2] = globalXStart + (int) (xStart/targetScale) + (int) (image.getBounds().width / targetScale);
		this.scaledBounds[3] = globalYStart + (int) (yStart/targetScale) + (int) (image.getBounds().height / targetScale);
	}
	
	
}
