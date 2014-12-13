/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;

/**
 *
 * @author Bob
 */
public class PlayerManager extends AbstractAppState {

  public Player           player;
  public BulletAppState   physics;
  private AppStateManager stateManager;
  
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
      
      super.initialize(stateManager, app);
      player            = new Player(stateManager);
      physics           = new BulletAppState();
      this.stateManager = stateManager;
      
  }
  
  @Override
  public void update(float tpf) {
  
      if (player.getLocalTranslation().y < -5) {
      
          stateManager.getState
                  (GuiManager.class).showAlert("No Escape", "As you fall into the darkness awaiting the sweet release of death... You wake up back where you started");
          player.phys.warp(stateManager.getState(SceneManager.class).scene.getChild("StartSpot").getLocalTranslation().add(0,5,0));
      
      }
  
  }
    
}
