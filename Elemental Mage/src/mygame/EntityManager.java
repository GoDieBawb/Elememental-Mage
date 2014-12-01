/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Bob
 */
public class EntityManager extends AbstractAppState {

  private SimpleApplication   app;
  private AppStateManager     stateManager;
  private AssetManager        assetManager;  
  public  Node                entityNode;
  public  CommandParser       parser;
  private Player              player;
    
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    
      super.initialize(stateManager, app);
      this.app            = (SimpleApplication) app;
      this.stateManager   = this.app.getStateManager();
      this.assetManager   = this.app.getAssetManager();
      parser              = new CommandParser(stateManager);
      entityNode          = new Node();
      player = stateManager.getState(PlayerManager.class).player;
  
  }
  
  public void initEntities(Node scene) {
  
      entityNode.detachAllChildren();
      entityNode = (Node) scene.getChild("EntityNode");
      stateManager.getState(SpellManager.class).updateNode(entityNode);
      
      for (int i = 0; i < entityNode.getQuantity(); i++) {
      
          Node node = (Node) entityNode.getChild(i);
          
          if (node instanceof Entity){}
          
          else {    
          
              Entity entity = new Entity(node, stateManager, this);
              entityNode.attachChild(entity);
              
          }
          
      }
      
      cleanNode();
  
  }
  
  private void cleanNode() {
  
    for (int i = 0; i < entityNode.getQuantity(); i++) {
    
        Node node = (Node) entityNode.getChild(i);
        
        if (node instanceof Entity) {
        
            node.attachChild(((Entity)node).model);
            
        }
        
        else {
            
            Entity entity = new Entity(node, stateManager, this);
            entityNode.attachChild(entity);        
        
        }
        
    }
    
    for (int i = 0; i < entityNode.getQuantity(); i++) {
    
        try {
        
            ((Entity) entityNode.getChild(i)).behavior.actionCheck();
            
        }
        
        catch (ClassCastException e){
            
            cleanNode();
            
        }
        
        catch (NullPointerException r) {
          
              
        }
        
    }
      
  }
  
  public void parse(ArrayList commands, Entity entity) {
    
      parser.parse(commands, entity);
        
  }
  
  public void reloadScripts() {
  
      for (int i = 0; i < entityNode.getQuantity(); i++) {
      
          ((Entity) entityNode.getChild(i)).behavior.setScript();
          
      }
      
  }
  
  private void makeBehave() {
  
      for (int i = 0; i < entityNode.getQuantity(); i++) {
          
          try {
      
          ((Entity) entityNode.getChild(i)).behavior.actionCheck();
          
          }
          
          catch (NullPointerException r) {
          
              
          }
      
      }
  
  }
  
  @Override
  public void update(float tpf) {
      
      if (player.hasFailed) {
          
          if (System.currentTimeMillis()/1000 - player.failTime > 3) {
              player.hasFailed = false;
              stateManager.getState(EntityManager.class).reloadScripts();
          }
          
      }
      
      else
      makeBehave();
  
  }
    
}
