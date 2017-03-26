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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
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
            cornerTopRight = Theme.getCurrentTheme().getCornerTopRight(getDisplay());
            cornerBottomRight = Theme.getCurrentTheme().getCornerBottomRight(getDisplay());
            cornerBottomLeft = Theme.getCurrentTheme().getCornerBottomLeft(getDisplay());
            
            borderLeft = Theme.getCurrentTheme().getBorderLeft(getDisplay());
            borderTop = Theme.getCurrentTheme().getBorderTop(getDisplay());
            borderRight = Theme.getCurrentTheme().getBorderRight(getDisplay());
            borderBottom = Theme.getCurrentTheme().getBorderBottom(getDisplay());
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage(), e);
        }
        
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        final Display display = Display.getDefault();
        theShell = new Shell(display);
        
        theShell.setLayout(new FillLayout());
        
        theShell.setMinimumSize(800, 600);
        theShell.setBackgroundImage(Theme.getCurrentTheme().getBackground(display));
        theShell.setBackgroundMode(SWT.BACKGROUND);
        
        new BorderedControlContainer(theShell, SWT.INHERIT_FORCE) {
            
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
        
        Canvas top = new Canvas(this, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.heightHint = 32;
        top.setLayoutData(gd);
        top.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                repeatDrawHorizontal(e.gc, borderTop, top.getSize().x);
            }
        });
        
        
        Label topRight = new Label(this, SWT.NONE);
        topRight.setImage(cornerTopRight);
        
        /*
        * mid row
        */
        Canvas midLeft = new Canvas(this, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, false, true);
        gd.widthHint = 32;
        midLeft.setLayoutData(gd);
        midLeft.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                repeatDrawVertical(e.gc, borderLeft, midLeft.getSize().y);
            }
        });
        
        Control mid = createContents(this);
        mid.setBackgroundImage(bgAlt);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        mid.setLayoutData(gd);
        
        Canvas midRight = new Canvas(this, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, false, true);
        gd.widthHint = 32;
        midRight.setLayoutData(gd);
        midRight.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                repeatDrawVertical(e.gc, borderRight, midRight.getSize().y);
            }
        });
        
        /*
        * bottom row
        */
        Label bottomLeft = new Label(this, SWT.NONE);
        bottomLeft.setImage(cornerBottomLeft);
        
        Label bottom = new Label(this, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.heightHint = 32;
        bottom.setLayoutData(gd);
        bottom.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                repeatDrawHorizontal(e.gc, borderBottom, bottom.getSize().x);
            }
        });
        
        Label bottomRight = new Label(this, SWT.NONE);
        bottomRight.setImage(cornerBottomRight);
        
        this.pack();
    }
    
    private void repeatDrawHorizontal(GC gc, Image img, int compositeWidth) {
        int width = img.getImageData().width;
        int xpos = 0;
        while (xpos < compositeWidth) {
            gc.drawImage(img, xpos, 0);
            xpos += width;
        }
    }
    
    private void repeatDrawVertical(GC gc, Image img, int compositeHeight) {
        int height = img.getImageData().height;
        int ypos = 0;
        while (ypos < compositeHeight) {
            gc.drawImage(img, 0, ypos);
            ypos += height;
        }
    }
    
    protected abstract Control createContents(Composite parent);
    
    private Image rotateImage(Image src, int angle) {
        int rotateType;
        
        switch (angle) {
            case 90:
                rotateType = SWT.RIGHT;
                break;
            case 180:
                rotateType = SWT.DOWN;
                break;
            case 270:
                rotateType = SWT.LEFT;
                break;
            default:
                rotateType = SWT.RIGHT;
                break;
        }
        
        return new Image(this.getDisplay(), rotate(src.getImageData(),
                rotateType));
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
