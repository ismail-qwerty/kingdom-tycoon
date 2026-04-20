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
 
enum class ScreenType {
    LOADING,
    MAIN_MENU,
    GAME,
    MAP,
    EVENT,
    PRESTIGE,
    SETTINGS
}
 
enum class TransitionType {
    FADE,
    SLIDE_LEFT,
    SLIDE_RIGHT,
    FLASH_WHITE,
    NONE
}
 
object ScreenNavigator {
 
    private var game: KingdomTycoonGame? = null
    private val screenStack = Stack<ScreenType>()
 
    private var transitionStage: Stage? = null
    private var isTransitioning = false
 
    fun initialize(game: KingdomTycoonGame) {
        this.game = game
        screenStack.clear()
        transitionStage = Stage(ScreenViewport())
    }
 
    fun navigate(screenType: ScreenType, transition: TransitionType = TransitionType.FADE, addToStack: Boolean = true) {
        if (game == null) {
            Gdx.app.error("ScreenNavigator", "Navigator not initialized!")
            return
        }
 
        if (isTransitioning) {
            Gdx.app.log("ScreenNavigator", "Transition already in progress, ignoring")
            return
        }
 
        if (addToStack && screenStack.isNotEmpty()) {
            if (screenStack.peek() != screenType) {
                screenStack.push(screenType)
            }
        } else if (addToStack) {
            screenStack.push(screenType)
        }
 
        Gdx.app.log("ScreenNavigator", "Navigating to $screenType with $transition transition")
 
        when (transition) {
            TransitionType.FADE -> fadeTransition(screenType)
            TransitionType.SLIDE_LEFT -> slideTransition(screenType, true)
            TransitionType.SLIDE_RIGHT -> slideTransition(screenType, false)
            TransitionType.FLASH_WHITE -> flashTransition(screenType)
            TransitionType.NONE -> instantTransition(screenType)
        }
    }
 
    fun goBack(): Boolean {
        if (screenStack.size <= 1) {
            Gdx.app.log("ScreenNavigator", "No screen to go back to")
            return false
        }
        screenStack.pop()
        val previousScreen = screenStack.peek()
        Gdx.app.log("ScreenNavigator", "Going back to $previousScreen")
        navigate(previousScreen, TransitionType.FADE, addToStack = false)
        return true
    }
 
    fun clearStack() {
        screenStack.clear()
        Gdx.app.log("ScreenNavigator", "Screen stack cleared")
    }
 
    private fun fadeTransition(screenType: ScreenType) {
        isTransitioning = true
        val overlay = createOverlay(Color.BLACK)
        overlay.color.a = 0f
        transitionStage?.addActor(overlay)
        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.15f),
                Actions.run { setScreen(screenType) },
                Actions.fadeOut(0.15f),
                Actions.run {
                    overlay.remove()
                    isTransitioning = false
                }
            )
        )
    }
 
    private fun slideTransition(screenType: ScreenType, slideLeft: Boolean) {
        isTransitioning = true
        val overlay = createOverlay(Color.BLACK)
        val screenWidth = Gdx.graphics.width.toFloat()
        overlay.x = if (slideLeft) screenWidth else -screenWidth
        transitionStage?.addActor(overlay)
        overlay.addAction(
            Actions.sequence(
                Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow2Out),
                Actions.run { setScreen(screenType) },
                Actions.moveTo(if (slideLeft) -screenWidth else screenWidth, 0f, 0.3f, Interpolation.pow2In),
                Actions.run {
                    overlay.remove()
                    isTransitioning = false
                }
            )
        )
    }
 
    private fun flashTransition(screenType: ScreenType) {
        isTransitioning = true
        val overlay = createOverlay(Color.WHITE)
        overlay.color.a = 0f
        transitionStage?.addActor(overlay)
        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f),
                Actions.delay(0.3f),
                Actions.run { setScreen(screenType) },
                Actions.fadeOut(0.5f),
                Actions.run {
                    overlay.remove()
                    isTransitioning = false
                }
            )
        )
    }
 
    private fun instantTransition(screenType: ScreenType) {
        setScreen(screenType)
    }
 
    // FIX: Always remove the old screen instance before adding a new one.
    // KtxGame throws GdxRuntimeException if you addScreen() with a type
    // that is already registered. removeScreen() disposes and unregisters it.
    private fun setScreen(screenType: ScreenType) {
        val g = game ?: return
        val newScreen = createScreen(screenType) ?: return
 
        // Remove existing instance of this screen type to avoid duplicate registration
        try {
            when (newScreen) {
                is LoadingScreen -> g.removeScreen<LoadingScreen>()
                is MainMenuScreen -> g.removeScreen<MainMenuScreen>()
                is GameScreen -> g.removeScreen<GameScreen>()
                is MapScreen -> g.removeScreen<MapScreen>()
                is EventScreen -> g.removeScreen<EventScreen>()
                is PrestigeScreen -> g.removeScreen<PrestigeScreen>()
                is SettingsScreen -> g.removeScreen<SettingsScreen>()
                else -> {}
            }
        } catch (e: Exception) {
            // Screen wasn't registered yet — safe to ignore
        }
 
        g.addScreen(newScreen)
 
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
 
    private fun createScreen(screenType: ScreenType): KtxScreen? {
        val g = game ?: return null
        return when (screenType) {
            ScreenType.LOADING -> LoadingScreen(g)
            ScreenType.MAIN_MENU -> MainMenuScreen(g)
            ScreenType.GAME -> GameScreen(g)
            ScreenType.MAP -> MapScreen(g)
            ScreenType.EVENT -> EventScreen(g)
            ScreenType.PRESTIGE -> {
                val layer = g.gameEngine.prestigeSystem.getAvailablePrestigeLayer()
                if (layer != com.ismail.kingdom.models.PrestigeLayer.NONE) {
                    PrestigeScreen(g, layer)
                } else {
                    Gdx.app.error("ScreenNavigator", "No prestige layer available yet")
                    null
                }
            }
            ScreenType.SETTINGS -> SettingsScreen(g)
        }
    }
 
    private val overlayTextures = mutableListOf<Texture>()
 
    private fun createOverlay(color: Color): Image {
        val pixmap = Pixmap(Gdx.graphics.width, Gdx.graphics.height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        overlayTextures.add(texture)
        return Image(texture)
    }
 
    fun update(delta: Float) {
        transitionStage?.act(delta)
    }
 
    fun render() {
        transitionStage?.draw()
    }
 
    fun dispose() {
        transitionStage?.dispose()
        transitionStage = null
        overlayTextures.forEach { it.dispose() }
        overlayTextures.clear()
    }
 
    fun isTransitioning(): Boolean = isTransitioning
 
    fun getCurrentScreen(): ScreenType? {
        return if (screenStack.isNotEmpty()) screenStack.peek() else null
    }
}
