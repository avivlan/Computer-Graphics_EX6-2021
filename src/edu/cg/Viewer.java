package edu.cg;

import edu.cg.algebra.Vec;
import edu.cg.models.Locomotive.Locomotive;
import edu.cg.models.Track.Track;
import edu.cg.models.Track.TrackSegment;
import edu.cg.util.glu.GLU;
import edu.cg.util.glu.Project;;
import static edu.cg.util.glu.Project.gluPerspective;
import static org.lwjgl.opengl.GL11.glLightModelfv;
import edu.cg.models.Locomotive.Specification;
import static org.lwjgl.opengl.GL21.*;

/**
 * An OpenGL model viewer
 */
public class Viewer {
    int canvasWidth, canvasHeight;
    private final GameState gameState; // Tracks the vehicle movement and orientation
    private final Locomotive car; // The locomotive we wish to render.
    private final Track gameTrack; // The track we wish to render.
    // driving direction, or looking down on the scene from above.
    private Vec carCameraTranslation; // The accumulated translation that should be applied on the car and camera.
    private boolean isModelInitialized = false; // Indicates whether initModel was called.
    private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
    private boolean isBirdseyeView = false; // Indicates whether the camera's perspective corresponds to the vehicle's

    // TODO: Set the initial position of the vehicle in the scene by assigning a value to carInitialPosition.
    private final double[] carInitialPosition = {0, 1, -4};


    // TODO: set the car scale as you wish - we uniformly scale the car by 3.0.
    private final double carScale = 3.0;

    // TODO: You can add additional fields to assist your implementation, for example:
    // - Camera initial position for standard 3rd person mode(should be fixed)
    // - Camera initial position for birdseye view)
    // - Light colors
    // Or in short anything reusable - this make it easier for your to keep track of your implementation.
    private final double[] cameraThirdPerson = {0, 3.0, 0};
    private final double[] cameraBirdseye = {0, 50, -22};



    public Viewer(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        this.gameState = new GameState();
        this.gameTrack = new Track();
        this.carCameraTranslation = new Vec(0.0D);
        this.car = new Locomotive();
    }

    public void render() {
        if (!this.isModelInitialized)
            initModel();
        // TODO : Define background color for the scene in day mode and in night.
        if (this.isDayMode) {
            // TODO: Setup background when day mode is on
            // use gl.glClearColor() function.
            glClearColor(0.3f, 0.85f, 1f, 1f);
        } else {
            // TODO: Setup background when night mode is on.
            glClearColor(0.08f, 0.08f, 0.5f, 1f);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // TODO: Read this part of the code, understand the flow that goes into rendering the scene.
        // Step (1) Update the accumulated translation that needs to be
        // applied on the car, camera and light sources.
        updateCarCameraTranslation();
        // Step (2) Position the camera and setup its orientation.
        setupCamera();
        // Step (3) setup the lights.
        setupLights();
        // Step (4) render the car.
        renderVehicle();
        // Step (5) render the track.
        renderTrack();
    }

    public void init() {
        // TODO(*) In your final submission you need to make sure that BACK FACE culling is enabled.
        //      You may disable face culling while building your model, and only later return it.
        //      Note that doing so may require you to modify the way you present the vertices to OPENGL in order for the
        //      normal of all surface be facing outside. See recitation 8 for more information about face culling.
        glCullFace(GL_BACK);    // Set Culling Face To Back Face
        glEnable(GL_CULL_FACE); // Enable back face culling

        // Enable other flags for OPENGL.
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);


        reshape(0, 0, canvasWidth, canvasHeight);
    }

