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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A theme is a set of image files that provide the
 * basic style of the UI.
 */
public abstract class Theme {
    
    private static final Logger logger = LoggerFactory.getLogger(Theme.class);
    
    protected static final String BOARD_HI = "board-hi.png";
    protected static final String BOARD_M = "board-m.png";
    protected static final String BOARD_LO = "board-lo.png";
    protected static final String BASE_DIR = "/themes";
    protected static final String DEFAULT_THEME = "edarts-classic";
    protected static final String BACKGROUND = "background.jpg";
    protected static final String BACKGROUND_ALT = "background_alt.jpg";
    protected static final String CORNER_TOP_LEFT = "corner_topleft.png";
    protected static final String BORDER_LEFT = "border_left.png";
    protected static final String CORNER_TOP_RIGHT = "corner_topright.png";
    protected static final String BORDER_TOP = "border_top.png";
    protected static final String CORNER_BOTTOM_LEFT = "corner_bottomleft.png";
    protected static final String BORDER_BOTTOM = "border_bottom.png";
    protected static final String CORNER_BOTTOM_RIGHT = "corner_bottomright.png";
    protected static final String BORDER_RIGHT = "border_right.png";
    
    protected static final FileFilter directoryFilter = new FileFilter() {
        
        @Override
        public boolean accept(File pathname) {
            /*
            * use directories, but not the default. we already have that
            */
            if (pathname.isDirectory() && !pathname.equals(DEFAULT_THEME)) {
                return true;
            }
            return false;
        }
    };
    
    private static Theme defaultTheme;
    private static Map<String, Theme> availableThemes = new HashMap<>();
    private static Theme currentTheme;
    
    static {
        try {
            defaultTheme = new ClasspathTheme(BASE_DIR + "/"+ DEFAULT_THEME);
            currentTheme = defaultTheme;
            resolveThemesFromFileSystem();
        } catch (URISyntaxException e) {
            logger.warn(e.getMessage(), e);
        }
    }
    
    public static Theme getDefault() {
        return defaultTheme;
    }
    
    public static void setCurrentTheme(String name) {
        if (availableThemes.containsKey(name)) {
            currentTheme = availableThemes.get(name);
        }
        else {
            logger.warn("No theme with name {} available. Using default", name);
        }
    }
    
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    
    private static void resolveThemesFromFileSystem() {
        logger.info("Resolving themes from file system...");
        File[] candidates = new File(".".concat(BASE_DIR)).listFiles(directoryFilter);
        
        if (candidates != null) {
            logger.info("Found theme candidates: {}", Arrays.toString(candidates));
            instantiateThemes(candidates);
        }
        else {
            logger.info("No theme candidates found on the file system.");
        }
        
        
    }
    
    private static void instantiateThemes(File[] candidates) {
        for (File c : candidates) {
            if (validateDirectoryContents(c.listFiles())) {
                try {
                    FileSystemTheme fst = new FileSystemTheme(c);
                    availableThemes.put(c.getName(), fst);
                    logger.info("Added theme: {}", c.getName());
                }
                catch (IllegalStateException e) {
                    logger.warn("Could not instantiate theme {}: {}", c.getName(), e.getMessage());
                }
            }
        }
    }
    
    private static boolean validateDirectoryContents(File[] files) {
        boolean hasHi = false;
        boolean hasM = false;
        boolean hasLo = false;
        
        for (File file : files) {
            switch (file.getName()) {
                case BOARD_HI:
                    hasHi = true;
                    break;
                case BOARD_M:
                    hasM = true;
                    break;
                case BOARD_LO:
                    hasLo = true;
                    break;
                default:
                    break;
            }
        }
        
        return hasHi && hasM && hasLo;
    }
    
    public abstract Image getBoardM(Display display) throws FileNotFoundException;
    
    public abstract Image getBoardHi(Display display) throws FileNotFoundException;
    
    public abstract Image getBoardLo(Display display) throws FileNotFoundException;
    
    public abstract Image getBackground(Display display) throws FileNotFoundException;
    
    public abstract Image getBackgroundAlt(Display display) throws FileNotFoundException;
    
    public abstract Image getCornerTopLeft(Display display) throws FileNotFoundException;
    
    public abstract Image getBorderLeft(Display display) throws FileNotFoundException;
    
    public abstract Image getCornerTopRight(Display display) throws FileNotFoundException;
    
    public abstract Image getBorderTop(Display display) throws FileNotFoundException;
    
    public abstract Image getCornerBottomLeft(Display display) throws FileNotFoundException;
    
    public abstract Image getBorderBottom(Display display) throws FileNotFoundException;
    
    public abstract Image getCornerBottomRight(Display display) throws FileNotFoundException;
    
    public abstract Image getBorderRight(Display display) throws FileNotFoundException;
}
