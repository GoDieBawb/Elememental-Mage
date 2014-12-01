/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AppStateManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bob
 */
public class Spell extends Node {
    
    private String           type;
    public  ParticleEmitter  effect;
    public  Node             model;
    public  Vector3f         moveDir;
    public  Long             startTime;
    
    public Spell(Player player, AppStateManager stateManager) {
        
        type      = player.spellType;
        model     = (Node) stateManager.getApplication().getAssetManager().loadModel("Models/Spell.j3o");
        effect    = (ParticleEmitter) model.getChild("Effect");
        moveDir   = stateManager.getApplication().getCamera().getDirection();
        startTime = System.currentTimeMillis() / 1000;
        attachChild(model);
        setType();
        
    }
    
    private void setType() {
    
    }
    
  }
