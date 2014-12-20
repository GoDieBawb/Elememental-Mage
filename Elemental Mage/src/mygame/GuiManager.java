package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.extras.android.Joystick;
import tonegod.gui.controls.windows.AlertBox;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
*
* @author Bob
*/
public class GuiManager extends AbstractAppState {

  private SimpleApplication app;
  private AppStateManager stateManager;
  private AssetManager  assetManager;
  public  AlertBox      alert;
  public  Screen        screen;
  private Joystick      stick;
  private Player        player;
  private ButtonAdapter interactButton;
  private String        delayedMessage;
  private String        delayedTitle;
  private int           alertDelay;
  private long          delayStart;
  private boolean       hasAlert;
  private boolean       isAndroid;
  public  String        alertTitle;
  private ButtonAdapter eyeButton;
  private ButtonAdapter shootButton;
  private float         prevX;
  private float         newX;
  private boolean       isDown;
  private boolean       isMouse;
  public  float         panMult;
  
  @Override
  public void initialize(AppStateManager stateManager, Application app){
    super.initialize(stateManager, app);
    this.app          = (SimpleApplication) app;
    this.stateManager = this.app.getStateManager();
    this.assetManager = this.app.getAssetManager();
    player            = stateManager.getState(PlayerManager.class).player;
    alertTitle        = "";
    initScreen();
    this.app.getGuiNode().addControl(screen);
    initInteractButton();
    initShootButton();
    initEyeButton();
    initAlertBox();
    isAndroid  = "Dalvik".equals(System.getProperty("java.vm.name"));
    
    if (isAndroid) {
        initJoyStick();
    }
    
    interactButton.show();
    
    }

  private void initScreen() {
        
        screen            = new Screen(app, "tonegod/gui/style/atlasdef/style_map.gui.xml") {
            
        @Override
        public void onTouchEvent(TouchEvent evt) {
            
            InteractionManager a = stateManager.getState(InteractionManager.class);
            float xSpot          = evt.getX();
            float ySpot          = evt.getY();
            
            if (ySpot > screen.getHeight()/3 && ySpot < screen.getHeight() - screen.getHeight()/3) {
            
            if (evt.getType() == evt.getType().DOWN) {
                isDown = true;
                prevX  = evt.getX();
            }
            
            else if (evt.getType() == evt.getType().MOVE) {   
                    
                    if (xSpot > prevX)
                        a.panLeft = true;
                    
                    else if (xSpot < prevX)
                        a.panRight = true;
                    
                    else {
                        
                        a.panLeft  = false;
                        a.panRight = false;
                        
                    }
                    
                }
                
                prevX = newX;
                newX  = xSpot;
                
            }
            
            else if (evt.getType() == evt.getType().UP) {
        
                    isDown     = false;
                    a.panRight = false;
                    a.panLeft  = false;
         
                }

            super.onTouchEvent(evt);

            }
        
        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
                
            InteractionManager a = stateManager.getState(InteractionManager.class); 
            
            prevX = evt.getX();
            
            if (evt.getY() > screen.getHeight()/3 && evt.getY() < screen.getHeight() - screen.getHeight()/3) {
                
                if (evt.isPressed()) {
                    isDown  = true;
                    isMouse = true;
                }
                
                else {
                    isMouse    = false;
                    isDown     = false;
                    a.panLeft  = false;
                    a.panRight = false;
                }
            }
            
            super.onMouseButtonEvent(evt);
                
        }
      
      };
        
