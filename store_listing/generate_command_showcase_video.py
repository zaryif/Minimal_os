import os
import sys
import subprocess
import json
import math
import asyncio
from PIL import Image, ImageDraw, ImageFont, ImageFilter

BASE_DIR = "/Users/zaryif/Minimal_os"
OUTPUT_DIR = os.path.join(BASE_DIR, "store_listing", "video")
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "store_listing", "screenshots_real")
DESKTOP_DIR = "/Users/zaryif/Desktop/Minimal_OS_Store_Listing/video"
TEMP_DIR = os.path.join(OUTPUT_DIR, "temp_command_showcase")

os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(DESKTOP_DIR, exist_ok=True)
os.makedirs(TEMP_DIR, exist_ok=True)

VOICE_NAME = "en-US-AndrewMultilingualNeural"

# Definition of the 5 showcase scenes
SCENES = [
    {
        "id": "scene1",
        "tag": "TERMINAL VELOCITY",
        "headline": "one command bar.\ntotal control.",
        "narration": "Meet the Minimal OS Command Bar. An intentional, keyboard-first terminal built directly into your home screen. Launch apps, trigger shortcuts, and control your entire Android experience at lightning speed.",
        "subtitle": "Meet the Minimal OS Command Bar. An intentional, keyboard-first terminal on your home screen.",
        "screenshot": "01_live_home_screen.png",
        "subactions": [
            {
                "type": "idle",
                "start": 0.0,
                "end": 2.5,
                "prompt": ""
            },
            {
                "type": "command",
                "start": 2.5,
                "type_end": 5.8,
                "enter_end": 6.6,
                "end": 13.22,
                "text": "help",
                "card": {
                    "tag": "COMMAND REFERENCE GUIDE",
                    "title": "Built-in Terminal Shortcuts",
                    "lines": [
                        "@ <name> <msg> : Direct SMS to contact",
                        "# <name>       : Instant phone call",
                        "- <task>       : Pin to-do to home screen",
                        "! <note>       : Minimal memo & Keep sync",
                        "* <event>      : Google Calendar event",
                        "+ 25m          : Live Pomodoro focus timer",
                        "alias <k> = <v>: Custom terminal shortcut"
                    ],
                    "status": "Over 15 built-in shortcuts ready to use"
                }
            }
        ]
    },
    {
        "id": "scene2",
        "tag": "INSTANT CONNECTIVITY",
        "headline": "type to text.\ntype to call.",
        "narration": "Communicate without distraction. Type at Zaryif, followed by your message, press enter, and your text is instantly dispatched. Type hash Zaryif to initiate a direct phone call in a single fluid gesture.",
        "subtitle": "Communicate in one stroke: '@Zaryif On my way' to message, and '#Zaryif' to place a call.",
        "screenshot": "02_live_terminal_buffer.png",
        "subactions": [
            {
                "type": "command",
                "start": 0.3,
                "type_end": 3.8,
                "enter_end": 4.5,
                "end": 7.0,
                "text": "@Zaryif On my way",
                "card": {
                    "tag": "DIRECT SMS DISPATCH",
                    "title": "Message Ready",
                    "lines": [
                        "Recipient: Zaryif (Contact Verified)",
                        "Body: \"On my way\"",
                        "App: Messages / Cellular SMS"
                    ],
                    "status": "Message drafted and dispatched silently"
                }
            },
            {
                "type": "command",
                "start": 7.0,
                "type_end": 9.8,
                "enter_end": 10.5,
                "end": 14.04,
                "text": "#Zaryif",
                "card": {
                    "tag": "ONE-TAP CELLULAR CALL",
                    "title": "Calling Contact",
                    "lines": [
                        "Contact: Zaryif",
                        "Number: +1 (555) 019-8234",
                        "Line: Default SIM Voice"
                    ],
                    "status": "Direct phone call initiated in one stroke"
                }
            }
        ]
    },
    {
        "id": "scene3",
        "tag": "CAPTURE & FOCUS",
        "headline": "stay in your flow.\nnever lose an idea.",
        "narration": "Capture your thoughts before they slip away. Prefix with a dash to pin a to-do straight to your home screen. Use an exclamation mark to save a scratchpad note with automatic Google Keep sync. Or asterisk to schedule calendar events.",
        "subtitle": "Capture tasks and thoughts: '- Finish my project', '! Life is always good...', and calendar events.",
        "screenshot": "01_live_home_screen_active.png",
        "subactions": [
            {
                "type": "command",
                "start": 0.3,
                "type_end": 3.8,
                "enter_end": 4.5,
                "end": 7.2,
                "text": "- Finish my project",
                "card": {
                    "tag": "FOCUS TO-DO DECK",
                    "title": "Task Pinned to Home",
                    "lines": [
                        "[ ] Finish my project",
                        "Location: Minimal Home Widget",
                        "Storage: Encrypted Local SQLite"
                    ],
                    "status": "Task added directly to your home screen"
                }
            },
            {
                "type": "command",
                "start": 7.2,
                "type_end": 11.2,
                "enter_end": 11.9,
                "end": 15.02,
                "text": "! Life is always good when you have Minimal OS as your default",
                "card": {
                    "tag": "SCRATCHPAD & KEEP SYNC",
                    "title": "Minimal Memo Saved",
                    "lines": [
                        "\"Life is always good when you have",
                        " Minimal OS as your default\"",
                        "Sync: Google Keep Integration Active"
                    ],
                    "status": "Saved to Minimal Memo and synced to Keep"
                }
            }
        ]
    },
    {
        "id": "scene4",
        "tag": "CUSTOM ALIASES",
        "headline": "your rules.\nyour terminal.",
        "narration": "Automate your daily routines with custom aliases. Define alias y t to launch YouTube search, or create shortcuts for live Pomodoro focus countdowns. Tailor your command syntax to fit your exact workflow.",
        "subtitle": "Create custom aliases directly in the prompt: 'alias yt = youtube' and live Pomodoro timers.",
        "screenshot": "08_live_focus_timer_home.png",
        "subactions": [
            {
                "type": "command",
                "start": 0.3,
                "type_end": 3.6,
                "enter_end": 4.3,
                "end": 6.3,
                "text": "alias yt = https://youtube.com",
                "card": {
                    "tag": "CUSTOM ALIAS SAVED",
                    "title": "Alias Registered",
                    "lines": [
                        "Trigger: 'yt'",
                        "Target: https://youtube.com",
                        "Scope: Global Terminal Prompt"
                    ],
                    "status": "Custom shortcut ready for instant execution"
                }
            },
            {
                "type": "command",
                "start": 6.3,
                "type_end": 8.5,
                "enter_end": 9.2,
                "end": 10.6,
                "text": "yt lofi beats",
                "card": {
                    "tag": "EXECUTING ALIAS: yt",
                    "title": "Macro Dispatched",
                    "lines": [
                        "Action: Launching YouTube",
                        "Query: \"lofi beats\"",
                        "Status: Handed off to browser / app"
                    ],
                    "status": "Executing alias with passed arguments"
                }
            },
            {
                "type": "command",
                "start": 10.6,
                "type_end": 11.8,
                "enter_end": 12.4,
                "end": 14.40,
                "text": "+25m",
                "card": {
                    "tag": "LIVE POMODORO TIMER",
                    "title": "Focus Session Started",
                    "lines": [
                        "24:59 Remaining | Work Session",
                        "Widget: Pinned to Home Screen",
                        "Haptics: Silent notification on finish"
                    ],
                    "status": "Live countdown running on your home screen"
                }
            }
        ]
    },
    {
        "id": "scene5",
        "tag": "COMMAND REFERENCE",
        "headline": "power user speed.\nzero distractions.",
        "narration": "Solve calculations on the fly with inline math, recall previous commands with up-arrow history, and tap the guide to explore all shortcuts. Minimal OS puts desktop-class speed right into your pocket.",
        "subtitle": "Inline math calculation, up-arrow command history, and built-in shortcuts guide.",
        "screenshot": "03_command_guide_dialog.png",
        "subactions": [
            {
                "type": "command",
                "start": 0.3,
                "type_end": 2.8,
                "enter_end": 3.5,
                "end": 5.4,
                "text": "calc 128 * 16",
                "card": {
                    "tag": "INLINE MATH ENGINE",
                    "title": "Instant Evaluation",
                    "lines": [
                        "Expression: 128 * 16",
                        "Result: = 2,048",
                        "Engine: Zero-latency parser"
                    ],
                    "status": "Calculated instantly without opening calculator"
                }
            },
            {
                "type": "command",
                "start": 5.4,
                "type_end": 7.0,
                "enter_end": 7.6,
                "end": 9.2,
                "text": "[UP ARROW] HISTORY RECALL",
                "card": {
                    "tag": "COMMAND BUFFER HISTORY",
                    "title": "Cycle Recent Commands",
                    "lines": [
                        "[3] calc 128 * 16",
                        "[2] ! Life is always good...",
                        "[1] - Finish my project",
                        "[0] @Zaryif On my way"
                    ],
                    "status": "Recall any previous command with one tap"
                }
            },
            {
                "type": "outro",
                "start": 9.2,
                "end": 13.25,
                "card": {
                    "tag": "MINIMAL OS LAUNCHER",
                    "title": "Reclaim Your Attention",
                    "lines": [
                        "100% Offline  |  Zero Telemetry  |  No Ads",
                        "Text-First Minimalist Android Experience",
                        "Available Now on Google Play Store"
                    ],
                    "status": "Minimal OS: The intentional phone launcher"
                }
            }
        ]
    }
]

