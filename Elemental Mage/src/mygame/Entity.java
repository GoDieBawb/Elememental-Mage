/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bob
 */
public class Entity extends Node {   

    public Node          model;
    public Behavior      behavior;
    public Player        player;
    public EntityManager manager;
    public Vector3f      startSpot;
    private AnimControl  animControl;
    private AnimChannel  animChannel;
    
  public Entity(Node model, AppStateManager stateManager, EntityManager manager) {
       name          = model.getName();
       this.model    = model;
       player        = stateManager.getState(PlayerManager.class).player;
       String script = model.getUserData("Script");
       behavior      = new Behavior(stateManager, script, this);
       this.manager  = manager;
       model.removeFromParent();
       attachChild(model);
       setLocalTranslation(model.getWorldTranslation());
       model.setLocalTranslation(0,0,0);
       setName(model.getName());
       behavior.startAction();
       setAnim();
    
    }
    
  public void setModel(String name, AppStateManager stateManager) {
  
      model       = (Node) stateManager.getApplication().getAssetManager()
                        .loadModel("Models/" + name + "/" + name + ".j3o");
      animControl =  ((Node)((Node)model.getChild(0)))
                            .getControl(AnimControl.class);
      animChannel = animControl.createChannel();
      animChannel.setAnim("FkIdle");
      model.scale(.1f);
      attachChild(model);
      model.setLocalTranslation(0,.75f,0);
  
  }
    
  public AnimControl getAnimControl() {
    return animControl;
    }
    
  private void setAnim() {
    
        try {
            
            animControl  =  ((Node)((Node) model.getChild(0)).getChild(0))
                                .getChild(0).getControl(AnimControl.class);
            animChannel  = animControl.createChannel();
            animChannel.setAnim("FkIdle");
        
        }
        
        catch (Exception e) {
        }
    
    }
    
  public void idle() {
  
      if (!animChannel.getAnimationName().equals("FkIdle")) {
      animChannel.setAnim("FkIdle");
      }
    
   }
  
  public void attack() {
  
      if (!animChannel.getAnimationName().equals("FkAttack")) {
      animChannel.setAnim("FkAttack");
      }
  
  }

  public void die() {
  
      if (!animChannel.getAnimationName().equals("FkDie")) {
      animChannel.setAnim("FkDie");
      animChannel.setLoopMode(LoopMode.DontLoop);
      }
  
  }
  
}
