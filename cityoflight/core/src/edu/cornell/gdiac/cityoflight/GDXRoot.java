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
 * Authors: Walker M. White, Katie Sadoff
 * Version: 3/2/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	/** Player mode for the help screen (CONTROLLER CLASS) */
	private HelpMode help;
	/** DrawHelper for drawing transitions */
	private DrawHelper drawHelper;

	private Music bgm;
	private Music menuMusic;

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
		drawHelper = new DrawHelper();

		loading = new LoadingMode(canvas,1);
//		menu = new MenuMode(canvas, this);
		menu = new MenuMode(canvas, this);

//		levels = new LevelController(canvas);
		pause = new PauseMode(canvas);
		help = new HelpMode(canvas);
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
		help = null;
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
//			System.out.println("exit menu");
//			loading.dispose();
//			loading = null;
//			System.out.println("stop music");
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

			if (controller != null) {
				if (controller.getBGM() != null) {
					controller.getBGM().stop();
					controller.getDet_bgm().stop();
				}
			}
			menu.setScreenListener(this);
			menu.setCanvas(canvas);
			menu.reset();
		}
		if (exitCode == GameController.EXIT_LEVEL) {
//			System.out.println("exit level");
            if (menu != null) {
                menu.dispose();
                menu = null;
            }

			levels = new LevelController(canvas);
			levels.setScreenListener(this);
			levels.setCanvas(canvas);
			levels.reset(canvas);
			setScreen(levels);
			levels.setActive(true);
		}

		if (exitCode == GameController.EXIT_HELP) {
//			System.out.println("exit help");

			menu.dispose();
			menu = null;
			help = new HelpMode(canvas);
			help.setScreenListener(this);
			help.setCanvas(canvas);
			help.reset();
			setScreen(help);
//			help.setActive(true);
		}

		if (exitCode == GameController.EXIT_PAUSE) {
//			System.out.println("exit pause");
			controller.getBGM().pause();
			controller.getDet_bgm().pause();
//			if (pause.isMusic()) {
//				controller.setMusicPlay(true);
////				System.out.println("setmusicplay true");
//			}
//			else {
//				controller.setMusicPlay(false);
//				System.out.println("setmusicplay false");
//			}
//			if (pause.isSound()) {
//				controller.setSoundPlay(true);
//			}
//			else {
//				controller.setSoundPlay(false);
//			}

			pause.setScreenListener(this);
			pause.setCanvas(canvas);
			pause.reset();
			setScreen(pause);



		}
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
//			System.out.println("EXIT PLAY");

//			if (menu!=null) {
//				menu.dispose();
//				menu = null;
//			}
			if (menu != null) {
//				System.out.println("stop music");
				menuMusic = menu.getMenuMusic();
				menuMusic.stop();
				menu.dispose();
				menu = null;
//				System.out.println("in GDXRoot music playing "+menuMusic.isPlaying());

			}
			if (pause.isMusic()) {
				controller.setMusicPlay(true);
//				System.out.println("setmusicplay true");
			}
			else {
				controller.setMusicPlay(false);
//				System.out.println("setmusicplay false");
			}
			if (pause.isSound()) {
				controller.setSoundPlay(true);
			}
			else {
				controller.setSoundPlay(false);
			}

				if (getScreen().equals(levels)) {
					if (levels.goLevelOne()) {
						controller.setWhichLevel(1);
					} else if (levels.goLevelTwo()) {
						controller.setWhichLevel(2);
					} else if (levels.goLevelThree()) {
						controller.setWhichLevel(3);
					} else if (levels.goLevelFour()) {
						controller.setWhichLevel(4);
					} else if (levels.goLevelFive()) {
						controller.setWhichLevel(5);
					} else if (levels.goLevelSix()) {
						controller.setWhichLevel(6);
					} else if (levels.goLevelSeven()) {
						controller.setWhichLevel(7);
					} else if (levels.goLevelEight()) {
						controller.setWhichLevel(8);
					}
				} else if (controller.whichlevel == 0) {
					controller.setWhichLevel(1);
				}
				controller.loadContent();
				controller.setScreenListener(this);
				controller.setCanvas(canvas);
				if (!getScreen().equals(pause)) {
					controller.reset();
				} else {
					pause.reset();
				}
				setScreen(controller);
				if (loading != null) {
					loading.dispose();
					loading = null;
				}
				controller.setMenu(menu);



//			else {
//				System.out.println("was null");
//			}
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
