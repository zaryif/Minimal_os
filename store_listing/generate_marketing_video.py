import os
import sys
import subprocess
import json
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

BASE_DIR = "/Users/zaryif/Minimal_os"
OUTPUT_DIR = os.path.join(BASE_DIR, "store_listing", "video")
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "store_listing", "screenshots_real")
LOGO_PATH = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", "app_logo.png")
DESKTOP_DIR = "/Users/zaryif/Desktop/Minimal_OS_Store_Listing/video"

os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(DESKTOP_DIR, exist_ok=True)
TEMP_DIR = os.path.join(OUTPUT_DIR, "temp_render")
os.makedirs(TEMP_DIR, exist_ok=True)

# Audio script scenes
SCENES = [
    {
        "id": "scene1",
        "narration": "Modern smartphones were engineered to hijack your attention with endless icons, notification badges, and noise.",
        "subtitle": "Modern smartphones were engineered to hijack your attention with endless icons and noise.",
        "title": "DIGITAL NOISE",
        "headline": "reclaim your focus.",
    },
    {
        "id": "scene2",
        "narration": "Meet Minimal OS. An intentional, text-first Android launcher designed for pure focus and clarity.",
        "subtitle": "Meet Minimal OS. An intentional, text-first Android launcher designed for pure focus.",
        "title": "MINIMAL OS",
        "headline": "intentional.\nfocused.\nminimal.",
        "screenshot": "01_onboarding_welcome_apple.png"
    },
    {
        "id": "scene3",
        "narration": "Control your phone with a fast unified command line. Send messages, capture to-dos, and save notes in seconds.",
        "subtitle": "Control your phone with a fast command line. Text, capture tasks, and save notes in seconds.",
        "title": "THE COMMAND BAR",
        "headline": "type to do things.",
        "screenshot": "02_onboarding_command_bar_apple.png"
    },
    {
        "id": "scene4",
        "narration": "Stay in your flow with live Pomodoro focus countdowns, a modular widgets deck, and one calm notification center.",
        "subtitle": "Live Pomodoro countdowns, an uncluttered widgets deck, and one calm notification hub.",
        "title": "CARDS & WIDGETS",
        "headline": "built for deep work.",
        "screenshot": "03_onboarding_widgets_apple.png"
    },
    {
        "id": "scene5",
        "narration": "100 percent offline. Zero telemetry. Zero ads. Simplify your screen and reclaim your time with Minimal OS.",
        "subtitle": "100% offline. Zero ads. Zero tracking. Reclaim your time with Minimal OS.",
        "title": "PRIVACY BY DESIGN",
        "headline": "available on google play.",
        "screenshot": "05_onboarding_setup_apple.png"
    }
]

