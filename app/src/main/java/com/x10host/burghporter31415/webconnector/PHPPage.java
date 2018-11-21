/**
 * author: Dylan Porter
 * permission: Open-Source
 * project: Silent Voyager
 * file: PHPPage.java
 */

package com.x10host.burghporter31415.webconnector;

public class PHPPage {

    private String base;
    private String relative;

    public PHPPage(String base, String relative) {
        this.base = base;
        this.relative = relative;
    }

    public String getURL() {
        return (this.base + this.relative);
    }

    public void setURL(String base, String relative) {
        this.base = base;
        this.relative = relative;
    }

}
