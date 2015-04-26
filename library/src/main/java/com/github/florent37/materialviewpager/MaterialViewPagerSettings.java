package com.github.florent37.materialviewpager;

/**
 * Created by florentchampigny on 26/04/15.
 */
public class MaterialViewPagerSettings {

    protected int headerHeight;
    protected int color;
    protected boolean hideToolbarAndTitle;
    protected boolean hideLogoWithFade;

    public static Builder Builder(int color, int headerHeight) {
        return new Builder(color, headerHeight);
    }

    public static class Builder {
        private MaterialViewPagerSettings settings;

        private Builder(int color, int headerHeight) {
            settings = new MaterialViewPagerSettings();
            settings.color = color;
            settings.headerHeight = headerHeight;
        }

        public Builder hideToolbarAndTitle() {
            settings.hideToolbarAndTitle = true;
            return this;
        }

        public Builder hideLogoWithFade() {
            settings.hideLogoWithFade = true;
            return this;
        }

        public MaterialViewPagerSettings build(){
            return settings;
        }
    }

}
