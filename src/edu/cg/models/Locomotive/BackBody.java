package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;


/***
 * A 3D locomotive back body renderer. The back-body of the locomotive model is composed of a chassis, two back wheels,
 * , a roof, windows and a door.
 */
public class BackBody implements IRenderable {
    // The back body is composed of one box that represents the locomotive front body.
    private Box chassis = new Box(Specification.BACK_BODY_WIDTH, Specification.BACK_BODY_HEIGHT, Specification.BACK_BODY_DEPTH);
    // The back body is composed of two back wheels.
    private Wheel wheel = new Wheel();
    // The back body is composed of a roof that lies on-top of the locomotive chassis.
    private Roof roof = new Roof();
    // TODO (9): Define your window/door objects here. You are free to implement these models as you wish as long as you
    //           stick to the locomotive sketch.
    private Box door = new Box(Specification.DOOR_WIDTH, Specification.DOOR_HEIGHT, Specification.DOOR_DEPTH);
    private Box window = new Box(Specification.WINDOW_WIDTH, Specification.WINDOW_HEIGHT, Specification.WINDOW_DEPTH);
    private Box backWindow = new Box(Specification.BACKWINDOW_WIDTH, Specification.BACKWINDOW_HEIGHT, Specification.BACKWINDOW_DEPTH);

    @Override
    public void render() {
        glPushMatrix();
        // TODO(8): render the back-body of the locomotive model. You need to combine the chassis, wheels and roof using
        //          affine transformations. In addition, you need to render the back-body windows and door. You can do
        //          that using simple QUADRATIC polygons (use GL_QUADS).
        //chassis
        glPushMatrix();
        Materials.setMaterialChassis();
        chassis.render();
        glPopMatrix();
        //roof
        glPushMatrix();
        glTranslatef(0, (float) Specification.BACK_BODY_HEIGHT / 2, (float) (-Specification.BACK_BODY_DEPTH / 2 + Specification.EPS));
        roof.render();
        glPopMatrix();
        //wheels
        glPushMatrix();
        glTranslated(Specification.BACK_BODY_WIDTH / 2, -Specification.BACK_BODY_HEIGHT / 2, -Specification.WHEEL_RADIUS + Specification.EPS);
        wheel.render();
        glPopMatrix();
        glPushMatrix();
        glTranslated(-Specification.BACK_BODY_WIDTH / 2, -Specification.BACK_BODY_HEIGHT / 2, -Specification.WHEEL_RADIUS + Specification.EPS);
        wheel.render();
        glPopMatrix();
        //windows
        for (int i = 0; i < 3; i++) {
            glPushMatrix();
            Materials.setMaterialWindow();
            glTranslatef((float) (Specification.BACK_BODY_WIDTH / 2 + Specification.EPS), (float) ((Specification.BACK_BODY_HEIGHT - Specification.WINDOW_HEIGHT) / 4), (float) ((- (float)(Specification.BACK_BODY_DEPTH - Specification.BASE_UNIT) / 2.0) + Specification.BASE_UNIT + (Specification.BASE_UNIT + Specification.WINDOW_WIDTH) * i));
            glRotated(90, 0, 1, 0);
            window.render();
            glPopMatrix();
        }
        for (int i = 0; i < 2; i++) {
            glPushMatrix();
            Materials.setMaterialWindow();
            glTranslatef(- (float) (Specification.BACK_BODY_WIDTH / 2 + Specification.EPS), (float) ((Specification.BACK_BODY_HEIGHT - Specification.WINDOW_HEIGHT) / 4), (float) ((- (float)(Specification.BACK_BODY_DEPTH - Specification.BASE_UNIT) / 2.0) + Specification.BASE_UNIT + (Specification.BASE_UNIT + Specification.WINDOW_WIDTH) * i));
            glRotated(-90, 0, 1, 0);
            window.render();
            glPopMatrix();
        }
        glPushMatrix();
        Materials.setMaterialWindow();
        glTranslatef(0, (float) ((Specification.BACK_BODY_HEIGHT - Specification.BACKWINDOW_HEIGHT) / 4), (float) (Specification.BACK_BODY_DEPTH / 2 + Specification.EPS));
        backWindow.render();
        glPopMatrix();
        glPushMatrix();
        Materials.setMaterialWindow();
        glTranslatef(0, (float) ((Specification.BACK_BODY_HEIGHT - Specification.BACKWINDOW_HEIGHT)/ 4), (float) -(Specification.BACK_BODY_DEPTH / 2 + Specification.EPS));
        glRotated(180, 1, 0, 0);
        backWindow.render();
        glPopMatrix();
        //door
        glPushMatrix();
        Materials.setMaterialDoor();
        glTranslatef((float) - (Specification.BACK_BODY_WIDTH / 2 + Specification.EPS), -(float) ((Specification.BACK_BODY_HEIGHT - Specification.DOOR_HEIGHT) / 2), (float) (2.5 * Specification.BASE_UNIT));
        glRotated(-90, 0, 1, 0);
        door.render();
        glPopMatrix();



        glPopMatrix();
    }


    @Override
    public void init() {

    }
}
