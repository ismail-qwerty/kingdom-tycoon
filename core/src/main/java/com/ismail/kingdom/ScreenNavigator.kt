// PATH: core/src/main/java/com/ismail/kingdom/ScreenNavigator.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import java.util.*

/**
 * NAVIGATION FLOW DOCUMENTATION
 * =============================
 * 
 * Screen Hierarchy:
 * 
 * LoadingScreen (initial)
 *   ↓ (auto-transition when assets loaded)
 * MainMenuScreen
 *   ↓ (tap to start - loads or creates new game)
 * GameScreen (main hub)
 *   ├→ MapScreen (MAP button)
 *   │   └→ GameScreen (BACK button)
 *   ├→ EventScreen (EVENTS button)
 *   │   └→ GameScreen (BACK button)
 *   ├→ PrestigeScreen (PRESTIGE button)
 *   │   ├→ GameScreen (CANCEL button)
 *   │   └→ GameScreen (after prestige animation)
 *   └→ SettingsScreen (settings icon)
 *       ├→ GameScreen (BACK button)
 *       └→ MainMenuScreen (after reset progress)
 * 
 * Special Cases:
 * - Any screen can return to MainMenuScreen via reset progress
 * - Android back button handled per screen (see goBack())
 * - GameEngine is SHARED across all screens (singleton pattern)
 * - Screens should NOT create new GameEngine instances
 * 
 * Screen Lifecycle:
 * 1. show() - called when screen becomes active
 * 2. render(delta) - called every frame
 * 3. hide() - called when screen becomes inactive
 * 4. dispose() - called when screen is removed from memory
 * 
 * Resource Management:
 * - Each screen must dispose its own resources in dispose()
 * - GameEngine is NOT disposed by screens (managed by KingdomTycoonGame)
 * - Stage, Skin, Textures created by screen must be disposed
 * - Shared resources (GameAssets) should NOT be disposed by screens
 */

// Screen types for navigation
enum class ScreenType {
    LOADING,
    MAIN_MENU,
    GAME,
    MAP,
    EVENT,
    PRESTIGE,
    SETTINGS
}

// Transition types for screen changes
enum class TransitionType {
    FADE,           // 0.3s fade out/in
    SLIDE_LEFT,     // Slide from right to left
    SLIDE_RIGHT,    // Slide from left to right
    FLASH_WHITE,    // White flash (for prestige)
    NONE            // Instant transition
}

// Singleton screen navigator managing screen transitions and back stack
object ScreenNavigator {
    
    private var game: KingdomTycoonGame? = null
    private val screenStack = Stack<ScreenType>()
    
    // Transition overlay stage
    private var transitionStage: Stage? = null
    private var isTransitioning = false
    
    // Initializes navigator with game instance
    fun initialize(game: KingdomTycoonGame) {
        this.game = game
        screenStack.clear()
        
        // Create transition stage
        transitionStage = Stage(ScreenViewport())
    }
    
    // Navigates to specified screen with transition
    fun navigate(screenType: ScreenType, transition: TransitionType = TransitionType.FADE, addToStack: Boolean = true) {
        if (game == null) {
            Gdx.app.error("ScreenNavigator", "Navigator not initialized!")
            return
        }
        
        if (isTransitioning) {
            Gdx.app.log("ScreenNavigator", "Transition already in progress, ignoring")
            return
        }
        
        // Add current screen to stack if requested
        if (addToStack && screenStack.isNotEmpty()) {
            // Don't add duplicate
            if (screenStack.peek() != screenType) {
                screenStack.push(screenType)
            }
        } else if (addToStack) {
            screenStack.push(screenType)
        }
        
        Gdx.app.log("ScreenNavigator", "Navigating to $screenType with $transition transition")
        
        // Perform transition
        when (transition) {
            TransitionType.FADE -> fadeTransition(screenType)
            TransitionType.SLIDE_LEFT -> slideTransition(screenType, true)
            TransitionType.SLIDE_RIGHT -> slideTransition(screenType, false)
            TransitionType.FLASH_WHITE -> flashTransition(screenType)
            TransitionType.NONE -> instantTransition(screenType)
        }
    }
    
    // Goes back to previous screen in stack
    fun goBack(): Boolean {
        if (screenStack.size <= 1) {
            Gdx.app.log("ScreenNavigator", "No screen to go back to")
            return false
        }
        
        // Pop current screen
        screenStack.pop()
        
        // Get previous screen
        val previousScreen = screenStack.peek()
        
        Gdx.app.log("ScreenNavigator", "Going back to $previousScreen")
        
        // Navigate without adding to stack
        navigate(previousScreen, TransitionType.FADE, addToStack = false)
        
        return true
    }
    
