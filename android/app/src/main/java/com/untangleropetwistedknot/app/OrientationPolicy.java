package com.untangleropetwistedknot.app;

/** Portrait-only full-screen gameplay. Android owns multi-window orientation. */
final class OrientationPolicy {
    enum Mode { PORTRAIT, SYSTEM }
    static Mode choose(int smallestWidthDp, int widthDp, int heightDp, boolean windowed) {
        return windowed ? Mode.SYSTEM : Mode.PORTRAIT;
    }
    private OrientationPolicy() {}
}
