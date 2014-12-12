/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Bob
 */
public class SpellManager extends AbstractAppState {
    
    public  Node      spellNode;
    private Node      entityNode;
    private Material  iceMat;
    private Material  poisMat;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        
        super.initialize(stateManager, app);
        spellNode  = new Node();
        entityNode = stateManager.getState(EntityManager.class).entityNode;
        iceMat     = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        iceMat.setColor("Color", ColorRGBA.Cyan);
        poisMat     = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        poisMat.setColor("Color", ColorRGBA.Green);
        ((SimpleApplication) app).getRootNode().attachChild(spellNode);
        
    }
    
    public void updateNode(Node newNode) {
    
        entityNode = newNode;
    
    }
    
    public void makeSpell(Player player, AppStateManager stateManager) {
    
        Spell spell = new Spell(player, stateManager);
        
        if (player.spellType.equals("Ice")) {
            
            spell.effect.setStartColor(ColorRGBA.Blue);
            spell.effect.setStartColor(ColorRGBA.Cyan);
            spell.getChild("Target").setMaterial(iceMat);
            
        }
        
        if (player.spellType.equals("Poison")) {
            
            spell.effect.setStartColor(ColorRGBA.Yellow);
            spell.effect.setStartColor(ColorRGBA.Green);
            spell.getChild("Target").setMaterial(poisMat);
            
        }
        
        spellNode.attachChild(spell);
        spell.setLocalTranslation(stateManager.getApplication().getCamera().getLocation());
    
    }
    
    private Entity getEnt(Spatial spatial) {
    
        Entity ent;
        
        if (spatial.getParent()instanceof Entity)
        ent = (Entity) spatial.getParent();
        
        else
        ent = getEnt(spatial.getParent());
        
        return ent;
        
    }

    @Override
    public void update(float tpf) {
        
        for (int i = 0; i < spellNode.getQuantity(); i++) {
        
            CollisionResults results = new CollisionResults();
            Spell currentSpell       = (Spell) spellNode.getChild(i);
            currentSpell.move(currentSpell.moveDir.mult(tpf).mult(20));
            
            entityNode.collideWith(currentSpell.getChild("Target").getWorldBound(), results);
            
            if (results.size() > 0) {
            
                currentSpell.removeFromParent();
                Entity hitEnt = getEnt(results.getCollision(0).getGeometry());
                hitEnt.behavior.hitAction();
                
            }
            
            if (System.currentTimeMillis() / 1000 - currentSpell.startTime > 10) {
            
                currentSpell.removeFromParent();
            
            }
        
        }   
    
    }
    
}
