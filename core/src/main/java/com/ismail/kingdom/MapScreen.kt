// PATH: core/src/main/java/com/ismail/kingdom/MapScreen.kt
package com.ismail.kingdom
 
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ismail.kingdom.assets.AssetDescriptors
import com.ismail.kingdom.assets.GameAssets
import com.ismail.kingdom.models.MapTile
import com.ismail.kingdom.ui.MapTileActor
import com.ismail.kingdom.ui.MapTilePopup
import ktx.app.KtxScreen
 
// Map exploration screen with 8x8 tile grid
class MapScreen(private val game: KingdomTycoonGame) : KtxScreen {
 
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
 
    private val mapContainer = Container<Table>()
    private val mapTable = Table()
    private val tileActors = mutableMapOf<Pair<Int, Int>, MapTileActor>()
 
    private var currentPopup: MapTilePopup? = null
    private var selectedTile: MapTileActor? = null
 
    private var backgroundTexture: Texture? = null
 
    private var zoomLevel = 1.0f
    private val MIN_ZOOM = 0.5f
    private val MAX_ZOOM = 2.0f
 
    private var panOffsetX = 0f
    private var panOffsetY = 0f
    private var panVelocity = 0f
    private val PAN_VELOCITY_THRESHOLD = 50f
 
    private val GRID_SIZE = 8
    private val TILE_SIZE = 100f
    private val BANNER_AD_HEIGHT = 60f
 
    override fun show() {
        val gestureDetector = GestureDetector(MapGestureListener())
        val inputMultiplexer = com.badlogic.gdx.InputMultiplexer()
        inputMultiplexer.addProcessor(stage)
        inputMultiplexer.addProcessor(gestureDetector)
        Gdx.input.inputProcessor = inputMultiplexer
 
        loadBackground()
        createSkin()
        buildUI()
 
        // Show banner ad
        game.adsManager?.showBannerAd(true)
    }
 
    private fun loadBackground() {
        try {
            if (GameAssets.isLoaded()) {
                val eraId = game.gameEngine.gameState.currentEra
                backgroundTexture = GameAssets.getTexture(when (eraId) {
                    1 -> AssetDescriptors.Eras.ERA1_BACKGROUND
                    2 -> AssetDescriptors.Eras.ERA2_BACKGROUND
                    3 -> AssetDescriptors.Eras.ERA3_BACKGROUND
                    4 -> AssetDescriptors.Eras.ERA4_BACKGROUND
                    else -> AssetDescriptors.Eras.ERA5_BACKGROUND
                })
            }
        } catch (e: Exception) {
            Gdx.app.log("MapScreen", "Failed to load background: ${e.message}")
        }
    }
 
