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
import java.util.ArrayList;

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
      
  }
  
  public void setModel(String name) {
  
      model       = (Node) stateManager.getApplication().getAssetManager()
                        .loadModel("Models/" + name + "/" + name + ".j3o");
      animControl =  ((Node)((Node)model.getChild(0)))
                            .getControl(AnimControl.class);
      animChannel = animControl.createChannel();
      animChannel.setAnim("FkIdle");
      model.scale(.1f);
      attachChild(model);
      model.setLocalTranslation(0,1f,0);
  
  }
  
  public void fail() {
      
      System.out.println("Fail");
      System.out.println(System.currentTimeMillis()/1000 -  failTime);
      hasFailed = true;
      failTime  = System.currentTimeMillis() / 1000;
      inventory.clear();
      dropItem();
  
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
