/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import tonegod.gui.core.Screen;

/**
 *
 * @author Bob
 */
public class InteractionManager extends AbstractAppState implements ActionListener {
    
  
  public SimpleApplication app;
  public AppStateManager   stateManager;
  public InputManager      inputManager;
  private Player           player;
  public  Vector3f         walkDirection = new Vector3f();
  private Vector3f         camDir = new Vector3f();
  private Vector3f         camLeft = new Vector3f();
  public boolean           inv = false, left = false, right = false, up = false, down = false, click = false;
  public boolean           topDown;
  public boolean           panLeft = false, panRight = false;
  
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
      
     super.initialize(stateManager, app);
     this.app          = (SimpleApplication) app;
     this.stateManager = this.app.getStateManager();
     this.inputManager = this.app.getInputManager();
     player            = stateManager.getState(PlayerManager.class).player;
     setUpKeys();
     
  }

    

  private void setUpKeys() {
  
      inputManager.addMapping("Reload", new KeyTrigger(KeyInput.KEY_T));
      inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
      inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
      inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
      inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
      inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
      inputManager.addMapping("Inventory", new KeyTrigger(KeyInput.KEY_E));
      inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
      inputManager.addMapping("Speed", new KeyTrigger(KeyInput.KEY_B));
      inputManager.addListener(this, "Speed");
      inputManager.addListener(this, "Up");
      inputManager.addListener(this, "Down");
      inputManager.addListener(this, "Left");
      inputManager.addListener(this, "Right");
      inputManager.addListener(this, "Click");
      inputManager.addListener(this, "Inventory");
      inputManager.addListener(this, "Space"); 
      inputManager.addListener(this, "Reload");
  
  }
    
  @Override
  public void onAction(String binding, boolean isPressed, float tpf) {
  
     if (binding.equals("Reload")) {
       
         if (isPressed)
         stateManager.getState(EntityManager.class).reloadScripts();
      
     }
     
     if (binding.equals("Speed")) {
     
         if(isPressed) {
             
            if (player.speedMult == .5f)
            player.speedMult = 5f;
         
            else
            player.speedMult = .5f;
         
         }
     
     }
     
     if (binding.equals("Inventory")) {
     
         this.inputManager.setCursorVisible(isPressed);
     
     }
     
     if (binding.equals("Space")) {
         
         player.hasChecked = isPressed;
         
     }
     
     if (binding.equals("Up")) {
        up = isPressed;
     }
     
     else if (binding.equals("Down")) {
        down = isPressed;
     }

     if (binding.equals("Left")) {
         left = isPressed;
     }
     
     else if (binding.equals("Right")) {
         right = isPressed;
     }
  
  }
  
  private void topDownMove() {
        camDir.set(this.app.getCamera().getDirection()).multLocal(10.0f, 0.0f, 10.0f);
        camLeft.set(this.app.getCamera().getLeft()).multLocal(10.0f);
        walkDirection.set(0, 0, 0);
        
        int xMove = 0;
        int zMove = 0;
        
        if (up) {
            zMove = 5;
        }
        else if (down) {
            zMove = -5;
        }
        
        if (left) {
            xMove = 5;
        }
        else if (right) {
            xMove = -5;
        
        } 
        
        if(up||down||left||right) {
            
          player.run();
            
        } else {
          player.idle();
          }
        
       walkDirection.addLocal(xMove, 0, zMove); 
        
       player.phys.setWalkDirection(walkDirection.mult(1f));
    }
  
  private void rotate() {
    
    if (up) {
      
      if (left) {
        player.phys.setViewDirection(new Vector3f(999,0,999));
        }
      
      else if (right) {
        player.phys.setViewDirection(new Vector3f(-999,0,999));
        }
      
      else {
        player.phys.setViewDirection(new Vector3f(0,0,999));
        }
      
      }
    
    else if (down) {
      
      if (left) {
        player.phys.setViewDirection(new Vector3f(999,0,-999));
        }
      
      else if (right) {
        player.phys.setViewDirection(new Vector3f(-999,0,-999));
        }
      
      else {
        player.phys.setViewDirection(new Vector3f(0,0,-999));
        } 
        
      }
    
    else if (left) {
      player.phys.setViewDirection(new Vector3f(999,0,0));
      }
    
    else if (right){
      player.phys.setViewDirection(new Vector3f(-999,0,0));
      }
    }
  
  private void chaseMove(){
      
        camDir.set(this.app.getCamera().getDirection()).multLocal(10.0f, 0.0f, 10.0f);
        camLeft.set(this.app.getCamera().getLeft()).multLocal(10.0f);
        walkDirection.set(0, 0, 0);
        
        if (up) {
            walkDirection.addLocal(camDir);
            player.run();
        }
        
        else if (down) {
            walkDirection.addLocal(camDir.negate());
            player.run();
        }
        
        if (left) {
            walkDirection.addLocal(camLeft);
            player.run();
        }
        
        else if (right) {
            walkDirection.addLocal(camLeft.negate());
            player.run();
        
        } 
        
        else if (!up && !down) {
            player.idle();
        }
        
        float speedMult;
        speedMult = player.speedMult*.8f + player.strafeMult*.2f;
        
       player.phys.setWalkDirection(walkDirection.mult(speedMult*.7f));
       
       if (!up && !down && !left && !right)
       player.phys.setViewDirection(camDir);
       else 
       player.phys.setViewDirection(walkDirection);
         
       
    }
  
  @Override
  public void update(float tpf) {
      
      if (stateManager.getState(CameraManager.class).isShoot) {
      
          up = false; down = false; right = false; left = false;
          
      }
      
      else if (topDown) {
          
          topDownMove();
          rotate();
          
      }
      
      else if (!stateManager.getState(CameraManager.class).isPan) {
          
        chaseMove();
        
      }
  
  }
    
}
