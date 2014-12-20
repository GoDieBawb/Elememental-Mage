package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.util.TempVars;

/**
*
* @author Bob
*/
public class CameraManager extends AbstractAppState {

  private SimpleApplication app;
  private AppStateManager   stateManager;
  private Player            player;
  public  Camera            cam;
  private Vector3f          panDir;
  private Vector3f          cameraSpot;
  private Vector3f          cameraLook;
  public  boolean           isPan;
  private float             panMult;
  public  boolean           isShoot;
  public  ChaseCamera       shootCam;
  private ChaseCamera       chaseCam;
  
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
      
      super.initialize(stateManager, app);
      this.app            = (SimpleApplication) app;
      this.stateManager   = this.app.getStateManager();
      this.player         = this.stateManager.getState(PlayerManager.class).player;
      panMult             = stateManager.getState(GuiManager.class).panMult;
      initChaseCam();
      initCamera();
      initShootCam();
    
  }
  
  //Creates camera
  public void initChaseCam() {
      
      //Creates a new chase cam and attached it to the player.model for the game
      chaseCam = new ChaseCamera(this.app.getCamera(), player, this.app.getInputManager());
      chaseCam.setMinDistance(6f);
      chaseCam.setMaxDistance(6);
      chaseCam.setDefaultDistance(6);
      chaseCam.setDragToRotate(false);
      chaseCam.setRotationSpeed(5f);
      chaseCam.setLookAtOffset(player.getLocalTranslation().add(0, 1.2f, 0));
      chaseCam.setMaxVerticalRotation(.146f);
      chaseCam.setDefaultVerticalRotation(.145f);
      chaseCam.setMinVerticalRotation(.144f);
      app.getInputManager().setSimulateMouse(true);
      
  }
  
  private void chaseCamMove() {
      
      //chaseCam.setMaxVerticalRotation(.145f);
      //chaseCam.setMinVerticalRotation(.145f); 
      chaseCam.setDefaultVerticalRotation(.145f);
    
  }
  
  //Creates camera
  private void initCamera() {
      
    app.getFlyByCamera().setEnabled(false);
    cam = this.app.getCamera();
    cameraLook = player.model.getWorldTranslation().add(0,1f,0);
    
  }
  
  private void initShootCam(){
  
      shootCam =  new ChaseCamera(cam, player.getChild("Face"), app.getInputManager());
      shootCam.setMinDistance(0);
      shootCam.setMaxDistance(.1f);
      shootCam.setDefaultDistance(.1f);
      shootCam.setRotationSpeed(2);
      shootCam.setEnabled(false);
      
  }
  
  private void chaseCamMove(float tpf) {
      
      cameraLook = cameraLook.mult(.7f).add
                    (player.model.getWorldTranslation().add(0,1f,0).mult(.3f));
      
      cameraSpot = player.model.getWorldTranslation()
                      .add(player.phys.getViewDirection()
                          .normalize()
                            .negate().mult(4)).add(0,1f,0);
      
      //cam.lookAt(cameraLook, new Vector3f(0,1,0));
      slerpLookAt(cameraLook, tpf);
      
      if (cam.getLocation().distance(player.getWorldTranslation()) > 6f){
          cam.setLocation(cameraSpot);
      }
 
      if (cam.getLocation().distance(player.getWorldTranslation()) > 4 && !isPan) {
          
          panDir = cameraSpot.subtract(cam.getLocation()).mult(2);
          cam.setLocation(cam.getLocation().addLocal(panDir.mult(tpf)));
          
      }
      
      else if (cam.getLocation().distance(player.getWorldTranslation()) < 3 && !isPan) {
          
          cameraSpot = cameraSpot.clone();
          panDir = cameraSpot.subtract(cam.getLocation());
          cam.setLocation(cam.getLocation().addLocal(panDir.mult(tpf)));
          isPan = true;
          
      }
      
      else if (isPan) {
      
          cam.setLocation(cam.getLocation().addLocal(panDir.mult(tpf)));
          
          if (cam.getLocation().distance(cameraSpot) < .05f) {
          isPan = false;
          }
          else if (cam.getLocation().distance(cameraSpot) > 4f) {
          isPan = false;  
          }
      
      }
    
  }
  
  private void slerpTo(Quaternion quaternion, float amount) {
                Quaternion rotation = cam.getRotation();
                rotation.slerp(quaternion, amount);
                cam.setRotation(rotation);
        }

  private void slerpLookAt(Vector3f pos, float amount) {
                TempVars vars = TempVars.get();
                Vector3f newDirection = vars.vect1;
                Vector3f newUp = vars.vect2;
                Vector3f newLeft = vars.vect3;
                Quaternion airRotation = vars.quat1;

                newDirection.set(pos).subtractLocal(cam.getLocation()).normalizeLocal();

                newLeft.set(Vector3f.UNIT_Y).crossLocal(newDirection).normalizeLocal();
                if (newLeft.equals(Vector3f.ZERO)) {
                        if (newDirection.x != 0) {
                                newLeft.set(newDirection.y, -newDirection.x, 0f);
                        } else {
                                newLeft.set(0f, newDirection.z, -newDirection.y);
                        }
                }

                newUp.set(newDirection).crossLocal(newLeft).normalizeLocal();

                airRotation.fromAxes(newLeft, newUp, newDirection);
                airRotation.normalizeLocal();

                slerpTo(airRotation, amount*5);

                vars.release();

        }
  
  private void topDownCamMove(){
    app.getCamera().setLocation(player.getLocalTranslation().addLocal(0,30,0));
    app.getCamera().lookAt(player.getLocalTranslation().multLocal(1,0,1), new Vector3f(0,1,0));
    }
  
  @Override 
  public void update(float tpf) {
    panMult          = stateManager.getState(GuiManager.class).panMult;   
    boolean topDown  = stateManager.getState(InteractionManager.class).topDown;
    boolean panLeft  = stateManager.getState(InteractionManager.class).panLeft;
    boolean panRight = stateManager.getState(InteractionManager.class).panRight;
    
    
     if (topDown) {
        
        topDownCamMove();
    
    }
     
     else if (isShoot) {
        
     
     }
     
     else if (!topDown && !isShoot) {
     
         chaseCamMove();
     
     }
    
    else if (panLeft) {
    
        Vector3f camDir = cam.getLeft().mult(panMult);
        
        cam.setLocation(cam.getLocation().add(camDir.mult(tpf)));
        
        player.phys.setViewDirection(cam.getDirection());
        cam.lookAt(cameraLook, new Vector3f(0,1,0));
    
    }
    
    else if (panRight) {
 
        Vector3f camDir = cam.getLeft().negate().mult(panMult);
        cam.setLocation(cam.getLocation().add(camDir.mult(tpf)));
        player.phys.setViewDirection(cam.getDirection());
        cam.lookAt(cameraLook, new Vector3f(0,1,0));
    
    }
    
    else {   
        
        chaseCamMove(tpf);
    
    }
    
    }
  
  }
