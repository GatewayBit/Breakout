package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
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
import java.util.LinkedList;

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
    
    LinkedList<Spatial> blocks = new LinkedList();
    
    AudioNode ballHit;
    AudioNode blockSmash;
    AudioNode ballDeath;
    
    boolean isRunning = true;
    
    float pSpeed = 4;
    float ballXSpeed = 4f;
    float ballYSpeed = 3f;
    float deltaX = 1;
    float deltaY = 1;

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
        block.scale(0.095f);
        
        ball.setLocalTranslation(0, -1, 0);
        player.setLocalTranslation(0, -3.5f, 0);
//        block.setLocalTranslation(-1, 0.5f, 0);
        
        rootNode.attachChild(ball);
        rootNode.attachChild(player);
//        rootNode.attachChild(block);
        drawBlocks(block);
        
        setDisplayFps(false); // to hide the FPS
        setDisplayStatView(false); // to hide the statistics 
    }
    
    private void drawBlocks(Spatial b)
    {
        int rows = 3;
        int columns = 5;
        
        int count = 0;
        
        float offset = 0.4f;
        
        float xPos = -columns + offset;
        float yPos = 3.75f - offset;
        
        for (int i = -rows; i < rows; i++)
        {
            for (int j = -columns; j < columns; j++)
            {
                blocks.add(b.clone());
                blocks.get(count).setLocalTranslation(xPos, yPos, 0);
                rootNode.attachChild(blocks.get(count));
                xPos += 1;
                count++;
            }
            yPos -= 0.5;
            xPos = -columns + offset;
        }
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
        if (isRunning)
        {
            ballLogic(tpf);
            blockLogic();
        }
    }
    
    private void blockLogic()
    {
        CollisionResults results = new CollisionResults();
        BoundingVolume ballBound = ball.getWorldBound();
        
        for (int i = 0; i < blocks.size(); i++)
        {
            if(blocks.get(i).hasAncestor(rootNode))
            {
                if (blocks.get(i).collideWith(ballBound, results) > 0)
                {
                    blocks.get(i).removeFromParent();
                    deltaY *= -1;
                }
            }
        }
//        if (block.hasAncestor(rootNode))
//        {
//            if (block.collideWith(ballBound, results) > 0)
//            {
//                if (block.removeFromParent())
//                {
//                    deltaY *= -1;
//                }
//    //            rootNode.detachChild(block);
//    //            deltaY *= -1;
//            }
//        }
    }
    
    private void ballLogic(float tpf)
    {
        Vector3f b = ball.getLocalTranslation();
        
        CollisionResults results = new CollisionResults();
        BoundingVolume ballBound = ball.getWorldBound();
        
        if (player.collideWith(ballBound, results) > 0)
        {
            deltaY = 1;
        }
        
        if (b.x > 5.25)
        {
            deltaX = -1;
        }
        if (b.x < -5.25)
        {
            deltaX = 1;
        }
        if (b.y > 4)
        {
            deltaY = -1;
        }
        if (b.y < -4)
        {
            deltaY = 1;
        }
        ball.setLocalTranslation(b.x + ballXSpeed * tpf * deltaX, b.y + ballYSpeed * tpf * deltaY, b.z);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
