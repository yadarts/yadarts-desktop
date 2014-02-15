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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class FileSystemTheme extends Theme {

	private File baseDir;
	private File boardHiFile;
	private File boardMFile;
	private File boardLoFile;
	private File backgroundImageFile;
	private File backgroundAltImageFile;
	
	private File cornerTopLeftImageFile;
	private File borderLeftImageFile;
	private File cornerTopRightImageFile;
	private File borderTopImageFile;
	private File borderBottomImageFile;
	private File cornerBottomRightImageFile;
	private File borderRightImageFile;
	private File cornerBottomLeftImageFile;
	
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



	public FileSystemTheme(String path) throws URISyntaxException {
		this(new File(Theme.class.getResource(path).toURI()));
	}

	public FileSystemTheme(File c) {
		this.baseDir = c;
		resolveImageFiles();
	}

	private void resolveImageFiles() {
		this.boardHiFile = new File(baseDir, BOARD_HI);
		this.boardMFile = new File(baseDir, BOARD_M);
		this.boardLoFile = new File(baseDir, BOARD_LO);
		this.backgroundImageFile = new File(baseDir, BACKGROUND);
		this.backgroundAltImageFile = new File(baseDir, BACKGROUND_ALT);
		this.cornerTopLeftImageFile = new File(baseDir, CORNER_TOP_LEFT);
		this.borderLeftImageFile = new File(baseDir, BORDER_LEFT);
		
		this.cornerTopRightImageFile = new File(baseDir, CORNER_TOP_RIGHT);
		this.borderTopImageFile = new File(baseDir, BORDER_TOP);
		this.borderBottomImageFile = new File(baseDir, BORDER_BOTTOM);
		this.cornerBottomRightImageFile = new File(baseDir, CORNER_BOTTOM_RIGHT);
		this.borderRightImageFile = new File(baseDir, BORDER_RIGHT);
		this.cornerBottomLeftImageFile = new File(baseDir, CORNER_BOTTOM_LEFT);
		
		assertFilesExist();
	}
	
	private void assertFilesExist() {
		assertFileExists(this.boardHiFile);
		assertFileExists(this.boardMFile);
		assertFileExists(this.boardLoFile);
		assertFileExists(this.backgroundImageFile);
		assertFileExists(this.backgroundAltImageFile);
		assertFileExists(this.cornerTopLeftImageFile);
		assertFileExists(this.borderLeftImageFile);
	}

	private void assertFileExists(File f) {
		if (f == null || !f.exists() || f.isDirectory()) {
			throw new IllegalStateException("Missing theme file.");
		}
	}

	/**
	 * the hi-res version of the dart board
	 */
	public synchronized Image getBoardHi(Display d) throws FileNotFoundException {
		if (this.boardHiImage == null) {
			this.boardHiImage = new Image(d, new FileInputStream(boardHiFile));
		}
		
		return this.boardHiImage;
	}
	
	/**
	 * the med-res version of the dart board
	 */
	public synchronized Image getBoardM(Display d) throws FileNotFoundException {
		if (this.boardMImage == null) {
			this.boardMImage = new Image(d, new FileInputStream(boardMFile));
		}
		
		return this.boardMImage;
	}
	
	/**
	 * the lo-res version of the dart board
	 */
	public synchronized Image getBoardLo(Display d) throws FileNotFoundException {
		if (this.boardLoImage == null) {
			this.boardLoImage = new Image(d, new FileInputStream(boardLoFile));
		}
		
		return this.boardLoImage;
	}
	
	public synchronized Image getBackground(Display d) throws FileNotFoundException {
		if (this.backgroundImage == null) {
			this.backgroundImage = new Image(d, new FileInputStream(backgroundImageFile));
		}
		
		return this.backgroundImage;
	}
	
	public synchronized Image getBackgroundAlt(Display d) throws FileNotFoundException {
		if (this.backgroundAltImage == null) {
			this.backgroundAltImage = new Image(d, new FileInputStream(backgroundAltImageFile));
		}
		
		return this.backgroundAltImage;
	}
	
	public synchronized Image getCornerTopLeft(Display d) throws FileNotFoundException {
		if (this.cornerTopLeftImage == null) {
			this.cornerTopLeftImage = new Image(d, new FileInputStream(cornerTopLeftImageFile));
		}
		
		return this.cornerTopLeftImage;
	}
	
	public synchronized Image getBorderLeft(Display d) throws FileNotFoundException {
		if (this.borderLeftImage == null) {
			this.borderLeftImage = new Image(d, new FileInputStream(borderLeftImageFile));
		}
		
		return this.borderLeftImage;
	}


	@Override
	public Image getCornerTopRight(Display d)
			throws FileNotFoundException {
		if (this.cornerTopRightImage == null) {
			this.cornerTopRightImage = new Image(d, new FileInputStream(cornerTopRightImageFile));
		}
		
		return this.cornerTopRightImage;
	}

	@Override
	public Image getBorderTop(Display d) throws FileNotFoundException {
		if (this.borderTopImage == null) {
			this.borderTopImage = new Image(d, new FileInputStream(borderTopImageFile));
		}
		
		return this.borderTopImage;
	}

	@Override
	public Image getCornerBottomLeft(Display d)
			throws FileNotFoundException {
		if (this.cornerBottomLeftImage == null) {
			this.cornerBottomLeftImage = new Image(d, new FileInputStream(cornerBottomLeftImageFile));
		}
		
		return this.cornerBottomLeftImage;
	}

	@Override
	public Image getBorderBottom(Display d) throws FileNotFoundException {
		if (this.borderBottomImage == null) {
			this.borderBottomImage = new Image(d, new FileInputStream(borderBottomImageFile));
		}
		
		return this.borderBottomImage;
	}

	@Override
	public Image getCornerBottomRight(Display d)
			throws FileNotFoundException {
		if (this.cornerBottomRightImage == null) {
			this.cornerBottomRightImage = new Image(d, new FileInputStream(cornerBottomRightImageFile));
		}
		
		return this.cornerBottomRightImage;
	}

	@Override
	public Image getBorderRight(Display d) throws FileNotFoundException {
		if (this.borderRightImage == null) {
			this.borderRightImage = new Image(d, new FileInputStream(borderRightImageFile));
		}
		
		return this.borderRightImage;
	}

}
