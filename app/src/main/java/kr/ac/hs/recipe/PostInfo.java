package kr.ac.hs.recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String profileImg;
    private String profileName;
    private ArrayList<String> contents;
    private ArrayList<String> formats;
    private String publisher;
    private Date createdAt;
    private String id;
    private String recipeId; // 레시피 아이디
    private boolean mine;

    public PostInfo(String profileImg, String profileName, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createdAt, String id, String recipeId, boolean mine){
        this.profileImg = profileImg;
        this.profileName = profileName;
        this.contents = contents;
        this.formats = formats;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.recipeId = recipeId;
        this.mine = mine;
    }

    public PostInfo(String profileImg, String profileName, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createdAt, String recipeId){
        this.profileImg = profileImg;
        this.profileName = profileName;
        this.contents = contents;
        this.formats = formats;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.recipeId = recipeId;
    }

    public PostInfo(String profileImg, String profileName, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createdAt, boolean mine){
        this.profileImg = profileImg;
        this.profileName = profileName;
        this.contents = contents;
        this.formats = formats;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.mine = mine;
    }

    public Map<String, Object> getPostInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("profileImg",profileImg);
        docData.put("profileName",profileName);
        docData.put("contents",contents);
        docData.put("formats",formats);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        docData.put("recipeId",recipeId);
        return  docData;
    }

    public String getProfileName(){
        return this.profileName;
    }
    public void setProfileName(String profileName){
        this.profileName = profileName;
    }
    public ArrayList<String> getContents(){
        return this.contents;
    }
    public void setContents(ArrayList<String> contents){
        this.contents = contents;
    }
    public ArrayList<String> getFormats(){
        return this.formats;
    }
    public void setFormats(ArrayList<String> formats){
        this.formats = formats;
    }
    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }
    public Date getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }
    public boolean isMine() {
        return mine;
    }
    public void setMine(boolean mine) {
        this.mine = mine;
    }
    public String getProfileImg() {
        return profileImg;
    }
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
