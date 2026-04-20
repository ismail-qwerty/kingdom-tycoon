# PATH: generate_placeholder_audio.py
"""
Generates placeholder audio files (500ms silent OGG) for Kingdom Tycoon game.

Requirements:
    pip install pydub

Note: pydub requires ffmpeg to be installed on your system.
    - Windows: Download from https://ffmpeg.org/download.html
    - Mac: brew install ffmpeg
    - Linux: sudo apt-get install ffmpeg
"""

import os
from pydub import AudioSegment
from pydub.generators import Sine

def create_silent_audio(duration_ms=500):
    """Creates a silent audio segment of specified duration."""
    # Create a very quiet sine wave (effectively silent)
    silent = Sine(20).to_audio_segment(duration=duration_ms, volume=-60)
    return silent

def create_audio_directories():
    """Creates necessary audio directories."""
    base_path = "android/assets/audio"
    
    directories = [
        f"{base_path}/music",
        f"{base_path}/sfx"
    ]
    
    for directory in directories:
        os.makedirs(directory, exist_ok=True)
        print(f"Created directory: {directory}")

def generate_music_tracks():
    """Generates placeholder music tracks for all 5 eras."""
    base_path = "android/assets/audio/music"
    
    music_files = [
        ("era1_theme.ogg", "Era 1 - Calm medieval folk loop"),
        ("era2_theme.ogg", "Era 2 - Grander stone town theme"),
        ("era3_theme.ogg", "Era 3 - Epic military march"),
        ("era4_theme.ogg", "Era 4 - Mystical magical ambient"),
        ("era5_theme.ogg", "Era 5 - Legendary orchestral swell")
    ]
    
    print("\nGenerating music tracks...")
    for filename, description in music_files:
        filepath = os.path.join(base_path, filename)
        
        # Create 2 second silent audio for music (longer than SFX)
        audio = create_silent_audio(duration_ms=2000)
        
        # Export as OGG
        audio.export(filepath, format="ogg")
        print(f"  ✓ {filename} - {description}")
    
    print(f"Generated {len(music_files)} music tracks")

def generate_sound_effects():
    """Generates placeholder sound effects."""
    base_path = "android/assets/audio/sfx"
    
    sound_files = [
        ("sfx_tap.ogg", "Coin clink on tap", 200),
        ("sfx_tap_combo.ogg", "Higher-pitched combo clink", 200),
        ("sfx_purchase.ogg", "Ka-ching register sound", 500),
        ("sfx_milestone.ogg", "Triumphant fanfare", 1000),
        ("sfx_prestige.ogg", "Epic orchestral hit", 800),
        ("sfx_advisor_hired.ogg", "Approval chime", 400),
        ("sfx_quest_complete.ogg", "Success fanfare", 600),
        ("sfx_event_start.ogg", "Dramatic announcement", 700),
        ("sfx_ui_click.ogg", "Soft button click", 100),
        ("sfx_map_reveal.ogg", "Discovery sound", 500)
    ]
    
    print("\nGenerating sound effects...")
    for filename, description, duration_ms in sound_files:
        filepath = os.path.join(base_path, filename)
        
        # Create silent audio with specified duration
        audio = create_silent_audio(duration_ms=duration_ms)
        
        # Export as OGG
        audio.export(filepath, format="ogg")
        print(f"  ✓ {filename} - {description} ({duration_ms}ms)")
    
    print(f"Generated {len(sound_files)} sound effects")

def generate_readme():
    """Generates README file explaining placeholder audio."""
    readme_content = """# Placeholder Audio Files

These are silent placeholder audio files for Kingdom Tycoon game development.

## Music Tracks (2 seconds each)
- era1_theme.ogg - Calm medieval folk loop
- era2_theme.ogg - Grander stone town theme
- era3_theme.ogg - Epic military march
- era4_theme.ogg - Mystical magical ambient
- era5_theme.ogg - Legendary orchestral swell

## Sound Effects (varying durations)
- sfx_tap.ogg (200ms) - Coin clink on tap
- sfx_tap_combo.ogg (200ms) - Higher-pitched combo clink
- sfx_purchase.ogg (500ms) - Ka-ching register sound
- sfx_milestone.ogg (1000ms) - Triumphant fanfare
- sfx_prestige.ogg (800ms) - Epic orchestral hit
- sfx_advisor_hired.ogg (400ms) - Approval chime
- sfx_quest_complete.ogg (600ms) - Success fanfare
- sfx_event_start.ogg (700ms) - Dramatic announcement
- sfx_ui_click.ogg (100ms) - Soft button click
- sfx_map_reveal.ogg (500ms) - Discovery sound

## Replacing Placeholders

To replace these with real audio:
1. Create/obtain audio files in OGG format
2. Match the filenames exactly
3. Place in the same directories
4. Recommended durations are listed above
5. Keep file sizes reasonable (< 1MB for SFX, < 5MB for music)

## Audio Guidelines

### Music Tracks
- Format: OGG Vorbis
- Sample Rate: 44100 Hz
- Bitrate: 128 kbps (good quality/size balance)
- Loop: Should loop seamlessly
- Volume: Normalized to -3dB peak

### Sound Effects
- Format: OGG Vorbis
- Sample Rate: 44100 Hz
- Bitrate: 96 kbps (sufficient for SFX)
- Duration: Keep short (< 2 seconds)
- Volume: Normalized to -6dB peak (allows headroom)

## Testing

Test audio in-game:
1. Enable sound/music in settings
2. Verify volume controls work
3. Check crossfade between era music
4. Test combo pitch scaling on tap sounds
5. Verify pause/resume on app backgrounding

Generated by: generate_placeholder_audio.py
"""
    
    readme_path = "android/assets/audio/README.md"
    with open(readme_path, 'w') as f:
        f.write(readme_content)
    
    print(f"\n✓ Generated README: {readme_path}")

def main():
    """Main function to generate all placeholder audio files."""
    print("=" * 60)
    print("Kingdom Tycoon - Placeholder Audio Generator")
    print("=" * 60)
    
    try:
        # Create directories
        create_audio_directories()
        
        # Generate music tracks
        generate_music_tracks()
        
        # Generate sound effects
        generate_sound_effects()
        
        # Generate README
        generate_readme()
        
        print("\n" + "=" * 60)
        print("✓ All placeholder audio files generated successfully!")
        print("=" * 60)
        print("\nNext steps:")
        print("1. Replace placeholders with real audio files")
        print("2. Test audio in-game")
        print("3. Adjust volumes in AudioSystem.kt if needed")
        print("\nNote: These are silent files for development only.")
        
    except Exception as e:
        print(f"\n✗ Error generating audio files: {e}")
        print("\nMake sure you have:")
        print("  1. Installed pydub: pip install pydub")
        print("  2. Installed ffmpeg on your system")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())
