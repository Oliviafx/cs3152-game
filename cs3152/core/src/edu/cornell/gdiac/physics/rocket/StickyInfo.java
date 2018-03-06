package edu.cornell.gdiac.physics.rocket;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class StickyInfo {
    Body bodyA;
    Body bodyB;
    public StickyInfo(Body bodyA, Body bodyB){
        this.bodyA = bodyA;
        this.bodyB = bodyB;
    }
}
