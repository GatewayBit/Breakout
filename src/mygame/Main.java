package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    Spatial ball, player, block;
    BitmapText bitText;
    
    AudioNode ballHit;
    AudioNode blockSmash;
    AudioNode ballDeath;
    
    boolean isRunning = true;
    
    float pSpeed = 4;
    float ballSpeed = 4f;

    @Override
    public void simpleInitApp() 
    {
        initAssets();
        initCamera();
        initKeys();
    }
    
    private void initAssets()
    {
        ball = assetManager.loadModel("Models/ball/ball.j3o");
        player = assetManager.loadModel("Models/paddle1/paddle1.j3o");
        block = assetManager.loadModel("Models/paddle2/paddle2.j3o");
        
        Quaternion rot = new Quaternion();
        
        //Ball
        rot.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));
        ball.rotate(rot);
        player.rotate(rot);
        block.rotate(rot);
        
        rot.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));
        player.rotate(rot);
        block.rotate(rot);
        
        ball.scale(0.10f);
        player.scale(0.10f);
        block.scale(0.10f);
        
        ball.setLocalTranslation(0, -1, 0);
        player.setLocalTranslation(0, -3.5f, 0);
        
        rootNode.attachChild(ball);
        rootNode.attachChild(player);
//        rootNode.attachChild(block);
    }
    
    private void initKeys()
    {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        
        inputManager.addListener(actionListener, "Pause");
        inputManager.addListener(analogListener, "Left", "Right");
    }
    
    private ActionListener actionListener = new ActionListener()
    {
        public void onAction(String name, boolean keyPressed, float tpf)
        {
            if (name.equals("Pause") && !keyPressed)
            {
                isRunning = !isRunning;
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener()
    {
        public void onAnalog(String name, float value, float tpf)
        {
            if (isRunning)
            {
                if (name.equals("Left"))
                {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x - pSpeed * tpf, v.y, v.z);
                }
                if (name.equals("Right"))
                {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x + pSpeed * tpf, v.y, v.z);
                }
            }
            else
            {
                System.out.println("Press 'P' to unpause");
            }
        }
    };
    
    private void initCamera()
    {
        cam.setLocation(new Vector3f(0, 0 , 10));
        
        flyCam.setEnabled(false);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        ballLogic(tpf);
    }
    
    private void ballLogic(float tpf)
    {
        Vector3f b = ball.getLocalTranslation();
        if (b.x > 5)
        {
            ballSpeed *= -1;
        }
        if (b.x < -5)
        {
            ballSpeed *= -1;
        }
        ball.setLocalTranslation(b.x + ballSpeed * tpf, b.y, b.z);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
