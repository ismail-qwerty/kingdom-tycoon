// PATH: core/src/main/java/com/ismail/kingdom/assets/GameAssets.kt
package com.ismail.kingdom.assets
 
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
 
// Singleton wrapper for LibGDX AssetManager
object GameAssets {
    
    private val assetManager = AssetManager()
    private var textureAtlas: TextureAtlas? = null
    
    // Loads all game assets
    fun load() {
        // Load texture atlas (optional - may not exist yet)
        try {
            assetManager.load(AssetDescriptors.TEXTURE_ATLAS, TextureAtlas::class.java)
        } catch (e: Exception) {
            com.badlogic.gdx.Gdx.app.log("GameAssets", "Texture atlas not found, skipping")
        }
        
        // Load UI textures
        loadAssetSafe(AssetDescriptors.UI.HUD_BACKGROUND, Texture::class.java)
        loadAssetSafe(AssetDescriptors.UI.BUTTON_GOLD, Texture::class.java)
        loadAssetSafe(AssetDescriptors.UI.BUTTON_GREY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.UI.PANEL_BACKGROUND, Texture::class.java)
        loadAssetSafe(AssetDescriptors.UI.COIN_SPRITE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.UI.PROGRESS_BAR_BG, Texture::class.java)
        loadAssetSafe(AssetDescriptors.UI.PROGRESS_BAR_FILL, Texture::class.java)
        
        // Load era backgrounds
        loadAssetSafe(AssetDescriptors.Eras.ERA1_BACKGROUND, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Eras.ERA2_BACKGROUND, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Eras.ERA3_BACKGROUND, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Eras.ERA4_BACKGROUND, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Eras.ERA5_BACKGROUND, Texture::class.java)
        
        // Load building textures (50 buildings)
        loadBuildingTextures()
        
        // Load hero textures (12 heroes)
        loadHeroTextures()
        
        // Load effect textures
        loadAssetSafe(AssetDescriptors.Effects.COIN_PARTICLE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Effects.SPARKLE_PARTICLE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Effects.MILESTONE_BURST, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Effects.CRITICAL_HIT, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Effects.LEVEL_UP, Texture::class.java)
        
        // Load fonts (optional - may not exist yet)
        loadAssetSafe(AssetDescriptors.Fonts.GOLD_LARGE, BitmapFont::class.java)
        loadAssetSafe(AssetDescriptors.Fonts.GOLD_SMALL, BitmapFont::class.java)
        loadAssetSafe(AssetDescriptors.Fonts.BODY, BitmapFont::class.java)
    }
    
    // Safely loads an asset, catching errors
    private fun <T> loadAssetSafe(path: String, type: Class<T>) {
        try {
            if (com.badlogic.gdx.Gdx.files.internal(path).exists()) {
                assetManager.load(path, type)
            }
        } catch (e: Exception) {
            com.badlogic.gdx.Gdx.app.log("GameAssets", "Failed to load $path: ${e.message}")
        }
    }
    
    // Loads all building textures
    private fun loadBuildingTextures() {
        // Era 1
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_WHEAT_FARM, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_CHICKEN_COOP, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_LUMBER_MILL, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_HUNTERS_LODGE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_BLACKSMITH, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_TAVERN, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_MARKET, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_CHURCH, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_TOWN_HALL, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA1_CASTLE, Texture::class.java)
        
