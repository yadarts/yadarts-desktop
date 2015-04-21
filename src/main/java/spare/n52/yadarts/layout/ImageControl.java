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
package spare.n52.yadarts.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import spare.n52.yadarts.layout.home.MenuImage;

public class ImageControl extends Composite {

	private MenuImage image;
	private int width;
	private int height;
	protected boolean active;
	private Cursor cursorHand;
	private Cursor cursorArrow;
	private ClickListener clickListener;

	public ImageControl(Composite parent, int style) {
		super(parent, style);

		addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (image != null) {
					image.dispose();
				}
			}
		});

		addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event e) {
				paintControl(e);
			}
		});

		/* Listen for click events */
		addMouseListener(new MouseListener() {
			private long startClick;

			@Override
			public void mouseUp(MouseEvent e) {
				if (System.currentTimeMillis() - startClick <= 200) {
					handleButtonClick(e);					
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				startClick = System.currentTimeMillis();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		addListener(SWT.MouseEnter, new Listener() {
			@Override
			public void handleEvent(Event e) {
				ImageControl.this.active = true;
				ImageControl.this.redraw();
				ImageControl.this.setCursor(cursorHand);
			}
		});
		
		addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event e) {
				ImageControl.this.active = false;
				ImageControl.this.redraw();
				ImageControl.this.setCursor(cursorArrow);
			}
		});
		
		cursorHand = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		cursorArrow = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);
	}

	protected void handleButtonClick(MouseEvent e) {
		if (this.clickListener != null) {
			this.clickListener.onClickEvent();
		}
	}

	private void paintControl(Event event) {
		GC gc = event.gc;

		if (image != null) {
			gc.drawImage(active ? image.getActiveImage() : image.getImage(), 0, 0);
		}
	}

	public void setImage(MenuImage menuImage) {
		this.image = menuImage;
		width = menuImage.getImage().getBounds().width;
		height = menuImage.getImage().getBounds().height;
		redraw();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int overallWidth = width;
		int overallHeight = height;

		/* Consider hints */
		if (wHint != SWT.DEFAULT && wHint < overallWidth)
			overallWidth = wHint;

		if (hHint != SWT.DEFAULT && hHint < overallHeight)
			overallHeight = hHint;

		return new Point(overallWidth, overallHeight);
	}
	
	public void setClickListener(ClickListener clickListener2) {
		this.clickListener = clickListener2;
	}
	
	public static interface ClickListener {
		
		void onClickEvent();
		
	}

}