      screen.setUseTextureAtlas(true,"tonegod/gui/style/atlasdef/atlas.png");
      screen.setUseMultiTouch(true);
      
  }
  
  private void initInteractButton(){
    interactButton = new ButtonAdapter( screen, "InteractButton", new Vector2f(15, 15) ) {
    
    @Override
      public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        player.swing(this.app.getStateManager());
        evt.setConsumed();
        }
      };
    
    interactButton.setMaterial(assetManager.loadMaterial("Materials/Paper.j3m"));
    interactButton.setDimensions(screen.getWidth()/8, screen.getHeight()/10);
    interactButton.setPosition(screen.getWidth() / 1.1f - interactButton.getHeight(), screen.getHeight() / 1.1f - interactButton.getHeight());
    interactButton.setFont("Interface/Fonts/UnrealTournament.fnt");
    interactButton.setText("Check");
    screen.addElement(interactButton);
    interactButton.hide();
    }
  
  private void initEyeButton() {

    eyeButton = new ButtonAdapter( screen, "EyeButton", new Vector2f(15, 15) ) {
    
    @Override
      public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        
        if (stateManager.getState(InteractionManager.class).topDown)
        stateManager.getState(InteractionManager.class).topDown = false;
        else
        stateManager.getState(InteractionManager.class).topDown = true;
        evt.setConsumed();
        
        }
      };
    
    eyeButton.setMaterial(assetManager.loadMaterial("Materials/Paper.j3m"));
    eyeButton.setDimensions(screen.getWidth()/8, screen.getHeight()/10);
    eyeButton.setPosition(screen.getWidth() - eyeButton.getWidth()*1.5f, 0 + eyeButton.getHeight()/2);
    eyeButton.setFont("Interface/SwishButtons.fnt");
    eyeButton.setText("w");
    screen.addElement(eyeButton);    
    eyeButton.show();
      
    }
  
  private void initShootButton() {
  
    shootButton = new ButtonAdapter( screen, "ShootButton", new Vector2f(15, 15) ) {
    
    @Override
      public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        
        if (stateManager.getState(CameraManager.class).isShoot) {
            
            stateManager.getState(CameraManager.class).isShoot = false;
            stateManager.getState(CameraManager.class).shootCam.setEnabled(false);
            interactButton.setText("Check");
            
        }
        
        else {
            
            stateManager.getState(CameraManager.class).isShoot = true;
            app.getCamera().setLocation(player.getWorldTranslation().add(0,1.5f,0));
            stateManager.getState(CameraManager.class).shootCam.setEnabled(true);
            interactButton.setText("Cast");
            
        }
        
        evt.setConsumed();
        
        }
      };
    
    shootButton.setMaterial(assetManager.loadMaterial("Materials/Paper.j3m"));
    shootButton.setDimensions(screen.getWidth()/8, screen.getHeight()/10);
    shootButton.setPosition(0+shootButton.getWidth()/3, 0 + shootButton.getHeight()/2);
    shootButton.setFont("Interface/SwishButtons.fnt");
    shootButton.setText("r");
    screen.addElement(shootButton);    
    shootButton.show();
  
  }
  
  private void initAlertBox(){
    alert = new AlertBox(screen, Vector2f.ZERO) {
    @Override
    public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
      hideWithEffect();
      }
    };
    
    alert.setMaterial(assetManager.loadMaterial("Materials/Paper.j3m"));
    alert.setFont("Interface/Fonts/UnrealTournament.fnt");
    alert.setWindowTitle("Welcome");
    alert.setMsg("Welcome to Townyville.");
    alert.setButtonOkText("X");
    alert.setLockToParentBounds(true);
    alert.setClippingLayer(alert);
    alert.setMinDimensions(new Vector2f(150,180));
    //alert.setDimensions(new Vector2f(150,180));
    alert.setIsResizable(true);
    screen.addElement(alert);
    alert.hide();
    }
  
  public void showAlert(String speaker, String text){
    alertTitle = speaker;
    alert.showWithEffect();
    alert.setWindowTitle(speaker);
    alert.setMsg(text);
    }
  
  public void delayAlert(String speaker, String text, int delay){
    hasAlert = true;
    delayStart = System.currentTimeMillis() / 1000;
    alertDelay = delay;
    delayedTitle = speaker;
    delayedMessage = text;
    }

  private void initJoyStick(){
    stick = new Joystick(screen, Vector2f.ZERO, (int)(screen.getWidth()/7)) {
    
    @Override
    public void onUpdate(float tpf, float deltaX, float deltaY) {
        
        float dzVal = .25f; // Dead zone threshold
        float dzxVal = .25f;
        
            if (deltaX < -dzxVal) {
              stateManager.getState(InteractionManager.class).left  = true;
              stateManager.getState(InteractionManager.class).right = false;
              } 
            
            else if (deltaX > dzxVal) {
              stateManager.getState(InteractionManager.class).right = true;
              stateManager.getState(InteractionManager.class).left  = false;
              }
            
            else {
              stateManager.getState(InteractionManager.class).right = false;
              stateManager.getState(InteractionManager.class).left  = false; 
              }
            
        
            if (deltaY < -dzVal) {
              stateManager.getState(InteractionManager.class).down = true;
              stateManager.getState(InteractionManager.class).up   = false;
              } 
            
            else if (deltaY > dzVal) {
              stateManager.getState(InteractionManager.class).down = false;
              stateManager.getState(InteractionManager.class).up   = true;
              }
            
            else {
              stateManager.getState(InteractionManager.class).up   = false;
              stateManager.getState(InteractionManager.class).down = false;    
              }
            
          player.speedMult  = FastMath.abs(deltaY);
          player.strafeMult = FastMath.abs(deltaX);
          
          }
    
        };
      // getGUIRegion returns region info “x=0|y=0|w=50|h=50″, etc
      TextureKey key = new TextureKey("Textures/barrel.png", false);
      Texture tex = assetManager.loadTexture(key);
      stick.setTextureAtlasImage(tex, "x=20|y=20|w=120|h=35");
      stick.getThumb().setTextureAtlasImage(tex, "x=20|y=20|w=120|h=35");
      screen.addElement(stick, true);
      stick.setPosition(screen.getWidth()/10 - stick.getWidth()/2, screen.getHeight() / 10f - interactButton.getHeight()/5);
      // Add some fancy effects
      Effect fxIn = new Effect(Effect.EffectType.FadeIn, Effect.EffectEvent.Show,.5f);
      stick.addEffect(fxIn);
      Effect fxOut = new Effect(Effect.EffectType.FadeOut, Effect.EffectEvent.Hide,.5f);
      stick.addEffect(fxOut);
      stick.show();
      }
  
  @Override
  public void update(float tpf){
    
    if (hasAlert && System.currentTimeMillis()/1000 - delayStart > alertDelay) {
        
      showAlert(delayedTitle, delayedMessage);
      hasAlert = false;
      
    }
    
    if (isMouse) {
    
        Vector2f clickSpot   = app.getInputManager().getCursorPosition();
        InteractionManager a = stateManager.getState(InteractionManager.class);    
        newX                 = clickSpot.getX();
        
    }
    
    if (isDown) {
        
        InteractionManager a = stateManager.getState(InteractionManager.class);  
        
        if (newX > prevX) {
            
            a.panLeft = true;
            
        }
        
        else if (newX < prevX) {
            
            a.panRight = true;
            
        }
        
        else {
            
            a.panLeft  = false;
            a.panRight = false;
                    
        }

        panMult = FastMath.abs(newX - prevX)*15;
        
        if (panMult > 25)
        panMult = 25;
        
        prevX   = newX;
    
    }
    
    }
  
  }