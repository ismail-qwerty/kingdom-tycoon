#!/usr/bin/env python3
"""
Generate Play Store assets for Kingdom Tycoon
Requires: pip install pillow
"""

from PIL import Image, ImageDraw, ImageFont
import os

# Create output directory
OUTPUT_DIR = "store_assets"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def create_gradient(width, height, color1, color2):
    """Creates a vertical gradient image"""
    base = Image.new('RGB', (width, height), color1)
    top = Image.new('RGB', (width, height), color2)
    mask = Image.new('L', (width, height))
    mask_data = []
    for y in range(height):
        mask_data.extend([int(255 * (y / height))] * width)
    mask.putdata(mask_data)
    base.paste(top, (0, 0), mask)
    return base

def draw_text_centered(draw, text, font_size, y_position, width, height, color=(255, 255, 255)):
    """Draws centered text on an image"""
    try:
        font = ImageFont.truetype("arial.ttf", font_size)
    except:
        font = ImageFont.load_default()
    
    # Get text bounding box
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    
    x = (width - text_width) // 2
    y = y_position
    
    draw.text((x, y), text, fill=color, font=font)

def generate_feature_graphic():
    """Generates feature graphic (1024x500)"""
    print("Generating feature graphic (1024x500)...")
    
    width, height = 1024, 500
    
    # Create gradient background (gold to dark gold)
    img = create_gradient(width, height, (30, 20, 50), (100, 70, 30))
    draw = ImageDraw.Draw(img)
    
    # Draw title
    try:
        title_font = ImageFont.truetype("arial.ttf", 80)
        subtitle_font = ImageFont.truetype("arial.ttf", 40)
    except:
        title_font = ImageFont.load_default()
        subtitle_font = ImageFont.load_default()
    
    # Title
    title = "KINGDOM TYCOON"
    bbox = draw.textbbox((0, 0), title, font=title_font)
    title_width = bbox[2] - bbox[0]
    draw.text(((width - title_width) // 2, 150), title, fill=(255, 215, 0), font=title_font)
    
    # Subtitle
    subtitle = "Build Your Legendary Empire"
    bbox = draw.textbbox((0, 0), subtitle, font=subtitle_font)
    subtitle_width = bbox[2] - bbox[0]
    draw.text(((width - subtitle_width) // 2, 280), subtitle, fill=(255, 255, 255), font=subtitle_font)
    
    # Add decorative elements
    draw.rectangle([50, 400, 974, 450], fill=(255, 215, 0, 100))
    
    img.save(f"{OUTPUT_DIR}/feature_graphic.png")
    print("✓ Feature graphic saved")

def generate_icon():
    """Generates app icon (512x512)"""
    print("Generating app icon (512x512)...")
    
    size = 512
    
    # Create dark background with gradient
    img = create_gradient(size, size, (20, 10, 40), (60, 40, 80))
    draw = ImageDraw.Draw(img)
    
    # Draw crown shape (simplified)
    crown_color = (255, 215, 0)
    
    # Crown base
    draw.rectangle([100, 300, 412, 380], fill=crown_color)
    
    # Crown points
    points = [
        (150, 300), (150, 200), (180, 250),
        (256, 300), (256, 150), (286, 250),
        (362, 300), (362, 200), (332, 250)
    ]
    
    for i in range(0, len(points), 3):
        if i + 2 < len(points):
            draw.polygon([points[i], points[i+1], points[i+2]], fill=crown_color)
    
    # Add gems (circles)
    draw.ellipse([230, 320, 282, 372], fill=(200, 50, 50))
    draw.ellipse([140, 330, 170, 360], fill=(50, 150, 200))
    draw.ellipse([342, 330, 372, 360], fill=(50, 200, 100))
    
    img.save(f"{OUTPUT_DIR}/icon.png")
    print("✓ App icon saved")

def generate_screenshots():
    """Generates 8 screenshot templates (1080x1920)"""
    print("Generating screenshot templates (1080x1920)...")
    
    width, height = 1080, 1920
    
    screenshots = [
        ("Tap to Earn Gold", "Start your journey with simple taps", (50, 100, 150)),
        ("Build Your Kingdom", "Construct 25+ unique buildings", (100, 50, 150)),
        ("5 Epic Eras", "Transform your realm across ages", (150, 100, 50)),
        ("Prestige System", "Reset for permanent power boosts", (200, 150, 50)),
        ("Hire Advisors", "Automate your entire kingdom", (50, 150, 100)),
        ("Unlock Heroes", "12 legendary heroes with unique powers", (150, 50, 100)),
        ("Shadow Kingdom", "Double your income in the dark realm", (100, 50, 200)),
        ("Offline Earnings", "Earn gold even when you're away", (50, 200, 150))
    ]
    
    for i, (title, subtitle, color) in enumerate(screenshots, 1):
        # Create gradient background
        img = create_gradient(width, height, (20, 20, 30), color)
        draw = ImageDraw.Draw(img)
        
        # Draw title
        try:
            title_font = ImageFont.truetype("arial.ttf", 70)
            subtitle_font = ImageFont.truetype("arial.ttf", 45)
        except:
            title_font = ImageFont.load_default()
            subtitle_font = ImageFont.load_default()
        
        # Title
        bbox = draw.textbbox((0, 0), title, font=title_font)
        title_width = bbox[2] - bbox[0]
        draw.text(((width - title_width) // 2, 200), title, fill=(255, 255, 255), font=title_font)
        
        # Subtitle
        bbox = draw.textbbox((0, 0), subtitle, font=subtitle_font)
        subtitle_width = bbox[2] - bbox[0]
        draw.text(((width - subtitle_width) // 2, 320), subtitle, fill=(200, 200, 200), font=subtitle_font)
        
        # Placeholder for game screenshot
        draw.rectangle([90, 500, 990, 1500], fill=(40, 40, 50), outline=(255, 215, 0), width=5)
        
        # Add text in placeholder
        placeholder_text = "Game Screenshot Here"
        try:
            placeholder_font = ImageFont.truetype("arial.ttf", 50)
        except:
            placeholder_font = ImageFont.load_default()
        
        bbox = draw.textbbox((0, 0), placeholder_text, font=placeholder_font)
        text_width = bbox[2] - bbox[0]
        draw.text(((width - text_width) // 2, 950), placeholder_text, fill=(100, 100, 100), font=placeholder_font)
        
        # Footer
        footer_text = f"Screenshot {i}/8"
        try:
            footer_font = ImageFont.truetype("arial.ttf", 35)
        except:
            footer_font = ImageFont.load_default()
        
        bbox = draw.textbbox((0, 0), footer_text, font=footer_font)
        footer_width = bbox[2] - bbox[0]
        draw.text(((width - footer_width) // 2, 1750), footer_text, fill=(150, 150, 150), font=footer_font)
        
        img.save(f"{OUTPUT_DIR}/screenshot_{i}.png")
        print(f"✓ Screenshot {i}/8 saved: {title}")

def generate_promo_graphic():
    """Generates promo graphic (180x120) for Play Store"""
    print("Generating promo graphic (180x120)...")
    
    width, height = 180, 120
    
    # Create gradient background
    img = create_gradient(width, height, (30, 20, 50), (100, 70, 30))
    draw = ImageDraw.Draw(img)
    
    # Draw simplified crown
    crown_color = (255, 215, 0)
    draw.polygon([(60, 50), (60, 30), (70, 40), (90, 50), (90, 20), (100, 40), (120, 50), (120, 30), (110, 40)], fill=crown_color)
    draw.rectangle([60, 50, 120, 70], fill=crown_color)
    
    img.save(f"{OUTPUT_DIR}/promo_graphic.png")
    print("✓ Promo graphic saved")

def main():
    print("\n========== KINGDOM TYCOON ASSET GENERATOR ==========\n")
    print("Generating Play Store assets...\n")
    
    generate_feature_graphic()
    generate_icon()
    generate_screenshots()
    generate_promo_graphic()
    
    print("\n========== GENERATION COMPLETE ==========\n")
    print(f"All assets saved to: {OUTPUT_DIR}/")
    print("\nGenerated files:")
    print("  - feature_graphic.png (1024x500)")
    print("  - icon.png (512x512)")
    print("  - screenshot_1.png to screenshot_8.png (1080x1920)")
    print("  - promo_graphic.png (180x120)")
    print("\nNext steps:")
    print("  1. Replace placeholder screenshots with actual game screenshots")
    print("  2. Enhance graphics with professional design tools")
    print("  3. Upload to Google Play Console")
    print("\nNote: These are placeholder templates with correct dimensions.")
    print("      Professional graphics are recommended for production release.\n")

if __name__ == "__main__":
    main()
