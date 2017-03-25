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
package spare.n52.yadarts.themes;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class ClasspathTheme extends Theme {
	

	private Image boardHiImage;
	private Image boardMImage;
	private Image boardLoImage;
	private Image backgroundImage;
	private Image backgroundAltImage;
	private Image cornerTopLeftImage;
	private Image borderLeftImage;
	private Image cornerTopRightImage;
	private Image borderTopImage;
	private Image cornerBottomLeftImage;
	private Image borderBottomImage;
	private Image cornerBottomRightImage;
	private Image borderRightImage;
	
	private String baseResource;
	private String boardHiResource;
	private String boardMResource;
	private String boardLoResource;
	private String backgroundImageResource;
	private String backgroundAltImageResource;
	private String cornerTopLeftImageResource;
	private String borderLeftImageResource;
	private String cornerTopRightImageResource = null;
	private String borderTopImageResource = null;
	private String cornerBottomLeftImageResource = null;
	private String borderBottomImageResource = null;
	private String cornerBottomRightImageResource = null;
	private String borderRightImageResource = null;
	
	


	public ClasspathTheme(String path) throws URISyntaxException {
		this.baseResource = path;
		resolveImageResources();
	}

	private void resolveImageResources() {
		this.boardHiResource = baseResource.concat("/").concat(BOARD_HI);
		this.boardMResource = baseResource.concat("/").concat(BOARD_M);
		this.boardLoResource = baseResource.concat("/").concat(BOARD_LO);
		this.backgroundImageResource = baseResource.concat("/").concat(BACKGROUND);
		this.backgroundAltImageResource = baseResource.concat("/").concat(BACKGROUND_ALT);
		
		this.cornerTopLeftImageResource = baseResource.concat("/").concat(CORNER_TOP_LEFT);
		this.borderLeftImageResource = baseResource.concat("/").concat(BORDER_LEFT);
		this.cornerTopRightImageResource = baseResource.concat("/").concat(CORNER_TOP_RIGHT);
		this.borderTopImageResource = baseResource.concat("/").concat(BORDER_TOP);
		this.cornerBottomLeftImageResource = baseResource.concat("/").concat(CORNER_BOTTOM_RIGHT);
		this.borderBottomImageResource = baseResource.concat("/").concat(BORDER_BOTTOM);
		this.cornerBottomRightImageResource = baseResource.concat("/").concat(CORNER_BOTTOM_RIGHT);
		this.borderRightImageResource = baseResource.concat("/").concat(BORDER_RIGHT);
		
		assertFilesExist();
	}
	
	private void assertFilesExist() {
		assertFileExists(this.boardHiResource);
		assertFileExists(this.boardMResource);
		assertFileExists(this.boardLoResource);
		assertFileExists(this.backgroundImageResource);
		assertFileExists(this.backgroundAltImageResource);
		
		assertFileExists(this.cornerTopLeftImageResource);
		assertFileExists(this.borderLeftImageResource);
		
//		assertFileExists(this.cornerTopRightImageResource);
//		assertFileExists(this.borderTopImageResource);
//		
//		assertFileExists(this.cornerBottomLeftImageResource);
//		assertFileExists(this.borderBottomImageResource);
//		
//		assertFileExists(this.cornerBottomRightImageResource);
//		assertFileExists(this.borderRightImageResource);
	}

	private void assertFileExists(String f) {
		URL url = getClass().getResource(f);
		if (url == null) {
			throw new IllegalStateException("Missing theme resource: "+f);
		}
	}

	/**
	 * the hi-res version of the dart board
	 */
	public synchronized Image getBoardHi(Display d) throws FileNotFoundException {
		if (this.boardHiImage == null) {
                        ImageData data = new ImageData(getClass().getResourceAsStream(boardHiResource));
			this.boardHiImage = new Image(d, data, data);
		}
		
		return this.boardHiImage;
	}
	
	/**
	 * the med-res version of the dart board
	 */
	public synchronized Image getBoardM(Display d) throws FileNotFoundException {
		if (this.boardMImage == null) {
                        ImageData data = new ImageData(getClass().getResourceAsStream(boardMResource));
			this.boardMImage = new Image(d, data);
		}
		
		return this.boardMImage;
	}
	
	/**
	 * the lo-res version of the dart board
	 */
	public synchronized Image getBoardLo(Display d) throws FileNotFoundException {
		if (this.boardLoImage == null) {
                        ImageData data = new ImageData(getClass().getResourceAsStream(boardLoResource));
			this.boardLoImage = new Image(d, data, data);
		}
		
		return this.boardLoImage;
	}
	
	public synchronized Image getBackground(Display d) throws FileNotFoundException {
		if (this.backgroundImage == null) {
			this.backgroundImage = new Image(d, getClass().getResourceAsStream(backgroundImageResource));
		}
		
		return this.backgroundImage;
	}
	
	public synchronized Image getBackgroundAlt(Display d) throws FileNotFoundException {
		if (this.backgroundAltImage == null) {
			this.backgroundAltImage = new Image(d, getClass().getResourceAsStream(backgroundAltImageResource));
		}
		
		return this.backgroundAltImage;
	}
	
	public synchronized Image getCornerTopLeft(Display d) throws FileNotFoundException {
		if (this.cornerTopLeftImage == null) {
			this.cornerTopLeftImage = new Image(d, getClass().getResourceAsStream(cornerTopLeftImageResource));
		}
		
		return this.cornerTopLeftImage;
	}
	
	public synchronized Image getBorderLeft(Display d) throws FileNotFoundException {
		if (this.borderLeftImage == null) {
			this.borderLeftImage = new Image(d, getClass().getResourceAsStream(borderLeftImageResource));
		}
		
		return this.borderLeftImage;
	}

	@Override
	public Image getCornerTopRight(Display d)
			throws FileNotFoundException {
		if (this.cornerTopRightImage == null) {
			this.cornerTopRightImage = new Image(d, getClass().getResourceAsStream(cornerTopRightImageResource));
		}
		
		return this.cornerTopRightImage;
	}

	@Override
	public Image getBorderTop(Display d) throws FileNotFoundException {
		if (this.borderTopImage == null) {
			this.borderTopImage = new Image(d, getClass().getResourceAsStream(borderTopImageResource));
		}
		
		return this.borderTopImage;
	}

	@Override
	public Image getCornerBottomLeft(Display d)
			throws FileNotFoundException {
		if (this.cornerBottomLeftImage == null) {
			this.cornerBottomLeftImage = new Image(d, getClass().getResourceAsStream(cornerBottomLeftImageResource));
		}
		
		return this.cornerBottomLeftImage;
	}

	@Override
	public Image getBorderBottom(Display d) throws FileNotFoundException {
		if (this.borderBottomImage == null) {
			this.borderBottomImage = new Image(d, getClass().getResourceAsStream(borderBottomImageResource));
		}
		
		return this.borderBottomImage;
	}

	@Override
	public Image getCornerBottomRight(Display d)
			throws FileNotFoundException {
		if (this.cornerBottomRightImage == null) {
			this.cornerBottomRightImage = new Image(d, getClass().getResourceAsStream(cornerBottomRightImageResource));
		}
		
		return this.cornerBottomRightImage;
	}

	@Override
	public Image getBorderRight(Display d) throws FileNotFoundException {
		if (this.borderRightImage == null) {
			this.borderRightImage = new Image(d, getClass().getResourceAsStream(borderRightImageResource));
		}
		
		return this.borderRightImage;
	}



}
