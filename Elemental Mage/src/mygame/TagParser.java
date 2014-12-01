/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Bob
 */
public class TagParser {

    public Object parseTag(AppStateManager stateManager, String tag, Entity entity) {
        
        String[] args = tag.split("\\.");
        
        Object obj = entity;
    
        for(int i = 0; i < args.length; i++) {
        
            if (args[i].equals("player")) {
                
                obj = stateManager.getState(PlayerManager.class).player;
                
            }
            
            else if (args[i].contains("entity") || args[i].contains("entities")) {
            
                if (args[i].contains("#")) {
                
                    String[] strAr = args[i].split("#");
                    obj            = entity.getParent().getChild(strAr[1]);
                    
                }
                
                else
                obj = entity;
            
            }
            
            else if (args[i].contains("child")) {
            
                String[] strAr = args[i].split("#");
                obj            = (Node) ((Node) obj).getChild(strAr[1]);
            
            }
            
            else if (args[i].equals("failed")) {
            
                obj = (Boolean) ((Player) obj).hasFailed;
            
            }
            
            else if(args[i].equals("location")) {
            
                obj = ((Node) obj).getLocalTranslation();
                
            }
            
            else if (args[i].equals("distance")) {
            
                String[] strAr  = args[i].split("#");
                Vector3f check  = (Vector3f) parseTag(stateManager, strAr[1], entity);
                obj             = ((Vector3f) obj).distance(check);
            
            }
            
            else if (args[i].equals("north")) {
            
                obj = (Boolean) (((Node) obj).getLocalTranslation().x < 0);
            
            }
            
            else if (args[i].equals("south")) {
            
                obj = (Boolean) (((Node) obj).getLocalTranslation().x > 0);
            
            }
            
            else if (args[i].equals("east")) {
            
                obj = (Boolean) (((Node) obj).getLocalTranslation().z < 0);
            
            }
            
            else if (args[i].equals("west")) {
            
                obj = (Boolean) (((Node) obj).getLocalTranslation().z > 0);
            
            }
            
            else if (args[i].equals("scarecrow")) {
                
               System.out.println(obj);
               obj = (Boolean) ((Entity) obj).model.getUserData("Scarecrow");
            
            }
            
            else if (args[i].equals("!")) {
            
                if (((Boolean) obj)) {
                
                   obj = (Boolean) false; 
                
                }
                
                else {
                
                    obj = (Boolean) true; 
                
                }
                
                
            }
            
            else if (args[i].equals("ishid")) {
            
                obj = (Boolean) ((Entity) obj).behavior.getIsHid();
            
            }
            
            else if (args[i].equals("inprox")) {
            
                obj = ((Entity) obj).behavior.getInProx();
            
            }
            
            else if(args[i].equals("inventory")) {
            
                obj = ((Player) obj).inventory;
                
            }
            
            else if(args[i].contains("equipped_item")) {
            
                String[] strAr  = args[i].split("#");
                obj = (Boolean) (((Player) obj).getEquippedItem().equals(strAr[1]));
            
            }
            
            else if (args[i].contains("spell_type")) {
            
                 String[] strAr  = args[i].split("#");
                 obj = (Boolean) (((Player) obj).spellType).equals(strAr[1]);
            
            }
            
            else if(args[i].contains("contains")) {
                
                String[] strAr = args[i].split("#");
                obj = ((ArrayList<String>)obj).contains(strAr[1]);
                
            }
            
            else {
            
                System.out.println("Unkown Tag Argument: " + args[i]);
                
            }
            
            if (i == args.length-1) {
                
                return obj;
                
            }
        
        }
      
      return obj;
        
    }
    
}
