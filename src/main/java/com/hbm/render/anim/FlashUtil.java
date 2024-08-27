package com.hbm.render.anim;

public class FlashUtil {
    private float flashd;

    public FlashUtil() {
        this.flashd = 0.0f; // Initialize to a default value
    }

    public float calculateFlashd() {
        flashd += 0.1f;
        flashd = Math.min(100.0f, flashd + 0.1f * (100.0f - flashd) * 0.15f);
        System.out.println(flashd);
        return flashd;
    }
}