def generate_voiceovers():
    import edge_tts
    print("Checking / Generating neural voiceover audio...")
    for scene in SCENES:
        mp3_path = os.path.join(TEMP_DIR, f"{scene['id']}.mp3")
        wav_path = os.path.join(TEMP_DIR, f"{scene['id']}.wav")
        
        if not os.path.exists(wav_path):
            async def fetch():
                comm = edge_tts.Communicate(scene["narration"], VOICE_NAME)
                await comm.save(mp3_path)
            asyncio.run(fetch())
            subprocess.run(["ffmpeg", "-y", "-i", mp3_path, "-ar", "44100", "-ac", "2", wav_path],
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
            
        probe = subprocess.run([
            "ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "json", wav_path
        ], capture_output=True, text=True, check=True)
        dur = float(json.loads(probe.stdout)["format"]["duration"])
        scene["audio_wav"] = wav_path
        scene["duration"] = dur + 0.5
        print(f"Scene {scene['id']}: audio duration={dur:.2f}s, total={scene['duration']:.2f}s")
        scene["subactions"][-1]["end"] = scene["duration"]
    return SCENES

def get_font(size, bold=False, mono=False):
    if mono:
        paths = ["/System/Library/Fonts/SFNSMono.ttf", "/System/Library/Fonts/Menlo.ttc"]
    elif bold:
        paths = ["/System/Library/Fonts/SFNS.ttf", "/System/Library/Fonts/HelveticaNeue.ttc", "app/src/main/res/font/inter_regular.ttf"]
    else:
        paths = ["/System/Library/Fonts/SFNS.ttf", "/System/Library/Fonts/HelveticaNeue.ttc", "app/src/main/res/font/inter_regular.ttf"]
    
    for p in paths:
        if os.path.exists(p):
            try:
                return ImageFont.truetype(p, size)
            except Exception:
                pass
    return ImageFont.load_default()

def get_active_subaction(scene, t_local):
    for sa in scene["subactions"]:
        if sa["start"] <= t_local < sa["end"]:
            return sa
    return scene["subactions"][-1]

# Pre-render static base frame for vertical format
def build_static_vertical_base(scene, width=1080, height=1920):
    img = Image.new("RGBA", (width, height), (10, 10, 12, 255))
    draw = ImageDraw.Draw(img)
    
    # Top decorative gradient accent
    for y in range(350):
        alpha = int(32 * (1 - y / 350))
        draw.line([(0, y), (width, y)], fill=(229, 57, 53, alpha))
        
    # Header area
    draw.line([(80, 80), (220, 80)], fill=(229, 57, 53, 255), width=3)
    font_tag = get_font(26, bold=True, mono=True)
    draw.text((80, 105), scene["tag"], font=font_tag, fill=(229, 57, 53, 255))
    
    font_headline = get_font(60, bold=True)
    draw.text((80, 155), scene["headline"], font=font_headline, fill=(245, 245, 248, 255))
    
    # Phone Mockup Stage
    mock_w = 680
    mock_h = 1040
    mock_x = (width - mock_w) // 2
    mock_y = 350
    
    # Outer frame
    draw.rounded_rectangle([mock_x - 5, mock_y - 5, mock_x + mock_w + 5, mock_y + mock_h + 5], radius=32, fill=(15, 15, 18, 255), outline=(48, 48, 54, 255), width=2)
    draw.rounded_rectangle([mock_x, mock_y, mock_x + mock_w, mock_y + mock_h], radius=28, fill=(10, 10, 12, 255))
    
    # Pre-render screenshot inside phone
    ss_name = scene.get("screenshot")
    if ss_name:
        ss_path = os.path.join(SCREENSHOTS_DIR, ss_name)
        if os.path.exists(ss_path):
            try:
                ss = Image.open(ss_path).convert("RGBA")
                ss_resized = ss.resize((mock_w, mock_h), Image.Resampling.BILINEAR)
                dark_overlay = Image.new("RGBA", (mock_w, mock_h), (0, 0, 0, 195))
                ss_faded = Image.alpha_composite(ss_resized, dark_overlay)
                
                mask = Image.new("L", (mock_w, mock_h), 0)
                ImageDraw.Draw(mask).rounded_rectangle([0, 0, mock_w, mock_h], radius=28, fill=255)
                img.paste(ss_faded, (mock_x, mock_y), mask)
            except Exception:
                pass
                
    draw.rounded_rectangle([mock_x, mock_y, mock_x + mock_w, mock_y + mock_h], radius=28, outline=(48, 48, 54, 255), width=2)
    
    # Pre-draw static Subtitle card background & branding
    sub_y = 1540
    draw.rounded_rectangle([60, sub_y, width - 60, sub_y + 180], radius=20, fill=(18, 18, 22, 240), outline=(45, 45, 55, 255), width=2)
    
    font_sub = get_font(34, bold=False)
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
        draw.text((width // 2, sub_y + 44 + i * 46), line, font=font_sub, fill=(240, 240, 245, 255), anchor="mm")
        
    font_brand = get_font(22, bold=True, mono=True)
    draw.text((width // 2, 1830), "MINIMAL OS  |  THE TEXT-FIRST LAUNCHER  |  GOOGLE PLAY", font=font_brand, fill=(140, 140, 150, 255), anchor="mm")
    
    return img, (mock_x, mock_y, mock_w, mock_h)

# Pre-render static base frame for landscape format
def build_static_landscape_base(scene, width=1920, height=1080):
    img = Image.new("RGBA", (width, height), (10, 10, 12, 255))
    draw = ImageDraw.Draw(img)
    
    # Left column background subtle glow
    for x in range(500):
        alpha = int(24 * (1 - x / 500))
        draw.line([(x, 0), (x, height)], fill=(229, 57, 53, alpha))
        
    # Left Column: Typography & Subtitles
    draw.line([(100, 90), (240, 90)], fill=(229, 57, 53, 255), width=3)
    
    font_tag = get_font(24, bold=True, mono=True)
    draw.text((100, 115), scene["tag"], font=font_tag, fill=(229, 57, 53, 255))
    
    font_head = get_font(56, bold=True)
    draw.text((100, 165), scene["headline"], font=font_head, fill=(245, 245, 248, 255))
    
    # Subtitle Card on Left Bottom
    sub_y = 680
    draw.rounded_rectangle([100, sub_y, 880, sub_y + 220], radius=18, fill=(22, 22, 28, 255), outline=(50, 50, 65, 255), width=2)
    
    font_sub = get_font(30, bold=False)
    words = scene["subtitle"].split()
    lines = []
    curr = []
    for w in words:
        curr.append(w)
        if len(" ".join(curr)) > 38:
            lines.append(" ".join(curr[:-1]))
            curr = [w]
    if curr:
        lines.append(" ".join(curr))
    for i, line in enumerate(lines[:3]):
        draw.text((130, sub_y + 45 + i * 42), line, font=font_sub, fill=(240, 240, 245, 255), anchor="lm")
        
    font_brand = get_font(20, bold=True, mono=True)
    draw.text((100, 960), "MINIMAL OS  |  100% OFFLINE  |  GOOGLE PLAY", font=font_brand, fill=(140, 140, 150, 255))
    
    # Right Column: Phone Mockup Stage
    mock_w = 460
    mock_h = 920
    mock_x = 1000
    mock_y = 80
    
    draw.rounded_rectangle([mock_x - 5, mock_y - 5, mock_x + mock_w + 5, mock_y + mock_h + 5], radius=32, fill=(15, 15, 18, 255), outline=(48, 48, 54, 255), width=2)
    draw.rounded_rectangle([mock_x, mock_y, mock_x + mock_w, mock_y + mock_h], radius=28, fill=(10, 10, 12, 255))
    
    ss_name = scene.get("screenshot")
    if ss_name:
        ss_path = os.path.join(SCREENSHOTS_DIR, ss_name)
        if os.path.exists(ss_path):
            try:
                ss = Image.open(ss_path).convert("RGBA")
                ss_resized = ss.resize((mock_w, mock_h), Image.Resampling.BILINEAR)
                dark_overlay = Image.new("RGBA", (mock_w, mock_h), (0, 0, 0, 195))
                ss_faded = Image.alpha_composite(ss_resized, dark_overlay)
                mask = Image.new("L", (mock_w, mock_h), 0)
                ImageDraw.Draw(mask).rounded_rectangle([0, 0, mock_w, mock_h], radius=28, fill=255)
                img.paste(ss_faded, (mock_x, mock_y), mask)
            except Exception:
                pass
                
    draw.rounded_rectangle([mock_x, mock_y, mock_x + mock_w, mock_y + mock_h], radius=28, outline=(48, 48, 54, 255), width=2)
    return img, (mock_x, mock_y, mock_w, mock_h)

def draw_command_bar_widget(draw, cx, cy, bar_w, bar_h, prompt_text, is_enter=False, cursor_blink=True):
    bx = cx - bar_w // 2
    by = cy - bar_h // 2
    
    border_color = (255, 82, 82, 255) if is_enter else (70, 70, 80, 255)
    bar_bg = (30, 22, 25, 255) if is_enter else (22, 22, 26, 255)
    border_w = 3 if is_enter else 2
    
    if is_enter:
        draw.rounded_rectangle([bx - 4, by - 4, bx + bar_w + 4, by + bar_h + 4], radius=20, outline=(255, 82, 82, 120), width=4)
        
    draw.rounded_rectangle([bx, by, bx + bar_w, by + bar_h], radius=16, fill=bar_bg, outline=border_color, width=border_w)
    
    font_mono = get_font(bar_h // 2 - 4, mono=True)
    draw.text((bx + 20, cy), ">", font=font_mono, fill=(229, 57, 53, 255), anchor="lm")
    
    cursor_str = "_" if cursor_blink else " "
    full_str = prompt_text + cursor_str
    
    draw.text((bx + 54, cy), full_str, font=font_mono, fill=(255, 255, 255, 255), anchor="lm")
    
    enter_badge_bg = (229, 57, 53, 255) if is_enter else (36, 36, 42, 255)
    enter_badge_text = (255, 255, 255, 255) if is_enter else (160, 160, 170, 255)
    font_badge = get_font(18, bold=True, mono=True)
    eb_w = 88
    eb_h = bar_h - 20
    eb_x = bx + bar_w - eb_w - 10
    eb_y = by + 10
    draw.rounded_rectangle([eb_x, eb_y, eb_x + eb_w, eb_y + eb_h], radius=10, fill=enter_badge_bg)
    draw.text((eb_x + eb_w // 2, eb_y + eb_h // 2), "RUN ↵" if is_enter else "EXEC", font=font_badge, fill=enter_badge_text, anchor="mm")

def draw_suggestions_pills(draw, cx, cy, bar_w):
    pills = ["@ Msg", "# Call", "- Task", "! Note", "* Event", "+ Timer", "alias"]
    font_pill = get_font(16, bold=True, mono=True)
    total_w = len(pills) * 88
    start_x = cx - total_w // 2
    for i, p in enumerate(pills):
        px = start_x + i * 88
        draw.rounded_rectangle([px, cy, px + 80, cy + 28], radius=8, fill=(26, 26, 32, 255), outline=(50, 50, 60, 255), width=1)
        draw.text((px + 40, cy + 14), p, font=font_pill, fill=(180, 180, 190, 255), anchor="mm")

def draw_result_card(draw, cx, cy, card_w, card_h, card_data, alpha=1.0):
    if not card_data:
        return
    bx = cx - card_w // 2
    by = cy - card_h // 2
    
    draw.rounded_rectangle([bx, by, bx + card_w, by + card_h], radius=18, fill=(20, 20, 26, int(255 * alpha)), outline=(65, 65, 80, int(255 * alpha)), width=2)
    
    font_tag = get_font(18, bold=True, mono=True)
    draw.rounded_rectangle([bx + 24, by + 20, bx + 24 + 280, by + 50], radius=8, fill=(229, 57, 53, 220))
    draw.text((bx + 34, by + 35), card_data.get("tag", "EXECUTION RESULT"), font=font_tag, fill=(255, 255, 255, 255), anchor="lm")
    
    font_title = get_font(26, bold=True)
    draw.text((bx + 24, by + 76), card_data.get("title", ""), font=font_title, fill=(250, 250, 252, 255), anchor="lm")
    
    font_line = get_font(19, mono=True)
    lines = card_data.get("lines", [])
    for idx, l in enumerate(lines[:5]):
        draw.text((bx + 24, by + 116 + idx * 28), l, font=font_line, fill=(210, 210, 220, 255), anchor="lm")
        
    font_status = get_font(18, bold=True)
    status_y = by + card_h - 28
    draw.line([(bx + 24, status_y - 12), (bx + card_w - 24, status_y - 12)], fill=(45, 45, 55, 255), width=1)
    draw.text((bx + 24, status_y), "✓ " + card_data.get("status", "Executed"), font=font_status, fill=(0, 230, 118, 255), anchor="lm")

def render_scenes_to_video(scenes, orientation="vertical"):
    width, height = (1080, 1920) if orientation == "vertical" else (1920, 1080)
    print(f"\n=======================================================")
    print(f"Rendering {orientation} video ({width}x{height}) via ffmpeg pipe...")
    print(f"=======================================================")
    
    fps = 24
    scene_mp4s = []
    
    for idx, scene in enumerate(scenes):
        duration = scene["duration"]
        total_frames = int(duration * fps)
        scene_mp4 = os.path.join(TEMP_DIR, f"{orientation}_{scene['id']}.mp4")
        
        print(f"Encoding Scene {scene['id']} ({duration:.2f}s, {total_frames} frames)...")
        
        # Pre-build static base
        if orientation == "vertical":
            base_canvas, (mock_x, mock_y, mock_w, mock_h) = build_static_vertical_base(scene, width, height)
        else:
            base_canvas, (mock_x, mock_y, mock_w, mock_h) = build_static_landscape_base(scene, width, height)
            
        cmd = [
            "ffmpeg", "-y",
            "-f", "rawvideo",
            "-vcodec", "rawvideo",
            "-s", f"{width}x{height}",
            "-pix_fmt", "rgba",
            "-r", str(fps),
            "-i", "-",
            "-i", scene["audio_wav"],
            "-c:v", "libx264",
            "-preset", "ultrafast",
            "-crf", "18",
            "-pix_fmt", "yuv420p",
            "-c:a", "aac",
            "-b:a", "192k",
            "-shortest",
            scene_mp4
        ]
        
        proc = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        
        for frame_idx in range(total_frames):
            t_local = frame_idx / fps
            img = base_canvas.copy()
            draw = ImageDraw.Draw(img)
            
            sa = get_active_subaction(scene, t_local)
            cursor_blink = int(t_local * 3.5) % 2 == 0
            
            prompt_str = ""
            is_enter = False
            show_card = False
            card_alpha = 1.0
            
            if sa["type"] == "idle":
                prompt_str = ""
                show_card = False
            elif sa["type"] == "command":
                t_start = sa["start"]
                t_type = sa["type_end"]
                t_enter = sa["enter_end"]
                target_text = sa["text"]
                
                if t_local < t_type:
                    progress = max(0.0, min(1.0, (t_local - t_start) / max(0.1, t_type - t_start)))
                    chars = int(progress * len(target_text))
                    prompt_str = target_text[:chars]
                    show_card = False
                elif t_local < t_enter:
                    prompt_str = target_text
                    is_enter = True
                    show_card = False
                else:
                    prompt_str = target_text
                    is_enter = False
                    show_card = True
                    card_alpha = min(1.0, (t_local - t_enter) / 0.3)
            elif sa["type"] == "outro":
                prompt_str = "Minimal OS"
                show_card = True
                card_alpha = 1.0

            if orientation == "vertical":
                bar_w = mock_w - 60
                bar_h = 68
                bar_cx = width // 2
                bar_cy = mock_y + 110
                draw_command_bar_widget(draw, bar_cx, bar_cy, bar_w, bar_h, prompt_str, is_enter, cursor_blink)
                draw_suggestions_pills(draw, bar_cx, bar_cy + 52, bar_w)
                
                if show_card and "card" in sa:
                    card_w = mock_w - 60
                    card_h = 420
                    card_cx = width // 2
                    card_cy = bar_cy + 52 + 35 + card_h // 2 + 15
                    draw_result_card(draw, card_cx, card_cy, card_w, card_h, sa["card"], card_alpha)
            else:
                # Landscape layout
                info_y = 380
                draw.rounded_rectangle([100, info_y, 880, info_y + 240], radius=16, fill=(18, 18, 22, 240), outline=(45, 45, 55, 255), width=2)
                font_card_head = get_font(20, bold=True, mono=True)
                draw.text((130, info_y + 35), "COMMAND BAR ACTIVE MODE", font=font_card_head, fill=(229, 57, 53, 255))
                active_cmd = sa.get("text", "Terminal Prompt Active")
                font_active_cmd = get_font(28, bold=True, mono=True)
                draw.text((130, info_y + 80), f"> {active_cmd}", font=font_active_cmd, fill=(255, 255, 255, 255))
                font_desc = get_font(22, bold=False)
                desc_text = sa.get("card", {}).get("status", "Keyboard-first velocity without app grid clutter.")
                draw.text((130, info_y + 130), desc_text, font=font_desc, fill=(190, 190, 200, 255))
                
                bar_w = mock_w - 40
                bar_h = 58
                bar_cx = mock_x + mock_w // 2
                bar_cy = mock_y + 90
                draw_command_bar_widget(draw, bar_cx, bar_cy, bar_w, bar_h, prompt_str, is_enter, cursor_blink)
                
                card_x = 1500
                card_y = 80
                card_w = 360
                card_h = 500
                if show_card and "card" in sa:
                    draw_result_card(draw, card_x + card_w // 2, card_y + card_h // 2, card_w, card_h, sa["card"], card_alpha)
                    draw.line([(mock_x + mock_w, bar_cy), (card_x, card_y + 60)], fill=(229, 57, 53, 160), width=2)
                else:
                    draw.rounded_rectangle([card_x, card_y, card_x + card_w, card_y + card_h], radius=18, fill=(18, 18, 22, 240), outline=(45, 45, 55, 255), width=2)
                    font_sc = get_font(20, bold=True, mono=True)
                    draw.text((card_x + 24, card_y + 35), "TERMINAL SHORTCUTS", font=font_sc, fill=(229, 57, 53, 255))
                    shortcuts = [
                        ("@", "Direct text message"),
                        ("#", "Phone call"),
                        ("-", "Pin home to-do"),
                        ("!", "Scratchpad note"),
                        ("*", "Calendar event"),
                        ("+", "Pomodoro timer"),
                        ("alias", "Custom command")
                    ]
                    font_row = get_font(18, mono=True)
                    for s_idx, (sym, label) in enumerate(shortcuts):
                        ry = card_y + 80 + s_idx * 54
                        draw.rounded_rectangle([card_x + 24, ry, card_x + 64, ry + 36], radius=8, fill=(28, 28, 35, 255), outline=(55, 55, 70, 255))
                        draw.text((card_x + 44, ry + 18), sym, font=font_row, fill=(255, 82, 82, 255), anchor="mm")
                        draw.text((card_x + 78, ry + 18), label, font=font_row, fill=(210, 210, 220, 255), anchor="lm")
                        
            proc.stdin.write(img.tobytes())
            
        proc.stdin.close()
        proc.wait()
        
        if proc.returncode != 0:
            raise RuntimeError(f"FFmpeg failed with code {proc.returncode} on scene {scene['id']}")
            
        scene_mp4s.append(scene_mp4)
        print(f"Finished Scene {scene['id']} -> {scene_mp4}")

    concat_txt = os.path.join(TEMP_DIR, f"concat_{orientation}.txt")
    with open(concat_txt, "w") as f:
        for fpath in scene_mp4s:
            f.write(f"file '{fpath}'\n")
            
    output_filename = f"minimal_os_command_showcase_{'vertical_9x16' if orientation == 'vertical' else 'landscape_16x9'}.mp4"
    final_output = os.path.join(OUTPUT_DIR, output_filename)
    desktop_output = os.path.join(DESKTOP_DIR, output_filename)
    
    print(f"Concatenating all scenes into {final_output}...")
    concat_cmd = [
        "ffmpeg", "-y",
        "-f", "concat", "-safe", "0",
        "-i", concat_txt,
        "-c:v", "copy", "-c:a", "copy",
        final_output
    ]
    subprocess.run(concat_cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
    subprocess.run(["cp", final_output, desktop_output], check=True)
    print(f"Successfully generated {final_output}")
    print(f"Mirrored to Desktop at {desktop_output}")
    return final_output

if __name__ == "__main__":
    scenes = generate_voiceovers()
    v_out = render_scenes_to_video(scenes, orientation="vertical")
    l_out = render_scenes_to_video(scenes, orientation="landscape")
    print("\nALL SHOWCASE VIDEOS SUCCESSFULLY GENERATED!")
    print(f"Vertical (9:16):   {v_out}")
    print(f"Landscape (16:9):  {l_out}")
