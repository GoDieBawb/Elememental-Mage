/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Bob
 */
public class Player extends Node {
    
  public  Node                   model;
  public  BetterCharacterControl phys;
  public  ArrayList<String>      inventory;
  public  boolean                hasChecked;
  private AnimControl            animControl;
  private AnimChannel            animChannel;
  private String                 equippedItem;
  private AppStateManager        stateManager;
  public  boolean                hasFailed;
  public  Long                   failTime;
  public  float                  speedMult;
  public  float                  strafeMult;
  public  String                 spellType;
  private String                 scenePath;
  
  public Player(AppStateManager stateManager) {
  
      Node faceNode     = new Node("Face");
      this.stateManager = stateManager;
      setModel("Mage");
      phys              = new BetterCharacterControl(.3f, 1.1f, 100);
      inventory         = new ArrayList();
      equippedItem      = "None";
      spellType         = "None";
      failTime          = 0L;
      speedMult         = .5f;
      attachChild(faceNode);
      faceNode.setLocalTranslation(0,1.3f,0);
      addControl(phys);
      inventory.add("Nothing");
      loadPlayerInfo(stateManager);
      
  }
  
  public void loadPlayerInfo(AppStateManager stateManager) {
  
      Yaml yaml         = new Yaml();
      LinkedHashMap map = null;
      String filePath;
      
      try {
      
          filePath = stateManager.getState(AndroidManager.class).filePath;
      
      }
      
      catch (Exception e) {
      
          filePath = System.getProperty("user.home")+ "/Documents/Mods/";
      
      }
      
      try {
          
          File file            = new File(filePath + "Save" + ".yml");
          FileInputStream fi   = new FileInputStream(file);
          Object obj           = yaml.load(fi);
          map                  = (LinkedHashMap) obj;
          
      }
      
      catch (FileNotFoundException e) {
          
          scenePath = "Scenes/PlayerHouse.j3o";
          savePlayerInfo(stateManager);
          loadPlayerInfo(stateManager);
          return;
      
      }

      ArrayList inventoryList = (ArrayList) map.get("Inventory");
      String scene            = (String)  map.get("Scene");
      String spell            = (String)  map.get("Spell");
      
      for (int i = 0; i < inventoryList.size(); i++) {
         
          String item = (String) inventoryList.get(i);
          inventory.add(item);
      
      }
      
      spellType = spell;

      scenePath = (scene);
      stateManager.getState(SceneManager.class).setScenePath(scenePath);
      
  }
  
  public void savePlayerInfo(AppStateManager stateManager) {
      
      HashMap contents = new HashMap();
      contents.put("Inventory", inventory);
      contents.put("Scene", scenePath);
      contents.put("Spell", spellType);
      
      DumperOptions options = new DumperOptions();
      options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
      options.setAllowUnicode(true);
      Yaml yaml = new Yaml(options);      
      
      
      String filePath;
      
      try {
      
          filePath = stateManager.getState(AndroidManager.class).filePath;
      
      }
      
      catch (Exception e) {
      
          filePath = System.getProperty("user.home")+ "/Documents/Mods/";
      
      }
      
      File file = new File(filePath + "Save" + ".yml");
      
      try {
          
          FileWriter fw  = new FileWriter(file);
          yaml.dump(contents, fw);
          fw.close();
          
      }
      
      catch(Exception e) {
      
      }
  
  }
  
  public void setModel(String name) {
  
      model       = (Node) stateManager.getApplication().getAssetManager()
                        .loadModel("Models/" + name + "/" + name + ".j3o");
      animControl =  ((Node)((Node)model.getChild(0)))
                            .getControl(AnimControl.class);
      animChannel = animControl.createChannel();
      animChannel.setAnim("FkIdle");
      model.scale(.09f);
      attachChild(model);
      model.setLocalTranslation(0,1f,0);
  
  }
  
  public void setScenePath(String scenePath) {
  
      this.scenePath = scenePath;
  
  }
  
  public void fail() {
      
    stateManager.getState(SceneManager.class).setScenePath("Scenes/PlayerHouse.j3o");
    stateManager.getState(SceneManager.class).initScene();
  
  }
  
  public void swing(AppStateManager stateManager) {
      
      if (stateManager.getState(CameraManager.class).isShoot)
      shoot(stateManager);
      else    
      hasChecked = true;
  
  }
  
  public String getEquippedItem() {
  
      return equippedItem;
  
  }
  
  public void equipItem(Node model, Vector3f location, float xRot, float yRot, float zRot, float scale) {

    equippedItem =  model.getName();
    model.scale(scale);
    ((Node) this.model.getChild("HandNode")).attachChild(model);
    model.setLocalTranslation(location);
    model.rotate(xRot,yRot,zRot);
  
  }
  
  public void shoot(AppStateManager stateManager) {
  
      if (!spellType.equals("None"))
      stateManager.getState(SpellManager.class).makeSpell(this, stateManager);
      
      else
      stateManager.getState(GuiManager.class).showAlert("Magic", "You currently do not have any spells.");
  
  }
  
  public void dropItem() {
  
       equippedItem = "None";
      ((Node)model.getChild("HandNode")).detachAllChildren();
  
  }
  
  public void run() {
  
      if (!animChannel.getAnimationName().equals("FkWalk")) {
          
          animChannel.setAnim("FkWalk");
      
      }
      
  }
  
  public void idle() {
  
      if (!animChannel.getAnimationName().equals("FkIdle")) {
          
          animChannel.setAnim("FkIdle");
          
      }
  
  }
    
}
