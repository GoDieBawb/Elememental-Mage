
package mygame;

import com.jme3.app.state.AppStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Bob
 */
public class Behavior {
    
  private Map<Object, Object> map;
  private String              script;
  public  Entity              entity;
  private int                 proximity;
  private boolean             enteredProx;
  private AppStateManager     stateManager;
  private boolean             inProx;
  private boolean             isHid;
  
  public Behavior(AppStateManager stateManager, String script, Entity entity) {
      this.stateManager = stateManager;
      this.script       = script;
      this.entity       = entity;
      setScript();
      
  }
  
  public void setScript() {
      
      Yaml yaml         = new Yaml();
      String filePath   = System.getProperty("user.home")+ "/Documents/GitHub/Elememental-Mage/Elemental Mage/assets/Scripts/";
      stateManager.getApplication().getAssetManager().registerLoader(ScriptLoader.class, "yml");
      Object obj;
      System.out.println(entity.getName() + "'s script is: "+ script + ".yml");
      
      try {
          
          try {
              
              
              try {
                 
                 File file            = new File(filePath + script + ".yml");
                 FileInputStream fi   = new FileInputStream(file);
                 obj                  = yaml.load(fi);
                 map                  = (LinkedHashMap) obj;
                 
              }
              
              catch(FileNotFoundException fnf) {
                  
                 filePath = System.getProperty("user.home")+ "/Documents/Mods/Elemental Mage/Scripts/";
                 File file            = new File(filePath + script + ".yml");
                 FileInputStream fi   = new FileInputStream(file);
                 obj                  = yaml.load(fi);
                 map                  = (LinkedHashMap) obj;
                  
              }
              
              
          }
          
          catch (Exception f) {
              
              filePath   = "Scripts/";
              obj        = stateManager.getApplication().getAssetManager().loadAsset(filePath + script + ".yml");
              map        = (LinkedHashMap) obj;
              
          }
          
      }
      
      catch (Exception e) {
          
      }
      
      setProximity();
      startAction();
  
  }
  
  private void setProximity() {

     try {
     Map<Object, Object> pm = (Map<Object, Object>)  map.get("Proximity");
     proximity              =  (Integer) pm.get("Distance");
     }
     catch (Exception e){
     }
     
  }
  
  public void startAction() {
  
     ArrayList startScript;
      
     try {
         
        Map<Object, Object> sm = (Map<Object, Object>)  map.get("Start");
        startScript            = (ArrayList) sm.get("Script");
        entity.manager.parser.parse(startScript, entity);
     
     }
     catch (Exception e){
     }
      
  }
  
  public void hitAction() {
  
      if (map.get("Hit") != null) {
          
          Map<Object, Object> hm  = (Map<Object, Object>)  map.get("Hit");
          ArrayList hitScript     = (ArrayList) hm.get("Script");
          entity.manager.parser.parse(hitScript, entity);
          
      }
  
  }
  
  private void proximityAction() {
      
      float distance = entity.player.getLocalTranslation().distance(entity.getLocalTranslation());
      Map<Object, Object> pm          = (Map<Object, Object>)  map.get("Proximity");
      
      if (proximity > distance) 
          inProx = true;
      
      if (proximity < distance)  
          inProx = false;
      
      
      if (inProx && !enteredProx) {
      
          enteredProx           = true;
          ArrayList enterScript = (ArrayList) pm.get("Enter");
          entity.manager.parser.parse(enterScript, entity);
      
      }
      
      if (!inProx && enteredProx) {
      
          enteredProx          = false;
          ArrayList exitScript = (ArrayList) pm.get("Exit");
          entity.manager.parser.parse(exitScript, entity);
      
      }
      
  }
  
  public Map<Object, Object> getMap() {
  
      return map;
  
  }
  
  private void checkAction() {
      
      if (entity.getParent().getChild(entity.getParent().getQuantity()-1) == entity)
      stateManager.getState(PlayerManager.class).player.hasChecked = false;
      
      if (inProx) {
      
          Map<Object, Object> im   = (Map<Object, Object>)  map.get("Interact");
          ArrayList interactScript = (ArrayList) im.get("Script");
          entity.manager.parser.parse(interactScript, entity);
      
      }
      
  }
  
  private void loopAction() {
      
      Map<Object, Object> wm          = (Map<Object, Object>)  map.get("While");
      ArrayList whileScript           = (ArrayList) wm.get("Script");
      entity.manager.parser.parse(whileScript, entity);
  
  }
  
  public boolean getInProx() {
      return inProx;    
  }
  
  public void hide(){
  
      entity.model.setLocalTranslation(0,-25,0);
      isHid = true;
      
      if (entity.model.getChild("Surface") !=null) {
      
          entity.getChild("Surface").setLocalTranslation(0,25,0);
      
      }
      
  }
  
  public void show(){
  
      entity.model.setLocalTranslation(0,0,0);
      isHid = false;
      
      if (entity.model.getChild("Surface") !=null) {
      
          entity.getChild("Surface").setLocalTranslation(0,0,0);
      
      }
      
  }
  
  public boolean getIsHid() {
      return isHid;
  }
  
  public void setIsHid(boolean status) {
      isHid = status;
  }
  
  public void actionCheck() {
      
      if (stateManager.getState(PlayerManager.class).player.hasChecked) {
      checkAction();
      }
      
      proximityAction();
      
      loopAction();
  }
    
}
