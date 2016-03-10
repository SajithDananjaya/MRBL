/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectStructures;

/**
 *
 * @author Sajith
 */
public class Tag {

    private int tagID;
    private String tagName;

    private Tag() {
    }

    public Tag(int tagID, String tagName) {
        this.tagID = tagID;
        this.tagName = tagName;
    }

    public int getTagID() {
        return this.tagID;
    }

    public String getTagName() {
        return this.tagName;
    }
}
