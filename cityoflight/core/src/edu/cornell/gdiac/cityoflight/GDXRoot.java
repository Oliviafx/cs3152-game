/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter.
 * There must be some undocumented OpenGL code in setScreen.
 *
 * This class differs slightly from the labs in that the AssetManager is now a
 * singleton and is not constructed by this class.
 *
 * Author: Walker M. White
 * Version: 3/2/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.obstacle.*;

import java.util.logging.Level;

/**
 * Root class for a LibGDX.
 *
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However,
 * those classes are unique to each platform, while this class is the same across all
 * plaforms. In addition, this functions as the root class all intents and purposes,
 * and you would draw it as a root class in an architecture specification.
 */
public class GDXRoot extends Game implements ScreenListener {
	/** Drawing context to display graphics (VIEW CLASS) */
	private ObstacleCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private GameController controller;
	/** Player mode for the menu screen (CONTROLLER CLASS) */
	private MenuMode menu;
	/** Player mode for the level screen (CONTROLLER CLASS) */
	private LevelController levels;
	/** Player mode for the pause screen (CONTROLLER CLASS) */
	private PauseMode pause;
	/** List of all WorldControllers */
//	private WorldController[] controllers;

	/**
	 * Creates a new game from the configuration settings.
	 */
	public GDXRoot() {}

	/**
	 * Called when the Application is first created.
	 *
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new ObstacleCanvas();
		loading = new LoadingMode(canvas,1);
//		menu = new MenuMode(canvas, this);
//		levels = new LevelController(canvas);
		pause = new PauseMode(canvas);
		// Initialize the three game worlds
		controller = new GameController();
		controller.preLoadContent();
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		controller.unloadContent();
		controller.dispose();

		canvas.dispose();
		canvas = null;

		menu = null;
		levels = null;
		pause = null;
		// Unload all of the resources
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (exitCode == GameController.EXIT_MENU) {
//			loading.dispose();
//			loading = null;
		    menu = new MenuMode(canvas, this);
			setScreen(menu);
			//			setScreen(menu);
//			System.out.println(getScreen().equals(levels));

//			if (getScreen().equals(levels)) {
//				System.out.println("hiding");
//				levels.hide();
//			}
			if (levels != null) {
				levels.dispose();
				levels.setActive(false);
				levels = null;
			}
			menu.setScreenListener(this);
			menu.setCanvas(canvas);
			menu.reset();




		}
		if (exitCode == GameController.EXIT_LEVEL) {
			menu.dispose();
			menu = null;
			levels = new LevelController(canvas);
			levels.setScreenListener(this);
			levels.setCanvas(canvas);
			levels.reset(canvas);
			setScreen(levels);
			levels.setActive(true);
		}

//		if (exitCode == GameController.EXIT_PAUSE) {
//			pause.setScreenListener(this);
//			pause.setCanvas(canvas);
//			pause.reset();
//			setScreen(pause);
//		}
//		else if (screen == loading) {
//			System.out.println("here");
//			controller.loadContent();
//			controller.setScreenListener(this);
//			controller.setCanvas(canvas);
//			controller.reset();
//			setScreen(controller);
//
//			loading.dispose();
//			loading = null;
//		}
		else if (exitCode == GameController.EXIT_PLAY) {
			if (menu!=null) {
				menu.dispose();
				menu = null;
			}
			if (getScreen().equals(levels)) {
				if (levels.goLevelOne()) {
					controller.setWhichLevel(1);
					System.out.println(controller.whichlevel);
				}
				else if (levels.goLevelTwo()) {
					controller.setWhichLevel(2);
					System.out.println(controller.whichlevel);
				}
				else if (levels.goLevelThree()) {
					controller.setWhichLevel(3);
					System.out.println(controller.whichlevel);
				}
			}
			else if (controller.whichlevel == 0) {
				controller.setWhichLevel(1);
			}
			controller.loadContent();
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			if (!getScreen().equals(pause)) {
				controller.reset();
			}
			setScreen(controller);
			if (loading !=null) {
				loading.dispose();
				loading = null;
			}
		}

		else if (exitCode == GameController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

	public void render() {
		super.render();
	}
}