    private void updateCarCameraTranslation() {
        // Here we update the car and camera translation values (not the ModelView-Matrix).
        // - We always keep track of the car offset relative to the starting
        // point.
        // - We change the track segments here if necessary.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        Vec ret = this.gameState.getNextTranslation();
        this.carCameraTranslation = this.carCameraTranslation.add(ret);
        // Min and Max calls to make sure we do not exceed the lateral boundaries of the track.
        double dx = Math.max(this.carCameraTranslation.x, -TrackSegment.ASPHALT_LENGTH / 2 - 2);
        this.carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_LENGTH / 2 + 2);
        // If the car reaches the end of the track segment, we generate a new segment.
        if (Math.abs(this.carCameraTranslation.z) >= TrackSegment.TRACK_SEGMENT_LENGTH - this.carInitialPosition[2]) {
            this.carCameraTranslation.z = -((float) (Math.abs(this.carCameraTranslation.z) % TrackSegment.TRACK_SEGMENT_LENGTH));
            this.gameTrack.changeTrackSegment();
        }
    }

    private void setupCamera() {
        // TODO: In this method you are advised to use :
        //       GLU glu = new GLU();
        //       glu.gluLookAt();
        GLU glu = new GLU();

        if (this.isBirdseyeView) {
            // TODO Setup camera for the Birds-eye view (You need to configure the viewing transformation accordingly).
            float camX = this.carCameraTranslation.x;
            float camY = this.carCameraTranslation.y;
            float camZ = this.carCameraTranslation.z;
            camX += (float)(cameraBirdseye[0]);
            camY += (float)(cameraBirdseye[1]);
            camZ += (float)(cameraBirdseye[2]);
            glu.gluLookAt(camX, camY, camZ, camX, camY - 1f, camZ, 0, 0, -1);
        }
        else {
            // TODO Setup camera for standard 3rd person view.
            float camX = this.carCameraTranslation.x;
            float camY = this.carCameraTranslation.y;
            float camZ = this.carCameraTranslation.z;
            camX += (float)(cameraThirdPerson[0]);
            camY += (float)(cameraThirdPerson[1]);
            camZ += (float)(cameraThirdPerson[2]);
            glu.gluLookAt(camX, camY, camZ, camX, camY, camZ - 10f, 0, 1, 0);
        }
    }

    private void setupLights() {
        if (this.isDayMode) {
            // TODO Setup day lighting.
            // * Remember: switch-off any light sources that were used in night mode and are not use in day mode.
            glDisable(GL_LIGHT1);
            glDisable(GL_LIGHT2);
            this.startDay();

        } else {
            // TODO Setup night lighting - here you should only set the ambient light source.
            //      The locomotive's spotlights should be defined in the car local coordinate system.
            //      so it is better to define the car light properties right before your render the locomotive rather
            //      than at this point.
            glDisable(GL_LIGHT0);
            this.startNight();
        }
    }

    private void startNight() {
        glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[]{0.25f, 0.25f, 0.3f, 1f});
    }

   private void startDay() {
        float[] colorOfDay = {1f, 1f, 1f, 1f};
        Vec directionOfLight = (new Vec(0d, 1d, 1d)).normalize();
        float[] positionOfLight = {directionOfLight.x, directionOfLight.y, directionOfLight.z, 0f};
        glLightfv(GL_LIGHT0, GL_SPECULAR, colorOfDay);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, colorOfDay);
        glLightfv(GL_LIGHT0, GL_POSITION, positionOfLight);
        glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] {0.1f, 0.1f, 0.1f, 1f});
        glEnable(GL_LIGHT0);
    }

    private void renderTrack() {
        glPushMatrix();
        // TODO : Note that if you wish to support textures, the render method of gameTrack must be changed.
        this.gameTrack.render();
        glPopMatrix();
    }

    private void startCarLights() {
        float dx = (float) (Specification.FRONT_BODY_WIDTH / 4);
        float dy = (float) (Specification.FRONT_BODY_HEIGHT);
        float dz = (float) (Specification.FRONT_BODY_DEPTH + Specification.EPS);
        float[] position1 = {-dx, dy, dz, 1f};
        float[] position2 = {dx, dy, dz, 1f};
        float[] directionOfLights = {0f, 0f, 1f, 0f};
        this.startCarLight(position1, directionOfLights, GL_LIGHT1);
        this.startCarLight(position2, directionOfLights, GL_LIGHT2);
    }

    private void startCarLight(float[] position, float[] direction, int light) {
        float[] colorOfLight = {0.77f, 0.77f, 0.77f, 1f};
        glLightfv(light, GL_POSITION, position);
        glLightf(light, GL_SPOT_CUTOFF, 90f);
        glLightfv(light, GL_SPOT_DIRECTION, direction);
        glLightfv(light, GL_SPECULAR, colorOfLight);
        glLightfv(light, GL_DIFFUSE, colorOfLight);
        glEnable(light);
    }



    private void renderVehicle() {
        // TODO: Render the vehicle.
        // * Remember: the vehicle's position should be the initial position + the accumulated translation.
        //             This will simulate the car movement.
        // * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
        // * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
        // * You should set up the car lights right before you render the locomotive after the appropriate transformations
        // * have been applied. This ensures that the light sources are fixed to the locomotive (ofcourse all of this
        // * is only relevant to rendering the vehicle in night mode).
        glPushMatrix();
        glTranslated(this.carInitialPosition[0] + this.carCameraTranslation.x, this.carInitialPosition[1] + this.carCameraTranslation.y, this.carInitialPosition[2] + this.carCameraTranslation.z);
        double carRotation = this.gameState.getCarRotation();
        glRotated(180d - carRotation, 0d, 1d, 0d);
        glScaled(this.carScale, this.carScale, this.carScale);
        if (!this.isDayMode){
            this.startCarLights();
        }
        this.car.render();
        glPopMatrix();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void initModel() {
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_SMOOTH);
        this.gameTrack.init();
        this.car.init();
        this.isModelInitialized = true;
    }


    public void reshape(int x, int y, int width, int height) {
        // We recommend using gluPerspective, which receives the field of view in the y-direction. You can use this
        // method by importing it via:
        // >> import static edu.cg.util.glu.Project.gluPerspective;
        // Further information about this method can be found in the recitation materials.
        glViewport(x, y, width, height);
        canvasWidth = width;
        canvasHeight = height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = (float)width / (float)height;
        if (this.isBirdseyeView) {
            // TODO : Set a projection matrix for birdseye view mode.
            float fieldOfViewY = 50.0f;
            gluPerspective(fieldOfViewY, aspectRatio, (float)(this.cameraBirdseye[1] - 10f), (float)(this.cameraBirdseye[1] + 10f));
        } else {
            // TODO : Set a projection matrix for third person mode.
            float fieldOfViewY = 120.0f;
            gluPerspective(fieldOfViewY, aspectRatio, 0.1f, 250.0f);

        }
    }

    public void toggleNightMode() {
        this.isDayMode = !this.isDayMode;
    }

    public void changeViewMode() {
        this.isBirdseyeView = !this.isBirdseyeView;
    }
}