        // Era 2
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_QUARRY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_STONE_MASON, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_BANK, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_LIBRARY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_UNIVERSITY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_CATHEDRAL, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_MERCHANT_GUILD, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_COURTHOUSE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_PALACE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA2_MONUMENT, Texture::class.java)
        
        // Era 3
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_IRON_MINE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_FOUNDRY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_ARMORY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_BARRACKS, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_FORTRESS, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_WAR_FACTORY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_SIEGE_WORKSHOP, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_MILITARY_ACADEMY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_CITADEL, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA3_IMPERIAL_FORTRESS, Texture::class.java)
        
        // Era 4
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_MANA_WELL, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_ARCANE_LIBRARY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_SPELL_FORGE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_WIZARD_TOWER, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_ENCHANTMENT_CHAMBER, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_ALCHEMY_LAB, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_SUMMONING_CIRCLE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_ARCANE_NEXUS, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_MAGE_ACADEMY, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA4_ASTRAL_SPIRE, Texture::class.java)
        
        // Era 5
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_COSMIC_SHRINE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_DIVINE_TEMPLE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_CELESTIAL_FORGE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_TITAN_WORKSHOP, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_MYTHIC_VAULT, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_LEGENDARY_ARENA, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_GODFORGE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_PANTHEON, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_WORLD_TREE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Buildings.ERA5_THRONE_OF_ETERNITY, Texture::class.java)
    }
    
    // Loads all hero textures
    private fun loadHeroTextures() {
        loadAssetSafe(AssetDescriptors.Heroes.MERLIN, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.KING_ARTHUR, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.GUINEVERE, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.LANCELOT, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.MORGANA, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.ROBIN_HOOD, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.JOAN_OF_ARC, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.HERCULES, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.CLEOPATRA, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.SUN_TZU, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.TESLA, Texture::class.java)
        loadAssetSafe(AssetDescriptors.Heroes.DA_VINCI, Texture::class.java)
    }
    
    // Updates asset loading (call every frame during loading screen)
    fun update(): Boolean {
        return assetManager.update()
    }
    
    // Checks if all assets are loaded
    fun isLoaded(): Boolean {
        return assetManager.isFinished
    }
    
    // Gets loading progress (0.0 to 1.0)
    fun getProgress(): Float {
        return assetManager.progress
    }
    
    // Gets a texture by path
    fun getTexture(path: String): Texture {
        return if (assetManager.isLoaded(path)) {
            assetManager.get(path, Texture::class.java)
        } else {
            getPlaceholderTexture(com.badlogic.gdx.graphics.Color.DARK_GRAY)
        }
    }
    
    // Gets a texture region from atlas; returns a 1x1 placeholder region if atlas is unavailable
    fun getTextureRegion(name: String): TextureRegion {
        if (textureAtlas == null && assetManager.isLoaded(AssetDescriptors.TEXTURE_ATLAS)) {
            textureAtlas = assetManager.get(AssetDescriptors.TEXTURE_ATLAS, TextureAtlas::class.java)
        }
        val atlas = textureAtlas ?: return TextureRegion(getPlaceholderTexture(com.badlogic.gdx.graphics.Color.DARK_GRAY))
        return atlas.findRegion(name) ?: TextureRegion(getPlaceholderTexture(com.badlogic.gdx.graphics.Color.MAGENTA))
    }
    
    // Gets a bitmap font
    fun getBitmapFont(path: String): BitmapFont {
        return assetManager.get(path, BitmapFont::class.java)
    }
 
    // Gets era background texture by era ID
    fun getEraBackground(eraId: Int): Texture {
        val path = when (eraId) {
            1 -> AssetDescriptors.Eras.ERA1_BACKGROUND
            2 -> AssetDescriptors.Eras.ERA2_BACKGROUND
            3 -> AssetDescriptors.Eras.ERA3_BACKGROUND
            4 -> AssetDescriptors.Eras.ERA4_BACKGROUND
            5 -> AssetDescriptors.Eras.ERA5_BACKGROUND
            else -> AssetDescriptors.Eras.ERA1_BACKGROUND
        }
        return if (assetManager.isLoaded(path)) assetManager.get(path, Texture::class.java)
        else getPlaceholderTexture(com.badlogic.gdx.graphics.Color.DARK_GRAY)
    }
 
    // Gets building texture by building ID
    fun getBuildingTexture(buildingId: String): Texture = getPlaceholderTexture(com.badlogic.gdx.graphics.Color.BROWN)
 
    // Gets advisor portrait by advisor ID
    fun getAdvisorPortrait(advisorId: String): Texture = getPlaceholderTexture(com.badlogic.gdx.graphics.Color.BLUE)
 
    // Gets hero portrait by hero ID
    fun getHeroPortrait(heroId: String): Texture = getPlaceholderTexture(com.badlogic.gdx.graphics.Color.GOLD)
 
    // Creates placeholder texture with specified color
    private fun getPlaceholderTexture(color: com.badlogic.gdx.graphics.Color): Texture {
        val pm = com.badlogic.gdx.graphics.Pixmap(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pm.setColor(color)
        pm.fill()
        val tex = Texture(pm)
        pm.dispose()
        return tex
    }
    
    // Disposes all assets
    fun dispose() {
        assetManager.dispose()
        textureAtlas = null
    }
}