    // Clears screen stack (used for reset)
    fun clearStack() {
        screenStack.clear()
        Gdx.app.log("ScreenNavigator", "Screen stack cleared")
    }
    
    // Fade transition
    private fun fadeTransition(screenType: ScreenType) {
        isTransitioning = true
        
        val overlay = createOverlay(Color.BLACK)
        overlay.color.a = 0f
        
        transitionStage?.addActor(overlay)
        
        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.15f),
                Actions.run {
                    setScreen(screenType)
                },
                Actions.fadeOut(0.15f),
                Actions.run {
                    overlay.remove()
                    isTransitioning = false
                }
            )
        )
    }
    
    // Slide transition
    private fun slideTransition(screenType: ScreenType, slideLeft: Boolean) {
        isTransitioning = true
        
        val overlay = createOverlay(Color.BLACK)
        val screenWidth = Gdx.graphics.width.toFloat()
        
        overlay.x = if (slideLeft) screenWidth else -screenWidth
        
        transitionStage?.addActor(overlay)
        
        overlay.addAction(
            Actions.sequence(
                Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow2Out),
                Actions.run {
                    setScreen(screenType)
                },
                Actions.moveTo(if (slideLeft) -screenWidth else screenWidth, 0f, 0.3f, Interpolation.pow2In),
                Actions.run {
                    overlay.remove()
                    isTransitioning = false
                }
            )
        )
    }
    
    // Flash white transition (for prestige)
    private fun flashTransition(screenType: ScreenType) {
        isTransitioning = true
        
        val overlay = createOverlay(Color.WHITE)
        overlay.color.a = 0f
        
        transitionStage?.addActor(overlay)
        
        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f),
                Actions.delay(0.3f),
                Actions.run {
                    setScreen(screenType)
                },
                Actions.fadeOut(0.5f),
                Actions.run {
                    overlay.remove()
                    isTransitioning = false
                }
            )
        )
    }
    
    // Instant transition
    private fun instantTransition(screenType: ScreenType) {
        setScreen(screenType)
    }
    
    // Sets the screen
    private fun setScreen(screenType: ScreenType) {
        val g = game ?: return
        val newScreen = createScreen(screenType) ?: return
        
        // Add screen if not already registered
        g.addScreen(newScreen)
        
        // Use reflection to call setScreen with proper type
        when (newScreen) {
            is LoadingScreen -> g.setScreen<LoadingScreen>()
            is MainMenuScreen -> g.setScreen<MainMenuScreen>()
            is GameScreen -> g.setScreen<GameScreen>()
            is MapScreen -> g.setScreen<MapScreen>()
            is EventScreen -> g.setScreen<EventScreen>()
            is PrestigeScreen -> g.setScreen<PrestigeScreen>()
            is SettingsScreen -> g.setScreen<SettingsScreen>()
            else -> Gdx.app.error("ScreenNavigator", "Unknown screen type: ${newScreen::class.simpleName}")
        }
    }
    
    // Creates screen instance
    private fun createScreen(screenType: ScreenType): KtxScreen? {
        val g = game ?: return null
        
        return when (screenType) {
            ScreenType.LOADING -> LoadingScreen(g)
            ScreenType.MAIN_MENU -> MainMenuScreen(g)
            ScreenType.GAME -> GameScreen(g)
            ScreenType.MAP -> MapScreen(g)
            ScreenType.EVENT -> EventScreen(g)
            ScreenType.PRESTIGE -> {
                // Determine prestige layer
                val layer = g.gameEngine.prestigeSystem.getAvailablePrestigeLayer()
                if (layer != null) {
                    PrestigeScreen(g, layer)
                } else {
                    Gdx.app.error("ScreenNavigator", "No prestige layer available")
                    null
                }
            }
            ScreenType.SETTINGS -> SettingsScreen(g)
        }
    }
    
    // Creates overlay image
    private fun createOverlay(color: Color): Image {
        val pixmap = Pixmap(Gdx.graphics.width, Gdx.graphics.height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        
        return Image(texture)
    }
    
    // Updates transition stage
    fun update(delta: Float) {
        transitionStage?.act(delta)
    }
    
    // Renders transition stage
    fun render() {
        transitionStage?.draw()
    }
    
    // Disposes navigator resources
    fun dispose() {
        transitionStage?.dispose()
        transitionStage = null
    }
    
    // Checks if currently transitioning
    fun isTransitioning(): Boolean = isTransitioning
    
    // Gets current screen type
    fun getCurrentScreen(): ScreenType? {
        return if (screenStack.isNotEmpty()) screenStack.peek() else null
    }
}