def generate_voiceovers():
    print("Generating voice-over audio files...")
    audio_files = []
    for scene in SCENES:
        aiff_path = os.path.join(TEMP_DIR, f"{scene['id']}.aiff")
        wav_path = os.path.join(TEMP_DIR, f"{scene['id']}.wav")
        # Generate with macOS say tool (Daniel voice)
        subprocess.run(["say", "-v", "Daniel", "-r", "170", scene["narration"], "-o", aiff_path], check=True)
        # Convert to standard WAV
        subprocess.run(["ffmpeg", "-y", "-i", aiff_path, "-ar", "44100", "-ac", "2", wav_path], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
        
        # Get duration
        probe = subprocess.run([
            "ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "json", wav_path
        ], capture_output=True, text=True, check=True)
        duration = float(json.loads(probe.stdout)["format"]["duration"])
        scene["audio_wav"] = wav_path
        # Add 0.8s padding between scenes for natural breathing space
        scene["duration"] = duration + 0.8
        print(f"Scene {scene['id']}: duration={scene['duration']:.2f}s")
        audio_files.append(wav_path)
    return SCENES

def get_font(size, bold=False, mono=False):
    if mono:
        paths = ["/System/Library/Fonts/SFNSMono.ttf", "/System/Library/Fonts/Menlo.ttc", "/Library/Fonts/Courier New.ttf"]
    elif bold:
        paths = ["/System/Library/Fonts/SFNS.ttf", "/System/Library/Fonts/HelveticaNeue.ttc", "/System/Library/Fonts/Supplemental/Arial Bold.ttf"]
    else:
        paths = ["/System/Library/Fonts/SFNS.ttf", "/System/Library/Fonts/HelveticaNeue.ttc", "/System/Library/Fonts/Supplemental/Arial.ttf"]
    
    for p in paths:
        if os.path.exists(p):
            try:
                return ImageFont.truetype(p, size)
            except Exception:
                pass
    return ImageFont.load_default()

def render_frame_vertical(scene, local_time, width=1080, height=1920):
    img = Image.new("RGBA", (width, height), (8, 8, 8, 255))
    draw = ImageDraw.Draw(img)
    
    # Elegant dark gradient overlay
    for y in range(height):
        alpha = int(25 * (y / height))
        draw.line([(0, y), (width, y)], fill=(20, 15, 18, alpha))
    
    # Glowing top accent line
    draw.line([(80, 100), (220, 100)], fill=(229, 57, 53, 255), width=3)
    
    # Tag
    font_tag = get_font(28, bold=True, mono=True)
    draw.text((80, 120), scene["title"], font=font_tag, fill=(229, 57, 53, 255))
    
    # Headline
    font_head = get_font(68, bold=True)
    draw.text((80, 175), scene["headline"], font=font_head, fill=(245, 245, 245, 255))
    
    # Device Mockup / Center Visual
    if "screenshot" in scene:
        ss_path = os.path.join(SCREENSHOTS_DIR, scene["screenshot"])
        if os.path.exists(ss_path):
            ss_img = Image.open(ss_path).convert("RGBA")
            # Calculate mockup sizing
            mock_w = 640
            mock_h = int(mock_w * (2400 / 1080))
            if mock_h > 1050:
                mock_h = 1050
                mock_w = int(mock_h * (1080 / 2400))
            
            ss_resized = ss_img.resize((mock_w, mock_h), Image.Resampling.LANCZOS)
            
            # Draw phone frame border & shadow
            mock_x = (width - mock_w) // 2
            mock_y = 380
            
            # Subtle shadow
            draw.rounded_rectangle(
                [mock_x - 6, mock_y - 6, mock_x + mock_w + 6, mock_y + mock_h + 6],
                radius=32,
                fill=(20, 20, 20, 255),
                outline=(50, 50, 50, 255),
                width=2
            )
            
            # Mask rounded corners on screenshot
            mask = Image.new("L", (mock_w, mock_h), 0)
            mask_draw = ImageDraw.Draw(mask)
            mask_draw.rounded_rectangle([0, 0, mock_w, mock_h], radius=28, fill=255)
            img.paste(ss_resized, (mock_x, mock_y), mask)
            
            # Outer phone border
            draw.rounded_rectangle(
                [mock_x, mock_y, mock_x + mock_w, mock_y + mock_h],
                radius=28,
                outline=(229, 57, 53, 180),
                width=2
            )
    else:
        # Scene 1: Chaos illustration
        center_y = 700
        font_big = get_font(84, bold=True)
        draw.text((width // 2, center_y - 80), "99+ NOTIFICATIONS", font=font_big, fill=(229, 57, 53, 230), anchor="mm")
        
        badges = [
            "Endless App Grid", "Dopamine Traps", "Badge Anxiety",
            "Privacy Tracking", "Algorithmic Feeds", "Lost Time"
        ]
        font_badge = get_font(32, bold=True, mono=True)
        for i, b in enumerate(badges):
            bx = 140 if i % 2 == 0 else 560
            by = center_y + 40 + (i // 2) * 90
            draw.rounded_rectangle([bx, by, bx + 380, by + 64], radius=16, fill=(24, 20, 22, 255), outline=(60, 40, 45, 255), width=2)
            draw.text((bx + 190, by + 32), b, font=font_badge, fill=(180, 180, 180, 255), anchor="mm")

    # Subtitle Box at Bottom
    sub_box_y = 1580
    draw.rounded_rectangle([60, sub_box_y, width - 60, sub_box_y + 180], radius=20, fill=(18, 18, 18, 240), outline=(40, 40, 40, 255), width=2)
    
    font_sub = get_font(36, bold=False)
    # Wrap subtitle
    words = scene["subtitle"].split()
    lines = []
    curr = []
    for w in words:
        curr.append(w)
        if len(" ".join(curr)) > 42:
            lines.append(" ".join(curr[:-1]))
            curr = [w]
    if curr:
        lines.append(" ".join(curr))
    
    for i, line in enumerate(lines[:3]):
        draw.text((width // 2, sub_box_y + 45 + i * 46), line, font=font_sub, fill=(240, 240, 240, 255), anchor="mm")
        
    # Branding badge
    font_brand = get_font(24, bold=True, mono=True)
    draw.text((width // 2, 1820), "MINIMAL OS  |  PLAY STORE", font=font_brand, fill=(140, 140, 140, 255), anchor="mm")
    
    return img

def render_frame_landscape(scene, local_time, width=1920, height=1080):
    img = Image.new("RGBA", (width, height), (8, 8, 8, 255))
    draw = ImageDraw.Draw(img)
    
    # Left column: Typography and Subtitles (x: 100 to 850)
    # Right column: Device Mockup (x: 950 to 1800)
    
    # Tag
    font_tag = get_font(26, bold=True, mono=True)
    draw.text((120, 140), scene["title"], font=font_tag, fill=(229, 57, 53, 255))
    
    # Headline
    font_head = get_font(64, bold=True)
    draw.text((120, 200), scene["headline"], font=font_head, fill=(245, 245, 245, 255))
    
    # Subtitle Card on Left
    sub_box_y = 480
    draw.rounded_rectangle([120, sub_box_y, 820, sub_box_y + 240], radius=20, fill=(18, 18, 18, 240), outline=(40, 40, 40, 255), width=2)
    
    font_sub = get_font(34, bold=False)
    words = scene["subtitle"].split()
    lines = []
    curr = []
    for w in words:
        curr.append(w)
        if len(" ".join(curr)) > 36:
            lines.append(" ".join(curr[:-1]))
            curr = [w]
    if curr:
        lines.append(" ".join(curr))
    
    for i, line in enumerate(lines[:4]):
        draw.text((470, sub_box_y + 50 + i * 46), line, font=font_sub, fill=(240, 240, 240, 255), anchor="mm")
    
    font_brand = get_font(24, bold=True, mono=True)
    draw.text((120, 800), "MINIMAL OS  |  OFFLINE & ZERO TELEMETRY", font=font_brand, fill=(140, 140, 140, 255))
    
    # Right column visual
    if "screenshot" in scene:
        ss_path = os.path.join(SCREENSHOTS_DIR, scene["screenshot"])
        if os.path.exists(ss_path):
            ss_img = Image.open(ss_path).convert("RGBA")
            mock_h = 860
            mock_w = int(mock_h * (1080 / 2400))
            ss_resized = ss_img.resize((mock_w, mock_h), Image.Resampling.LANCZOS)
            
            mock_x = 1150
            mock_y = 110
            
            draw.rounded_rectangle(
                [mock_x - 6, mock_y - 6, mock_x + mock_w + 6, mock_y + mock_h + 6],
                radius=32,
                fill=(20, 20, 20, 255),
                outline=(50, 50, 50, 255),
                width=2
            )
            
            mask = Image.new("L", (mock_w, mock_h), 0)
            mask_draw = ImageDraw.Draw(mask)
            mask_draw.rounded_rectangle([0, 0, mock_w, mock_h], radius=28, fill=255)
            img.paste(ss_resized, (mock_x, mock_y), mask)
            
            draw.rounded_rectangle(
                [mock_x, mock_y, mock_x + mock_w, mock_y + mock_h],
                radius=28,
                outline=(229, 57, 53, 180),
                width=2
            )
    else:
        # Chaos Badges on Right
        center_x = 1380
        center_y = 480
        font_big = get_font(52, bold=True)
        draw.text((center_x, center_y - 140), "THE NOTIFICATION TRAP", font=font_big, fill=(229, 57, 53, 230), anchor="mm")
        
        badges = [
            "Icon Red Dot Badges", "Endless News Feeds",
            "Dopamine Algorithms", "App Clutter & Chaos",
            "Background Tracking", "Lost Attention"
        ]
        font_badge = get_font(26, bold=True, mono=True)
        for i, b in enumerate(badges):
            bx = center_x - 220
            by = center_y - 40 + i * 70
            draw.rounded_rectangle([bx, by, bx + 440, by + 54], radius=12, fill=(24, 20, 22, 255), outline=(60, 40, 45, 255), width=2)
            draw.text((center_x, by + 27), b, font=font_badge, fill=(180, 180, 180, 255), anchor="mm")
            
    return img

def render_scenes_to_video(scenes, orientation="vertical"):
    width, height = (1080, 1920) if orientation == "vertical" else (1920, 1080)
    print(f"Rendering {orientation} video ({width}x{height})...")
    
    fps = 30
    concat_list = []
    
    for idx, scene in enumerate(scenes):
        scene_dir = os.path.join(TEMP_DIR, f"{orientation}_{scene['id']}")
        os.makedirs(scene_dir, exist_ok=True)
        
        duration = scene["duration"]
        total_frames = int(duration * fps)
        
        # Render keyframe
        if orientation == "vertical":
            frame_img = render_frame_vertical(scene, 0, width, height)
        else:
            frame_img = render_frame_landscape(scene, 0, width, height)
            
        frame_path = os.path.join(scene_dir, "frame.png")
        frame_img.convert("RGB").save(frame_path, "PNG")
        
        # Build scene mp4 from single frame + audio
        scene_mp4 = os.path.join(TEMP_DIR, f"{orientation}_{scene['id']}.mp4")
        
        # Loop frame for duration and mux audio
        cmd = [
            "ffmpeg", "-y",
            "-loop", "1", "-i", frame_path,
            "-i", scene["audio_wav"],
            "-c:v", "libx264", "-tune", "stillimage", "-c:a", "aac", "-b:a", "192k",
            "-pix_fmt", "yuv420p",
            "-t", str(duration),
            "-shortest",
            scene_mp4
        ]
        subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
        concat_list.append(scene_mp4)
        print(f"Rendered {scene['id']} for {orientation}")
        
    # Concat all scenes
    concat_txt = os.path.join(TEMP_DIR, f"concat_{orientation}.txt")
    with open(concat_txt, "w") as f:
        for fpath in concat_list:
            f.write(f"file '{fpath}'\n")
            
    output_filename = f"minimal_os_marketing_{'vertical_9x16' if orientation == 'vertical' else 'landscape_16x9'}.mp4"
    final_output = os.path.join(OUTPUT_DIR, output_filename)
    desktop_output = os.path.join(DESKTOP_DIR, output_filename)
    
    concat_cmd = [
        "ffmpeg", "-y",
        "-f", "concat", "-safe", "0",
        "-i", concat_txt,
        "-c:v", "libx264", "-c:a", "aac",
        "-pix_fmt", "yuv420p",
        final_output
    ]
    subprocess.run(concat_cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
    subprocess.run(["cp", final_output, desktop_output], check=True)
    print(f"Final {orientation} video saved to {final_output} and mirrored to {desktop_output}")
    return final_output

if __name__ == "__main__":
    scenes = generate_voiceovers()
    v_out = render_scenes_to_video(scenes, orientation="vertical")
    l_out = render_scenes_to_video(scenes, orientation="landscape")
    print("Video generation complete!")
    print(f"Vertical: {v_out}")
    print(f"Landscape: {l_out}")
