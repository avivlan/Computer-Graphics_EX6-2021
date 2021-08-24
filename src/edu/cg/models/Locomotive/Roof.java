package edu.cg.models.Locomotive;

import edu.cg.models.IRenderable;
import edu.cg.util.glu.Cylinder;
import edu.cg.util.glu.Disk;

import static org.lwjgl.opengl.GL11.*;

/***
 * A 3D roof model renderer.
 * The roof is modeled using a cylinder bounded by disks that undergo a non-uniform scaling.
 */
public class Roof implements IRenderable {


    @Override
    public void render() {
        glPushMatrix();
        // TODO(7): Render the locomotive back body roof
        Materials.setMaterialRoof();
        glScalef(1, (float) (Specification.ROOF_HEIGHT * 2.5 + 0.35), 1);
        new Cylinder().draw((float) (Specification.ROOF_WIDTH) / 2, (float) (Specification.ROOF_WIDTH) / 2, (float) Specification.ROOF_DEPTH, 20, 1);
        glPushMatrix();
        glRotated(180, 0, 1, 0);
        Materials.setMaterialRoof();
        new Disk().draw(0, (float) (Specification.ROOF_WIDTH) / 2, 20, 1);
        glPopMatrix();
        glRotated(180, 1, 0, 0);
        glTranslatef(0, 0, (float) -Specification.ROOF_DEPTH);
        glPushMatrix();
        glRotated(180, 0, 1, 0);
        Materials.setMaterialRoof();
        new Disk().draw(0, (float) (Specification.ROOF_WIDTH) / 2, 20, 1);
        glPopMatrix();
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}