    private fun createSkin() {
        val font = com.badlogic.gdx.graphics.g2d.BitmapFont()
        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        skin.add("default", labelStyle)
        skin.add("body", labelStyle)
        val goldLabelStyle = Label.LabelStyle(font, Color.GOLD)
        skin.add("gold-small", goldLabelStyle)
        skin.add("gold-large", goldLabelStyle)
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        buttonStyle.fontColor = Color.WHITE
        skin.add("default", buttonStyle)
    }
 
    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)
 
        val backButton = TextButton("← BACK", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                ScreenNavigator.goBack()
            }
        })
 
        val topBar = Table()
        topBar.add(backButton).size(120f, 50f).pad(10f).left()
        root.add(topBar).fillX().top().row()
 
        mapContainer.fill()
        mapContainer.actor = mapTable
        root.add(mapContainer).expand().fill().row()
 
        val bannerAdSpace = Table()
        bannerAdSpace.background = createColorDrawable(Color(0.1f, 0.1f, 0.1f, 1f))
        root.add(bannerAdSpace).fillX().height(BANNER_AD_HEIGHT)
 
        buildMapGrid()
    }
 
    private fun buildMapGrid() {
        mapTable.clear()
        tileActors.clear()
 
        val eraId = game.gameEngine.gameState.currentEra
        val mapTiles = game.gameEngine.gameState.mapTiles
 
        for (y in (GRID_SIZE - 1) downTo 0) {
            for (x in 0 until GRID_SIZE) {
                val tile = mapTiles.find { it.x == x && it.y == y }
                if (tile != null) {
                    val tileActor = MapTileActor(tile, eraId) { clickedTile -> onTileClicked(clickedTile) }
                    tileActors[Pair(x, y)] = tileActor
                    mapTable.add(tileActor).size(TILE_SIZE, TILE_SIZE).pad(2f)
                } else {
                    mapTable.add().size(TILE_SIZE, TILE_SIZE).pad(2f)
                }
            }
            mapTable.row()
        }
        centerMap()
    }
 
    private fun centerMap() {
        val mapWidth = GRID_SIZE * (TILE_SIZE + 4f)
        val mapHeight = GRID_SIZE * (TILE_SIZE + 4f)
        val screenWidth = stage.viewport.worldWidth
        val screenHeight = stage.viewport.worldHeight - BANNER_AD_HEIGHT - 60f
        panOffsetX = (screenWidth - mapWidth) / 2f
        panOffsetY = (screenHeight - mapHeight) / 2f
        updateMapPosition()
    }
 
    private fun updateMapPosition() {
        mapTable.setScale(zoomLevel)
        mapTable.setPosition(panOffsetX, panOffsetY)
    }
 
    private fun onTileClicked(tile: MapTile) {
        if (panVelocity > PAN_VELOCITY_THRESHOLD) return
        selectedTile?.setSelected(false)
        val tileActor = tileActors[Pair(tile.x, tile.y)]
        tileActor?.setSelected(true)
        selectedTile = tileActor
        currentPopup?.dismiss()
        showTilePopup(tile)
    }
 
    private fun showTilePopup(tile: MapTile) {
        val popup = MapTilePopup(
            tile, skin, stage, game.gameEngine.gameState.currentGold,
            onExploreClicked = { exploredTile -> onTileExplore(exploredTile) },
            onDismiss = { currentPopup = null; selectedTile?.setSelected(false); selectedTile = null }
        )
        currentPopup = popup
        stage.addActor(popup)
    }
 
    private fun onTileExplore(tile: MapTile) {
        val result = game.gameEngine.mapSystem.revealTile(tile.id, game.gameEngine.gameState)
        if (result.success) {
            val tileActor = tileActors[Pair(tile.x, tile.y)]
            tileActor?.revealTile()
            updateAdjacentTiles(tile.x, tile.y)
            // NOTE: Do NOT manually add tile.goldReward here.
            // mapSystem.revealTile() already calls applyReward() which adds gold via state.addGold().
            // Adding it again here would duplicate the reward.
        } else {
            Gdx.app.log("MapScreen", "Exploration failed: ${result.message}")
        }
    }
 
    private fun updateAdjacentTiles(x: Int, y: Int) {
        val directions = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        val allTiles = game.gameEngine.gameState.mapTiles
        for ((dx, dy) in directions) {
            val adjX = x + dx
            val adjY = y + dy
            if (adjX in 0 until GRID_SIZE && adjY in 0 until GRID_SIZE) {
                // tiles are updated via isAdjacentToRevealed function - no field to set
            }
        }
    }
 
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.08f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
 
        if (backgroundTexture != null) {
            stage.batch.begin()
            stage.batch.draw(backgroundTexture, 0f, 0f, stage.viewport.worldWidth, stage.viewport.worldHeight)
            stage.batch.end()
        }
 
        stage.act(delta)
        stage.draw()
    }
 
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        centerMap()
    }
 
    override fun hide() {
        game.adsManager?.showBannerAd(false)
        currentPopup?.remove()
        currentPopup = null
    }
 
    override fun dispose() {
        stage.dispose()
        skin.dispose()
        for (tileActor in tileActors.values) tileActor.dispose()
    }
 
    private fun createColorDrawable(color: Color): com.badlogic.gdx.scenes.scene2d.utils.Drawable? {
        return try {
            val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
            pixmap.setColor(color)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()
            com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(texture)
        } catch (e: Exception) { null }
    }
 
    inner class MapGestureListener : GestureDetector.GestureAdapter() {
        private var isPanning = false
 
        override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
            isPanning = false
            panVelocity = 0f
            return false
        }
 
        override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
            val velocity = Math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()
            panVelocity = velocity
            if (velocity > PAN_VELOCITY_THRESHOLD || isPanning) {
                isPanning = true
                panOffsetX += deltaX
                panOffsetY -= deltaY
                updateMapPosition()
                return true
            }
            return false
        }
 
        override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
            isPanning = false
            panVelocity = 0f
            return false
        }
 
        override fun zoom(initialDistance: Float, distance: Float): Boolean {
            val ratio = initialDistance / distance
            zoomLevel = (zoomLevel * ratio).coerceIn(MIN_ZOOM, MAX_ZOOM)
            updateMapPosition()
            return true
        }
 
        override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean = true
    }
}
