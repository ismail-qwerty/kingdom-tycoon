// PATH: core/src/main/java/com/ismail/kingdom/utils/LoreStrings.kt
package com.ismail.kingdom.utils

import com.ismail.kingdom.models.TileType
import kotlin.random.Random

// Provides lore text for map tiles based on type and era
object LoreStrings {
    
    // Gets random lore text for a tile type and era
    fun getLoreText(type: TileType, eraId: Int): String {
        val loreList = when (type) {
            TileType.EMPTY -> getEmptyLore(eraId)
            TileType.RESOURCE_DEPOSIT -> getResourceDepositLore(eraId)
            TileType.ANCIENT_RUINS -> getAncientRuinsLore(eraId)
            TileType.ENEMY_CAMP -> getEnemyCampLore(eraId)
            TileType.MERCHANT -> getMerchantLore(eraId)
            TileType.QUEST_SITE -> getQuestSiteLore(eraId)
            TileType.LEGENDARY_SPOT -> getLegendarySpotLore(eraId)
        }
        
        return loreList.random()
    }
    
    // Empty tile lore texts
    private fun getEmptyLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "A peaceful meadow stretches before you. Birds sing in the distance.",
                "Rolling hills covered in wildflowers. The air smells of fresh grass.",
                "An open field perfect for future expansion. The soil is rich and fertile.",
                "A quiet grove of oak trees. Sunlight filters through the leaves.",
                "Gentle plains as far as the eye can see. A good place to rest."
            )
            2 -> listOf(
                "Quarried stone formations dot the landscape. Evidence of past mining.",
                "A rocky plateau with scattered boulders. The ground is solid here.",
                "Stone pathways wind through the area. Someone built roads here long ago.",
                "A cleared area surrounded by stone walls. Ready for construction.",
                "Flat bedrock exposed to the sky. A stable foundation awaits."
            )
            3 -> listOf(
                "Iron-rich soil stains the ground red. The smell of metal fills the air.",
                "Abandoned forges stand silent. The fires have long since died.",
                "Slag heaps mark old smelting sites. Industry once thrived here.",
                "A cleared industrial zone. Rails and gears lie scattered about.",
                "Open ground reinforced with iron beams. Built to last."
            )
            4 -> listOf(
                "Arcane energy crackles in the air. The veil between worlds is thin here.",
                "Floating crystals drift lazily overhead. Magic saturates this place.",
                "Ley lines converge beneath your feet. Power flows through the earth.",
                "A mystical clearing where reality bends. Anything seems possible.",
                "Ethereal mists swirl around you. The mundane world feels distant."
            )
            5 -> listOf(
                "Cosmic void stretches infinitely. Stars wheel overhead in impossible patterns.",
                "Reality fractures at the edges. You stand at the threshold of eternity.",
                "Celestial energies pulse through the fabric of space. Time has no meaning here.",
                "The universe itself seems to breathe. You are one with the cosmos.",
                "Legendary power resonates from the very air. This is where gods walk."
            )
            else -> listOf("An unremarkable patch of land.")
        }
    }
    
    // Resource deposit lore texts
    private fun getResourceDepositLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "A hidden cache of gold coins! Some traveler's lost fortune.",
                "Wild berry bushes heavy with fruit. Nature's bounty awaits harvest.",
                "A small vein of gold ore glints in the sunlight. Easy pickings!",
                "An abandoned merchant cart filled with goods. Finders keepers!",
                "A natural spring surrounded by valuable herbs and minerals."
            )
            2 -> listOf(
                "A rich deposit of limestone and marble. Perfect for grand buildings!",
                "Gemstones embedded in the rock face. A fortune in precious stones!",
                "An old treasury vault, still sealed. The lock is rusted but intact.",
                "Quarry workers left their tools and a chest of wages. Lucky find!",
                "A natural cave filled with crystalline formations worth a fortune."
            )
            3 -> listOf(
                "A massive iron ore deposit. Enough to forge a thousand swords!",
                "Abandoned war chests from an ancient battle. Gold and weapons within!",
                "A coal seam runs deep underground. Fuel for your industrial empire!",
                "Copper and tin veins intertwine. The makings of bronze and brass!",
                "A crashed supply wagon loaded with ingots. The driver is long gone."
            )
            4 -> listOf(
                "Mana crystals grow like flowers from the ground. Pure magical energy!",
                "A rift to the elemental planes leaks precious resources. Harvest carefully!",
                "Enchanted gold that multiplies when touched. Magic has its perks!",
                "A wizard's hidden stash of reagents and gold. They won't miss it.",
                "Solidified moonlight worth more than mundane gold. Arcane treasure!"
            )
            5 -> listOf(
                "Stardust coalesces into solid gold. The universe provides!",
                "A collapsed pocket dimension spills forth cosmic riches. Infinite wealth!",
                "Crystallized time itself. Each shard is worth kingdoms.",
                "The essence of creation made manifest. Reality bends to your will!",
                "A dying star's final gift. Resources beyond mortal comprehension!"
            )
            else -> listOf("Some resources lie here.")
        }
    }
    
    // Ancient ruins lore texts
    private fun getAncientRuinsLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "Crumbling stone circles from a forgotten age. Ancient wisdom lingers here.",
                "The foundation of a long-lost village. Their knowledge strengthens you.",
                "A weathered monument covered in mysterious runes. Power flows from it.",
                "Burial mounds of ancient kings. Their blessing empowers your realm.",
                "A sacred grove where druids once gathered. Nature's magic remains."
            )
            2 -> listOf(
                "The ruins of a great library. Scrolls of economic wisdom survive!",
                "An ancient bank vault, its mechanisms still functional. Prosperity awaits!",
                "Collapsed temples to the god of commerce. Divine favor increases your wealth!",
                "The throne room of a merchant prince. Their legacy empowers trade!",
                "A preserved counting house with ledgers of profit. Learn from the masters!"
            )
            3 -> listOf(
                "The skeleton of a massive war machine. Its design inspires efficiency!",
                "Ruins of the first factory. Industrial secrets lie within!",
                "An ancient engineer's workshop. Blueprints for prosperity remain!",
                "The control room of a great forge. Its power can still be harnessed!",
                "A monument to the age of steam. Progress echoes through time!"
            )
            4 -> listOf(
                "A shattered wizard's tower. Arcane formulas boost your income!",
                "The remains of a mage academy. Their teachings multiply your power!",
                "A ritual circle that still pulses with energy. Magic enhances your realm!",
                "The laboratory of an arch-mage. Their experiments benefit you!",
                "A portal to the astral plane, partially functional. Cosmic power flows through!"
            )
            5 -> listOf(
                "The ruins of a god's palace. Divine essence strengthens your kingdom!",
                "A shattered world-engine from the age of titans. Its power is yours!",
                "The throne of the first emperor of reality. Sit and claim its blessing!",
                "A monument to the architects of existence. Their wisdom is eternal!",
                "The birthplace of the universe itself. Creation energy empowers you!"
            )
            else -> listOf("Old ruins stand here.")
        }
    }
    
    // Enemy camp lore texts
    private fun getEnemyCampLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "A bandit camp! Defeat them and claim their stolen gold!",
                "Goblin raiders have set up here. Drive them out for a reward!",
                "A pack of wolves guards a treasure hoard. Fight for the prize!",
                "Brigands block the path forward. Clear them out!",
                "A rival lord's scouts. Defeat them to secure your borders!"
            )
            2 -> listOf(
                "Stone golems guard ancient treasure. Overcome them for riches!",
                "A mercenary company camps here. Pay them off or fight!",
                "Gargoyles nest in the rocks. Defeat them for their hoard!",
                "A rival guild's outpost. Drive them away and take their gold!",
                "Earth elementals block your expansion. Conquer them!"
            )
            3 -> listOf(
                "A war machine factory occupied by rebels. Reclaim it!",
                "Iron automatons patrol this sector. Disable them for salvage!",
                "A rival industrialist's forces. Defeat them in economic warfare!",
                "Clockwork soldiers guard a vault. Break through their defenses!",
                "A steam-powered fortress. Conquer it for massive rewards!"
            )
            4 -> listOf(
                "A coven of dark mages. Defeat them and claim their power!",
                "Summoned demons guard a magical treasure. Banish them!",
                "A rival wizard's sanctum. Magical duel for supremacy!",
                "Elemental lords block your path. Overcome their fury!",
                "A lich's phylactery vault. Destroy it for great rewards!"
            )
            5 -> listOf(
                "Cosmic horrors from beyond reality. Face them for legendary rewards!",
                "A fallen god's army. Defeat divinity itself!",
                "Reality warpers defend this sector. Overcome impossible odds!",
                "The void incarnate. Conquer nothingness for everything!",
                "Time itself opposes you. Win and claim eternity's treasure!"
            )
            else -> listOf("Enemies camp here.")
        }
    }
    
    // Merchant lore texts
    private fun getMerchantLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "A traveling merchant offers rare goods! New buildings available!",
                "A caravan from distant lands. They bring exotic construction plans!",
                "A peddler with a mysterious cart. What wonders does he sell?",
                "A merchant guild representative. They'll help expand your kingdom!",
                "A wandering trader with a map to new opportunities!"
            )
            2 -> listOf(
                "A stone mason's guild offers their services! New structures unlocked!",
                "Architects from the capital arrive. They bring grand designs!",
                "A master builder seeks employment. Accept their offer!",
                "A guild of craftsmen sets up shop. New buildings await!",
                "A renowned engineer offers their blueprints. Progress beckons!"
            )
            3 -> listOf(
                "An industrial magnate offers partnership! New factories available!",
                "A consortium of inventors arrives. Revolutionary designs await!",
                "A steam baron shares their secrets. New machinery unlocked!",
                "A guild of engineers offers their expertise. Industry expands!",
                "A master machinist with cutting-edge blueprints. The future is now!"
            )
            4 -> listOf(
                "A magical artificer offers their services! Arcane buildings unlocked!",
                "A wizard's guild representative. They bring mystical construction plans!",
                "An enchanter with impossible designs. Magic makes all things possible!",
                "A sorcerer's consortium offers partnership. New magical structures await!",
                "An arch-mage shares forbidden knowledge. Reality-bending buildings unlocked!"
            )
            5 -> listOf(
                "A cosmic entity offers divine architecture! Legendary buildings unlocked!",
                "The architects of reality arrive. They build with pure creation!",
                "A god of construction offers their blessing. Impossible structures await!",
                "The universe itself offers new possibilities. Transcendent buildings unlocked!",
                "A titan of industry from beyond time. They bring ultimate designs!"
            )
            else -> listOf("A merchant waits here.")
        }
    }
    
    // Quest site lore texts
    private fun getQuestSiteLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "A notice board with urgent requests. The people need your help!",
                "A messenger arrives with a royal decree. A quest awaits!",
                "A mysterious stranger offers a challenge. Adventure calls!",
                "A village elder seeks aid. Help them for great rewards!",
                "A treasure map leads here. Follow it to glory!"
            )
            2 -> listOf(
                "A guild master posts a lucrative contract. Big rewards await!",
                "An ancient prophecy points to this location. Fulfill your destiny!",
                "A stone tablet describes a legendary challenge. Are you worthy?",
                "A master craftsman needs rare materials. Help them for rewards!",
                "A monument marks the start of a grand quest. Begin your journey!"
            )
            3 -> listOf(
                "A factory owner needs help with a crisis. Industrial quest available!",
                "A blueprint for a legendary machine. Gather the components!",
                "An inventor's final challenge. Complete their life's work!",
                "A steam baron's dying wish. Honor their legacy!",
                "A mechanical puzzle awaits solving. Crack it for rewards!"
            )
            4 -> listOf(
                "A magical anomaly requires investigation. Arcane quest begins!",
                "A wizard's final test. Prove your magical prowess!",
                "A rift to another dimension. Explore it for rewards!",
                "An ancient spell requires completion. Finish the ritual!",
                "A mage's tower holds a challenge. Ascend to the top!"
            )
            5 -> listOf(
                "A cosmic trial awaits. Prove yourself to the universe!",
                "A god's challenge. Complete it for divine rewards!",
                "The fabric of reality needs mending. Save existence itself!",
                "A titan's final request. Help them pass into legend!",
                "The ultimate quest begins here. Transcend mortality!"
            )
            else -> listOf("A quest awaits here.")
        }
    }
    
    // Legendary spot lore texts
    private fun getLegendarySpotLore(eraId: Int): List<String> {
        return when (eraId) {
            1 -> listOf(
                "The birthplace of your kingdom! Sacred ground blessed by fate itself!",
                "An ancient tree of legend. Its roots connect to the world's heart!",
                "The site where the first king was crowned. Power resonates here!",
                "A natural wonder of incredible beauty. The gods smile upon this place!",
                "The convergence of three rivers. A place of destiny and fortune!"
            )
            2 -> listOf(
                "The legendary Stone of Prosperity! Touch it for eternal wealth!",
                "The vault of the first merchant king. Unimaginable riches within!",
                "A monument to the golden age. Its power still flows!",
                "The cornerstone of civilization. All roads lead from here!",
                "The throne of the merchant emperor. Sit and claim their power!"
            )
            3 -> listOf(
                "The first factory ever built! The birthplace of industry!",
                "The legendary Engine of Progress. It still runs after centuries!",
                "The workshop of the greatest inventor. Their genius empowers you!",
                "The heart of the industrial revolution. Power beyond measure!",
                "The monument to human achievement. Stand in awe of progress!"
            )
            4 -> listOf(
                "The Nexus of All Magic! The source of arcane power!",
                "The tower of the first arch-mage. Infinite knowledge within!",
                "A tear in reality leading to pure magic. Harness its power!",
                "The birthplace of the first spell. Creation magic flows here!",
                "The throne of the Mage Emperor. Claim ultimate magical authority!"
            )
            5 -> listOf(
                "The Origin Point! Where the universe began! All power flows from here!",
                "The Throne of Eternity! Sit and command reality itself!",
                "The Heart of Creation! The source of all existence!",
                "The Palace of the First God! Divine power beyond comprehension!",
                "The End and Beginning! Alpha and Omega converge here!"
            )
            else -> listOf("A legendary location.")
        }
    }
}
