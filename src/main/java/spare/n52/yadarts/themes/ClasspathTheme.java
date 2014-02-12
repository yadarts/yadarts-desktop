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
import org.eclipse.swt.widgets.Display;

public class ClasspathTheme extends Theme {
	
	private Image boardHiImage;
	private Image boardMImage;
	private Image boardLoImage;
	private Image backgroundImage;
	private Image backgroundAltImage;
	private Image cornerTopLeftImage;
	private Image borderLeftImage;
	private String baseResource;
	private String boardHiResource;
	private String boardMResource;
	private String boardLoResource;
	private String backgroundImageResource;
	private String backgroundAltImageResource;
	private String cornerTopLeftImageResource;
	private String borderLeftImageResource;


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
	}

	private void assertFileExists(String f) {
		URL url = getClass().getResource(f);
		if (url == null) {
			throw new IllegalStateException("Missing theme resource.");
		}
	}

	/**
	 * the hi-res version of the dart board
	 */
	public synchronized Image getBoardHi(Display d) throws FileNotFoundException {
		if (this.boardHiImage == null) {
			this.boardHiImage = new Image(d, getClass().getResourceAsStream(boardHiResource));
		}
		
		return this.boardHiImage;
	}
	
	/**
	 * the med-res version of the dart board
	 */
	public synchronized Image getBoardM(Display d) throws FileNotFoundException {
		if (this.boardMImage == null) {
			this.boardMImage = new Image(d, getClass().getResourceAsStream(boardMResource));
		}
		
		return this.boardMImage;
	}
	
	/**
	 * the lo-res version of the dart board
	 */
	public synchronized Image getBoardLo(Display d) throws FileNotFoundException {
		if (this.boardLoImage == null) {
			this.boardLoImage = new Image(d, getClass().getResourceAsStream(boardLoResource));
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



}
