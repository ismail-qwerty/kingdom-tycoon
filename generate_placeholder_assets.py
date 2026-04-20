# PATH: generate_placeholder_assets.py
"""
Generates placeholder PNG assets for Kingdom Tycoon game.
Creates solid-color 64x64 images for all buildings and 800x600 backgrounds.
Run this script before launching the game to create placeholder art.
"""

from PIL import Image, ImageDraw, ImageFont
import os

# Output directory
ASSETS_DIR = "android/assets"
TEXTURES_DIR = os.path.join(ASSETS_DIR, "textures")
BUILDINGS_DIR = os.path.join(TEXTURES_DIR, "buildings")
ERAS_DIR = os.path.join(TEXTURES_DIR, "eras")
HEROES_DIR = os.path.join(TEXTURES_DIR, "heroes")
EFFECTS_DIR = os.path.join(TEXTURES_DIR, "effects")
UI_DIR = os.path.join(TEXTURES_DIR, "ui")
FONTS_DIR = os.path.join(ASSETS_DIR, "fonts")

# Create directories
os.makedirs(BUILDINGS_DIR, exist_ok=True)
os.makedirs(ERAS_DIR, exist_ok=True)
os.makedirs(HEROES_DIR, exist_ok=True)
os.makedirs(EFFECTS_DIR, exist_ok=True)
os.makedirs(UI_DIR, exist_ok=True)
os.makedirs(FONTS_DIR, exist_ok=True)

# Era colors (RGB)
ERA_COLORS = {
    1: (139, 105, 20),   # Warm dirt brown (Forest Village)
    2: (128, 128, 128),  # Stone grey (Stone Town)
    3: (70, 70, 80),     # Dark iron grey (Iron Kingdom)
    4: (75, 0, 130),     # Deep purple (Mage Realm)
    5: (25, 25, 112),    # Midnight blue (Legendary Empire)
}

# Building names per era
BUILDINGS = {
    1: ["wheat_farm", "chicken_coop", "lumber_mill", "hunters_lodge", "blacksmith",
        "tavern", "market", "church", "town_hall", "castle"],
    2: ["quarry", "stone_mason", "bank", "library", "university",
        "cathedral", "merchant_guild", "courthouse", "palace", "monument"],
    3: ["iron_mine", "foundry", "armory", "barracks", "fortress",
        "war_factory", "siege_workshop", "military_academy", "citadel", "imperial_fortress"],
    4: ["mana_well", "arcane_library", "spell_forge", "wizard_tower", "enchantment_chamber",
        "alchemy_lab", "summoning_circle", "arcane_nexus", "mage_academy", "astral_spire"],
    5: ["cosmic_shrine", "divine_temple", "celestial_forge", "titan_workshop", "mythic_vault",
        "legendary_arena", "godforge", "pantheon", "world_tree", "throne_of_eternity"],
}

# Hero names
HEROES = ["merlin", "arthur", "guinevere", "lancelot", "morgana", "robin_hood",
          "joan_of_arc", "hercules", "cleopatra", "sun_tzu", "tesla", "davinci"]

def create_placeholder_image(width, height, color, text=""):
    """Creates a solid color image with optional text"""
    img = Image.new('RGB', (width, height), color=color)
    
    if text:
        draw = ImageDraw.Draw(img)
        # Use default font
        try:
            font = ImageFont.truetype("arial.ttf", 12)
        except:
            font = ImageFont.load_default()
        
        # Calculate text position (centered)
        bbox = draw.textbbox((0, 0), text, font=font)
        text_width = bbox[2] - bbox[0]
        text_height = bbox[3] - bbox[1]
        x = (width - text_width) // 2
        y = (height - text_height) // 2
        
        # Draw text with shadow
        draw.text((x+1, y+1), text, fill=(0, 0, 0), font=font)
        draw.text((x, y), text, fill=(255, 255, 255), font=font)
    
    return img

def generate_buildings():
    """Generates placeholder building sprites"""
    print("Generating building sprites...")
    
    for era, buildings in BUILDINGS.items():
        base_color = ERA_COLORS[era]
        
        for i, building in enumerate(buildings):
            # Vary color slightly for each building
            r = min(255, base_color[0] + i * 10)
            g = min(255, base_color[1] + i * 10)
            b = min(255, base_color[2] + i * 10)
            color = (r, g, b)
            
            filename = f"building_era{era}_{building}.png"
            filepath = os.path.join(BUILDINGS_DIR, filename)
            
            img = create_placeholder_image(64, 64, color, f"E{era}")
            img.save(filepath)
            print(f"  Created {filename}")

