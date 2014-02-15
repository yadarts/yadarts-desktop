# yadarts - Desktop Edition

This open source project uses the Emprex Wireless Dart Board and its
protocol as the basis for a sophisticated, extensible dart software.

This is the desktop edition which provides an application to visualize
the current game.

## How to build and start the desktop edition

1. Checkout the `yadarts` and `yadarts-desktop` project: `git clone URL_OF_THIS_REPO.git`.
2. Build both projects according to your architecture: `yadarts-desktop# mvn install -P linux32` 
    for a linux based machine with 32bit operating system.
3. Create executable file: `yadarts-desktop/release# mvn install -P linux32`.
4. Configure `JAVA_HOME` or add it to `yadarts-desktop/release/yadarts.sh`.
5. Start `yadarts-desktop`: `yadarts-desktop/release# yadarts.sh`
