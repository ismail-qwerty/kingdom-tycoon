// PATH: core/src/main/java/com/ismail/kingdom/factories/HeroFactory.kt
package com.ismail.kingdom.factories

import com.ismail.kingdom.models.Hero
import com.ismail.kingdom.models.HeroPassiveType

// Factory for creating all legendary heroes
object HeroFactory {
    
    // Creates all 12 legendary heroes
    fun buildAllHeroes(): List<Hero> {
        return listOf(
            Hero(
                id = "hero_merlin",
                name = "Merlin",
                title = "The Archmage",
                passiveType = HeroPassiveType.INCOME_MULTIPLIER,
                passiveValue = 2.0,
                description = "All income doubled",
                isUnlocked = false,
                portraitAsset = "heroes/merlin.png"
            ),
            Hero(
                id = "hero_arthur",
                name = "King Arthur",
                title = "The Once and Future King",
                passiveType = HeroPassiveType.TAP_MULTIPLIER,
                passiveValue = 5.0,
                description = "Tapping is 5x more powerful",
                isUnlocked = false,
                portraitAsset = "heroes/arthur.png"
            ),
            Hero(
                id = "hero_guinevere",
                name = "Guinevere",
                title = "The Fair Queen",
                passiveType = HeroPassiveType.QUEST_REWARDS,
                passiveValue = 3.0,
                description = "Quest rewards tripled",
                isUnlocked = false,
                portraitAsset = "heroes/guinevere.png"
            ),
            Hero(
                id = "hero_lancelot",
                name = "Lancelot",
                title = "The First Knight",
                passiveType = HeroPassiveType.OFFLINE_MULTIPLIER,
                passiveValue = 2.0,
                description = "Offline earnings doubled",
                isUnlocked = false,
                portraitAsset = "heroes/lancelot.png"
            ),
            Hero(
                id = "hero_morgana",
                name = "Morgana",
                title = "The Enchantress",
                passiveType = HeroPassiveType.ADVISOR_SPEED,
                passiveValue = 2.0,
                description = "Advisors work twice as fast",
                isUnlocked = false,
                portraitAsset = "heroes/morgana.png"
            ),
            Hero(
                id = "hero_robin",
                name = "Robin Hood",
                title = "The Outlaw",
                passiveType = HeroPassiveType.COST_REDUCTION,
                passiveValue = 0.75,
                description = "All buildings cost 25% less",
                isUnlocked = false,
                portraitAsset = "heroes/robin.png"
            ),
            Hero(
                id = "hero_joan",
                name = "Joan of Arc",
                title = "The Maid of Orleans",
                passiveType = HeroPassiveType.MILESTONE_BONUS,
                passiveValue = 3.0,
                description = "Milestone rewards tripled",
                isUnlocked = false,
                portraitAsset = "heroes/joan.png"
            ),
            Hero(
                id = "hero_hercules",
                name = "Hercules",
                title = "The Demigod",
                passiveType = HeroPassiveType.RESOURCE_MULTIPLIER,
                passiveValue = 2.5,
                description = "All resources x2.5",
                isUnlocked = false,
                portraitAsset = "heroes/hercules.png"
            ),
            Hero(
                id = "hero_cleopatra",
                name = "Cleopatra",
                title = "The Last Pharaoh",
                passiveType = HeroPassiveType.CROWN_SHARDS_BONUS,
                passiveValue = 2.0,
                description = "Earn 2x Crown Shards",
                isUnlocked = false,
                portraitAsset = "heroes/cleopatra.png"
            ),
            Hero(
                id = "hero_sun_tzu",
                name = "Sun Tzu",
                title = "The Art of War",
                passiveType = HeroPassiveType.EVENT_DURATION,
                passiveValue = 2.0,
                description = "Events last twice as long",
                isUnlocked = false,
                portraitAsset = "heroes/sun_tzu.png"
            ),
            Hero(
                id = "hero_tesla",
                name = "Tesla",
                title = "The Lightning Wizard",
                passiveType = HeroPassiveType.MAP_SPEED,
                passiveValue = 3.0,
                description = "Explore 3x faster",
                isUnlocked = false,
                portraitAsset = "heroes/tesla.png"
            ),
            Hero(
                id = "hero_da_vinci",
                name = "da Vinci",
                title = "The Renaissance Master",
                passiveType = HeroPassiveType.LEGEND_BUFF,
                passiveValue = 1.5,
                description = "All other heroes 50% stronger",
                isUnlocked = false,
                portraitAsset = "heroes/da_vinci.png"
            )
        )
    }
    
    // Gets a hero by ID
    fun getHero(id: String): Hero? {
        return buildAllHeroes().find { it.id == id }
    }
    
    // Gets all heroes of a specific passive type
    fun getHeroesByType(type: HeroPassiveType): List<Hero> {
        return buildAllHeroes().filter { it.passiveType == type }
    }
}