def generate_era_backgrounds():
    """Generates placeholder era backgrounds"""
    print("\nGenerating era backgrounds...")
    
    for era, color in ERA_COLORS.items():
        filename = f"era{era}_background.png"
        filepath = os.path.join(ERAS_DIR, filename)
        
        img = create_placeholder_image(800, 600, color, f"Era {era}")
        img.save(filepath)
        print(f"  Created {filename}")

def generate_heroes():
    """Generates placeholder hero portraits"""
    print("\nGenerating hero portraits...")
    
    for i, hero in enumerate(HEROES):
        # Use varied colors for heroes
        hue = (i * 30) % 360
        r = int(128 + 127 * (i % 3) / 2)
        g = int(128 + 127 * ((i + 1) % 3) / 2)
        b = int(128 + 127 * ((i + 2) % 3) / 2)
        color = (r, g, b)
        
        filename = f"hero_{hero}.png"
        filepath = os.path.join(HEROES_DIR, filename)
        
        img = create_placeholder_image(64, 64, color, hero[:4].upper())
        img.save(filepath)
        print(f"  Created {filename}")

def generate_effects():
    """Generates placeholder effect sprites"""
    print("\nGenerating effect sprites...")
    
    effects = [
        ("coin_particle.png", (255, 215, 0)),
        ("sparkle_particle.png", (255, 255, 255)),
        ("milestone_burst.png", (255, 100, 0)),
        ("critical_hit.png", (255, 0, 0)),
        ("level_up.png", (0, 255, 0)),
    ]
    
    for filename, color in effects:
        filepath = os.path.join(EFFECTS_DIR, filename)
        img = create_placeholder_image(32, 32, color)
        img.save(filepath)
        print(f"  Created {filename}")

def generate_ui():
    """Generates placeholder UI elements"""
    print("\nGenerating UI elements...")
    
    ui_elements = [
        ("hud_background.png", 800, 100, (50, 40, 30)),
        ("button_gold.png", 200, 50, (218, 165, 32)),
        ("button_grey.png", 200, 50, (128, 128, 128)),
        ("panel_background.png", 400, 300, (60, 50, 40)),
        ("coin_sprite.png", 32, 32, (255, 215, 0)),
        ("progress_bar_bg.png", 200, 20, (50, 50, 50)),
        ("progress_bar_fill.png", 200, 20, (0, 200, 0)),
    ]
    
    for filename, width, height, color in ui_elements:
        filepath = os.path.join(UI_DIR, filename)
        img = create_placeholder_image(width, height, color)
        img.save(filepath)
        print(f"  Created {filename}")

def create_texture_atlas():
    """Creates a simple texture atlas manifest"""
    print("\nCreating texture atlas manifest...")
    
    atlas_path = os.path.join(TEXTURES_DIR, "textures.atlas")
    
    with open(atlas_path, 'w') as f:
        f.write("# Placeholder texture atlas\n")
        f.write("# This is a minimal atlas file for placeholder assets\n")
        f.write("\ntextures.png\n")
        f.write("size: 1024,1024\n")
        f.write("format: RGBA8888\n")
        f.write("filter: Linear,Linear\n")
        f.write("repeat: none\n")
    
    print(f"  Created textures.atlas")

def create_placeholder_fonts():
    """Creates placeholder font files"""
    print("\nCreating placeholder font files...")
    
    fonts = [
        "font_gold_large.fnt",
        "font_gold_small.fnt",
        "font_body.fnt",
    ]
    
    for font in fonts:
        filepath = os.path.join(FONTS_DIR, font)
        with open(filepath, 'w') as f:
            f.write("# Placeholder font file\n")
            f.write("# Replace with actual BitmapFont .fnt file\n")
        print(f"  Created {font}")

def main():
    print("=" * 60)
    print("Kingdom Tycoon - Placeholder Asset Generator")
    print("=" * 60)
    print()
    
    generate_buildings()
    generate_era_backgrounds()
    generate_heroes()
    generate_effects()
    generate_ui()
    create_texture_atlas()
    create_placeholder_fonts()
    
    print()
    print("=" * 60)
    print("✓ All placeholder assets generated successfully!")
    print(f"✓ Total: 50 buildings + 5 backgrounds + 12 heroes + UI elements")
    print(f"✓ Assets saved to: {ASSETS_DIR}")
    print("=" * 60)

if __name__ == "__main__":
    main()
