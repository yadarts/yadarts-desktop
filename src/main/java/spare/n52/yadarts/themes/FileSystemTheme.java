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
	
	private Image boardHiImage;
	private Image boardMImage;
	private Image boardLoImage;
	private Image backgroundImage;
	private Image backgroundAltImage;
	private Image cornerTopLeftImage;
	private Image borderLeftImage;


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

	public File getBoardHiFile() {
		return checkAndReturnExistingFile(this.boardHiFile);
	}

	public File getBoardMFile() {
		return checkAndReturnExistingFile(boardMFile);
	}

	public File getBoardLoFile() {
		return checkAndReturnExistingFile(boardLoFile);
	}
	
	private File checkAndReturnExistingFile(File f) {
		if (f != null && f.exists() && !f.isDirectory()) {
			return f;
		}
		return null;
	}


}
