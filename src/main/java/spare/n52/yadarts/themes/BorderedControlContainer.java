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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A convenience class to visually wrap a {@link Control} component
 * with a {@link Theme}-based border.
 * 
 * A sub-class of this shall provide the {@link Control} to be wrapped
 * via the {@link #createContents(Composite)} method.
 * 
 * <br/>
 * <br/>
 * Example:
 * <br/>
 * <br/>
 * <code>
 * 
 * new BorderedControlContainer(theShell, SWT.NONE) {<br/>
 * 			@Override<br/>
 * 			protected Control createContents(Composite parent) {<br/>
 * 				Label l = new Label(parent, SWT.NONE);<br/>
 * 				l.setText("HI!!!!!");<br/>
 * 				return l;<br/>
 * 			}<br/>
 * 		};<br/>
 * </code>
 * 
 */
public abstract class BorderedControlContainer extends Composite {

	private static final Logger logger = LoggerFactory.getLogger(BorderedControlContainer.class);
	
	private static Shell theShell;
	private static Image bgAlt;
	private static Image cornerTopLeft;
	private static Image cornerTopRight;
	private static Image cornerBottomRight;
	private static Image cornerBottomLeft;
	private static Image borderLeft;
	private static Image borderTop;
	private static Image borderRight;
	private static Image borderBottom;
	private boolean imagesLoaded;
	
	private void readImageResources() {
		try {
			bgAlt = Theme.getCurrentTheme().getBackgroundAlt(getDisplay());
			
			cornerTopLeft = Theme.getCurrentTheme().getCornerTopLeft(getDisplay());
			cornerTopRight = rotateImage(cornerTopLeft, 90);
			cornerBottomRight = rotateImage(cornerTopRight, 90);
			cornerBottomLeft = rotateImage(cornerBottomRight, 90);

			borderLeft = Theme.getCurrentTheme().getBorderLeft(getDisplay());
			borderTop = rotateImage(borderLeft, 90);
			borderRight = rotateImage(borderTop, 90);
			borderBottom = rotateImage(borderRight, 90);
		} catch (FileNotFoundException e) {
			logger.warn(e.getMessage(), e);
		}
		
	}

	public static void main(String[] args) {
		final Display display = Display.getDefault();
		theShell = new Shell(display);
		
		theShell.setLayout(new FillLayout());

		theShell.setMinimumSize(800, 600);

		new BorderedControlContainer(theShell, SWT.NONE) {

			@Override
			protected Control createContents(Composite parent) {
				Label l = new Label(parent, SWT.NONE);
				l.setText("HI!°!!!!");
				return l;
			}
			
		};
		
		theShell.setSize(800, 600);

		Rectangle splashBounds = theShell.getBounds();
		Rectangle displayBounds = theShell.getDisplay().getPrimaryMonitor()
				.getBounds();
		int x = (displayBounds.width - splashBounds.width) / 2;
		int y = (displayBounds.height - splashBounds.height) / 2;
		theShell.setLocation(x, y);

		theShell.open();
		while (!theShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	

	public BorderedControlContainer(Composite parent, int style) {
		super(parent, style);

		synchronized (BorderedControlContainer.class) {
			if (!imagesLoaded) {
				readImageResources();
				imagesLoaded = true;
			}
		}
		
		initLayout();
	}

	private void initLayout() {
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		this.setLayout(layout);


		/*
		 * top row
		 */
		Label topLeft = new Label(this, SWT.NONE);
		topLeft.setImage(cornerTopLeft);

		Label top = new Label(this, SWT.NONE);
		top.setBackgroundImage(borderTop);
		// top.setBackground(new Color(shell.getDisplay(), new RGB(0, 255, 0)));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		top.setLayoutData(gd);

		Label topRight = new Label(this, SWT.NONE);
		topRight.setImage(cornerTopRight);

		/*
		 * mid row
		 */
		Control midLeft = new Label(this, SWT.NONE);
		midLeft.setBackgroundImage(borderLeft);
		gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		midLeft.setLayoutData(gd);

		Control mid = createContents(this);
		mid.setBackgroundImage(bgAlt);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		mid.setLayoutData(gd);

		Label midRight = new Label(this, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		midRight.setBackgroundImage(borderRight);
		midRight.setLayoutData(gd);

		/*
		 * bottom row
		 */
		Label bottomLeft = new Label(this, SWT.NONE);
		bottomLeft.setImage(cornerBottomLeft);

		Label bottom = new Label(this, SWT.NONE);
		bottom.setImage(borderBottom);
		bottom.setBackgroundImage(borderBottom);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		bottom.setLayoutData(gd);

		Label bottomRight = new Label(this, SWT.NONE);
		bottomRight.setImage(cornerBottomRight);

		this.pack();
	}

	protected abstract Control createContents(Composite parent);

	private Image rotateImage(Image src, float angle) {
		return new Image(this.getDisplay(), rotate(src.getImageData(),
				SWT.RIGHT));
	}

	static ImageData rotate(ImageData srcData, int direction) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width
				* bytesPerPixel : srcData.height * bytesPerPixel;
		byte[] newData = new byte[(direction == SWT.DOWN) ? srcData.height
				* destBytesPerLine : srcData.width * destBytesPerLine];
		int width = 0, height = 0;
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				switch (direction) {
				case SWT.LEFT: // left 90 degrees
					destX = srcY;
					destY = srcData.width - srcX - 1;
					width = srcData.height;
					height = srcData.width;
					break;
				case SWT.RIGHT: // right 90 degrees
					destX = srcData.height - srcY - 1;
					destY = srcX;
					width = srcData.height;
					height = srcData.width;
					break;
				case SWT.DOWN: // 180 degrees
					destX = srcData.width - srcX - 1;
					destY = srcData.height - srcY - 1;
					width = srcData.width;
					height = srcData.height;
					break;
				}
				destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
				System.arraycopy(srcData.data, srcIndex, newData, destIndex,
						bytesPerPixel);
			}
		}
		// destBytesPerLine is used as scanlinePad to ensure that no padding is
		// required
		return new ImageData(width, height, srcData.depth, srcData.palette,
				srcData.scanlinePad, newData);
	}


}
