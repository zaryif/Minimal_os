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
TEMP_DIR = os.path.join(OUTPUT_DIR, "temp_virtual_phone_code")

os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(DESKTOP_DIR, exist_ok=True)
os.makedirs(TEMP_DIR, exist_ok=True)

VOICE_NAME = "en-US-AndrewMultilingualNeural"

# Definition of the 5 scenes showing Code + Real Virtual Phone in action
SCENES = [
    {
        "id": "scene1",
        "tag": "UNIFIED COMMAND ARCHITECTURE",
        "headline": "code to execution.\npure speed.",
        "narration": "Welcome to the architectural breakdown of Minimal OS. Every action in this launcher is driven by a unified, hardware-accelerated command terminal. You type, and Android responds instantly with zero middleman apps.",
        "subtitle": "Architectural breakdown: a unified, hardware-accelerated command terminal running directly on Android.",
        "code_file": "CommandBar.kt",
        "code_lines": [
            ("// Jetpack Compose Terminal Bar", "comment"),
            ("@Composable", "annotation"),
            ("fun CommandBar(", "keyword"),
            ("    query: String,", "code"),
            ("    onExecute: (String) -> Unit,", "keyword"),
            ("    commandHistory: List<String>,", "code"),
            ("    customAliases: Set<String>", "code"),
            (") {", "code"),
            ("    // Syntax highlighter triggers MinimalRed", "comment"),
            ("    val actionTransformation = remember { ... }", "code"),
            ("}", "code")
        ],
        "phone_states": [
            {"time": 0.0, "img": "real_01_home_screen.png", "caption": "Live Home Screen Prompt"},
            {"time": 6.5, "img": "real_02_typing_message.png", "caption": "Real CommandBar Focused & Active"}
        ]
    },
    {
        "id": "scene2",
        "tag": "INTENT DISPATCH ENGINE",
        "headline": "type to message.\ntype to call.",
        "narration": "Watch it happen in the virtual device. Type at Zaryif, followed by your message. Minimal OS resolves the contact and prepares your message. Prefix with hash for an instant phone call, executing direct Android intents in one stroke.",
        "subtitle": "Watch real execution: '@Zaryif On my way' resolves contacts, while '#Zaryif' dispatches direct call intents.",
        "code_file": "ExecuteCommandUseCase.kt",
        "code_lines": [
            ("// Direct Android Intent Dispatch", "comment"),
            ("when (command.type) {", "keyword"),
            ("    CommandType.MESSAGE -> {", "keyword"),
            ("        val contact = contactRepo.search(query)", "code"),
            ("        Intent(ACTION_SENDTO).apply {", "keyword"),
            ("            data = Uri.parse(\"smsto:${contact.phone}\")", "string"),
            ("            putExtra(\"sms_body\", body)", "string"),
            ("        }", "code"),
            ("    }", "code"),
            ("    CommandType.CALL -> dial(contact.phone)", "keyword"),
            ("}", "code")
        ],
        "phone_states": [
            {"time": 0.0, "img": "real_02_typing_message.png", "caption": "Typing: @Zaryif On my way"},
            {"time": 4.5, "img": "real_03_executed_message.png", "caption": "Action Dispatched: SMS Drafted"},
            {"time": 8.0, "img": "real_04_typing_call.png", "caption": "Typing: #Zaryif (Direct Voice Call)"}
        ]
    },
    {
        "id": "scene3",
        "tag": "LOCAL SQLITE & CLOUD SYNC",
        "headline": "focus to-dos.\nminimal memo.",
        "narration": "Never break your concentration. Prefixing with a dash calls storageRepository.addTodo, instantly pinning your task to the home screen widget. An exclamation mark writes directly to local encrypted storage and triggers background Google Keep sync.",
        "subtitle": "Never break flow: '-' pins to-dos to home screen SQLite widgets, '!' writes notes with Google Keep sync.",
        "code_file": "ExecuteCommandUseCase.kt",
        "code_lines": [
            ("// Local SQLite & Google Keep Sync", "comment"),
            ("CommandType.TODO -> {", "keyword"),
            ("    storageRepository.addTodo(task)", "code"),
            ("    CommandResult.Success(\"Saved to-do\")", "string"),
            ("}", "code"),
            ("CommandType.NOTE -> {", "keyword"),
            ("    storageRepository.addNote(memo)", "code"),
            ("    syncGoogleKeepIfAvailable(memo)", "code"),
            ("    CommandResult.Success(\"Saved note\")", "string"),
            ("}", "code")
        ],
        "phone_states": [
            {"time": 0.0, "img": "real_05_typing_todo.png", "caption": "Typing: - Finish my project"},
            {"time": 4.2, "img": "real_06_executed_todo.png", "caption": "Task Pinned LIVE to Home Screen To-Do"},
            {"time": 8.5, "img": "real_07_typing_note.png", "caption": "Typing: ! Life is always good..."},
            {"time": 11.5, "img": "real_08_executed_note.png", "caption": "Minimal Memo Saved & Synced to Keep"}
        ]
    },
    {
        "id": "scene4",
        "tag": "USER ALIAS ENGINE & TIMERS",
        "headline": "custom shortcuts.\nlive countdowns.",
        "narration": "Create user-defined commands on the fly. Typing alias y t equals youtube persists the key in DataStore preferences. Then simply type y t with search keywords to launch straight into your query. Or type plus 25m to start a live Pomodoro countdown.",
        "subtitle": "User-defined aliases: 'alias yt = youtube' saved to DataStore, macro queries, and '+25m' live focus countdowns.",
        "code_file": "UserPreferences.kt",
        "code_lines": [
            ("// Dynamic Custom Alias Storage", "comment"),
            ("suspend fun setCustomCommand(k: String, target: String) {", "keyword"),
            ("    dataStore.edit { prefs ->", "keyword"),
            ("        val map = prefs[CUSTOM_MAP] ?: emptyMap()", "code"),
            ("        prefs[CUSTOM_MAP] = map + (k to target)", "code"),
            ("    }", "code"),
            ("}", "code"),
            ("// Start Live Pomodoro Timer Widget", "comment"),
            ("fun handlePomodoro(arg: String) = startTimer(25 * 60)", "code")
        ],
        "phone_states": [
            {"time": 0.0, "img": "real_09_typing_alias.png", "caption": "Typing: alias yt = https://youtube.com"},
            {"time": 3.8, "img": "real_10_executed_alias.png", "caption": "Result: Saved Custom Alias 'yt'"},
            {"time": 7.0, "img": "real_11_typing_macro.png", "caption": "Executing: yt lofi beats"},
            {"time": 10.2, "img": "real_12_typing_timer.png", "caption": "Typing: +25m"},
            {"time": 12.0, "img": "real_13_executed_timer.png", "caption": "Pomodoro Focus Timer ACTIVE on Home"}
        ]
    },
    {
        "id": "scene5",
        "tag": "COMMAND HISTORY & CHEAT SHEET",
        "headline": "terminal buffer.\ninstant history.",
        "narration": "Here is the real command history in action. Minimal OS logs past commands into an interactive terminal buffer stream. Tap any previous action to re-run it, cycle with hardware keys, or open the built-in guide for the full command cheat sheet.",
        "subtitle": "Real command history: interactive terminal buffer stream, HIST cycling, and built-in shortcuts guide.",
        "code_file": "CommandHistoryChatStream.kt",
        "code_lines": [
            ("// Interactive Terminal History Buffer", "comment"),
            ("@Composable", "annotation"),
            ("fun CommandHistoryChatStream(", "keyword"),
            ("    history: List<String>,", "code"),
            ("    onSelectCommand: (String) -> Unit,", "keyword"),
            ("    onClearHistory: () -> Unit", "code"),
            (") {", "code"),
            ("    // Renders chat cards for all executed commands", "comment"),
            ("    LazyColumn { items(history) { Card(...) } }", "code"),
            ("}", "code")
        ],
        "phone_states": [
            {"time": 0.0, "img": "real_14_terminal_buffer_stream.png", "caption": "REAL TERMINAL BUFFER // HISTORY STREAM"},
            {"time": 4.5, "img": "real_15_history_cycled.png", "caption": "Cycling Command History: HIST 1/N"},
            {"time": 8.5, "img": "real_16_command_guide_dialog.png", "caption": "In-App Command Guide Cheat Sheet"}
        ]
    }
]

def generate_voiceovers():
    import edge_tts
    print("Checking / Generating neural voiceovers for virtual phone showcase...")
    for scene in SCENES:
        mp3_path = os.path.join(TEMP_DIR, f"vp_{scene['id']}.mp3")
        wav_path = os.path.join(TEMP_DIR, f"vp_{scene['id']}.wav")
        
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
        scene["duration"] = dur + 0.6
        print(f"Scene {scene['id']}: audio duration={dur:.2f}s, total={scene['duration']:.2f}s")
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

def get_current_phone_state(scene, t_local):
    states = scene["phone_states"]
    active = states[0]
    for s in states:
        if t_local >= s["time"]:
            active = s
    return active

def draw_code_editor_window(draw, x, y, w, h, filename, code_lines):
    # Window card
    draw.rounded_rectangle([x, y, x + w, y + h], radius=16, fill=(16, 18, 24, 255), outline=(42, 46, 60, 255), width=2)
    
    # Title bar
    draw.rounded_rectangle([x, y, x + w, y + 44], radius=16, fill=(22, 25, 34, 255))
    draw.rectangle([x, y + 24, x + w, y + 44], fill=(22, 25, 34, 255)) # Square bottom of header
    draw.line([(x, y + 44), (x + w, y + 44)], fill=(42, 46, 60, 255), width=1)
    
    # Window buttons
    draw.ellipse([x + 16, y + 16, x + 28, y + 28], fill=(255, 95, 86, 255))
    draw.ellipse([x + 36, y + 16, x + 48, y + 28], fill=(255, 189, 46, 255))
    draw.ellipse([x + 56, y + 16, x + 68, y + 28], fill=(39, 201, 63, 255))
    
    # File tab
    font_tab = get_font(18, bold=True, mono=True)
    draw.text((x + 88, y + 22), f"kt  {filename}", font=font_tab, fill=(220, 225, 235, 255), anchor="lm")
    
    # Code body
    font_code = get_font(18, mono=True)
    font_num = get_font(16, mono=True)
    
    line_y = y + 64
    for idx, (code_text, kind) in enumerate(code_lines):
        # Line number
        draw.text((x + 20, line_y), f"{idx + 1:2d}", font=font_num, fill=(80, 85, 105, 255))
        
        # Color coding
        if kind == "comment":
            color = (105, 115, 135, 255)
        elif kind == "annotation":
            color = (255, 180, 84, 255)
        elif kind == "keyword":
            color = (199, 146, 234, 255)
        elif kind == "string":
            color = (195, 232, 141, 255)
        else:
            color = (235, 240, 250, 255)
            
        draw.text((x + 64, line_y), code_text, font=font_code, fill=color)
        line_y += 32

def draw_virtual_phone_frame(draw, x, y, w, h, img_cache, caption=""):
    # Phone outer bezel
    draw.rounded_rectangle([x - 6, y - 6, x + w + 6, y + h + 6], radius=34, fill=(18, 18, 22, 255), outline=(55, 58, 70, 255), width=2)
    # Screen inner background
    draw.rounded_rectangle([x, y, x + w, y + h], radius=28, fill=(10, 10, 12, 255))
    
    if img_cache:
        mask = Image.new("L", (w, h), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, w, h], radius=28, fill=255)
        # Paste directly into destination image
        # Note: img_cache is already pre-resized
        return img_cache, mask
    return None, None

def pre_cache_screenshots(target_w, target_h):
    cache = {}
    for fname in os.listdir(SCREENSHOTS_DIR):
        if fname.endswith(".png"):
            fpath = os.path.join(SCREENSHOTS_DIR, fname)
            try:
                im = Image.open(fpath).convert("RGBA")
                resized = im.resize((target_w, target_h), Image.Resampling.BILINEAR)
                cache[fname] = resized
            except Exception:
                pass
    return cache

def render_landscape_frame(scene, t_local, total_dur, cache, width=1920, height=1080):
    img = Image.new("RGBA", (width, height), (10, 10, 12, 255))
    draw = ImageDraw.Draw(img)
    
    # Left accent glow
    for x in range(450):
        alpha = int(22 * (1 - x / 450))
        draw.line([(x, 0), (x, height)], fill=(229, 57, 53, alpha))
        
    # Left Column: Header (x: 80 to 980)
    draw.line([(80, 70), (220, 70)], fill=(229, 57, 53, 255), width=3)
    font_tag = get_font(22, bold=True, mono=True)
    draw.text((80, 95), scene["tag"], font=font_tag, fill=(229, 57, 53, 255))
    
    font_head = get_font(52, bold=True)
    draw.text((80, 140), scene["headline"], font=font_head, fill=(245, 245, 248, 255))
    
    # Code Editor Window on Left (x: 80, y: 270, w: 900, h: 440)
    code_w = 900
    code_h = 440
    code_x = 80
    code_y = 265
    draw_code_editor_window(draw, code_x, code_y, code_w, code_h, scene["code_file"], scene["code_lines"])
    
    # Subtitle Card on Left Bottom (y: 735 to 940)
    sub_y = 735
    draw.rounded_rectangle([80, sub_y, 80 + code_w, sub_y + 200], radius=16, fill=(18, 20, 26, 240), outline=(42, 46, 60, 255), width=2)
    
    font_sub = get_font(28, bold=False)
    words = scene["subtitle"].split()
    lines = []
    curr = []
    for w in words:
        curr.append(w)
        if len(" ".join(curr)) > 44:
            lines.append(" ".join(curr[:-1]))
            curr = [w]
    if curr:
        lines.append(" ".join(curr))
    for i, line in enumerate(lines[:3]):
        draw.text((110, sub_y + 40 + i * 40), line, font=font_sub, fill=(240, 240, 245, 255), anchor="lm")
        
    font_brand = get_font(18, bold=True, mono=True)
    draw.text((80, 975), "MINIMAL OS  |  REAL-TIME ARCHITECTURE SHOWCASE  |  GOOGLE PLAY", font=font_brand, fill=(140, 145, 160, 255))
    
    # Right Column: Virtual Phone Running Minimal OS (x: 1040 to 1840)
    phone_w = 460
    phone_h = 920
    phone_x = 1200
    phone_y = 70
    
    active_state = get_current_phone_state(scene, t_local)
    ss_img = cache.get(active_state["img"])
    
    # Draw frame and paste real screenshot
    draw.rounded_rectangle([phone_x - 6, phone_y - 6, phone_x + phone_w + 6, phone_y + phone_h + 6], radius=34, fill=(18, 18, 22, 255), outline=(55, 58, 70, 255), width=2)
    if ss_img:
        mask = Image.new("L", (phone_w, phone_h), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, phone_w, phone_h], radius=28, fill=255)
        img.paste(ss_img, (phone_x, phone_y), mask)
    draw.rounded_rectangle([phone_x, phone_y, phone_x + phone_w, phone_y + phone_h], radius=28, outline=(55, 58, 70, 255), width=2)
    
    # State Action Caption Pill on top of phone
    cap_text = active_state.get("caption", "")
    font_cap = get_font(18, bold=True, mono=True)
    draw.rounded_rectangle([phone_x - 80, phone_y + phone_h + 16, phone_x + phone_w + 80, phone_y + phone_h + 54], radius=10, fill=(22, 22, 28, 240), outline=(229, 57, 53, 200), width=1)
    draw.text((phone_x + phone_w // 2, phone_y + phone_h + 35), f"ACTUAL APP: {cap_text}", font=font_cap, fill=(255, 255, 255, 255), anchor="mm")
    
    return img

def render_vertical_frame(scene, t_local, total_dur, cache, width=1080, height=1920):
    img = Image.new("RGBA", (width, height), (10, 10, 12, 255))
    draw = ImageDraw.Draw(img)
    
    # Top decorative glow
    for y in range(300):
        alpha = int(28 * (1 - y / 300))
        draw.line([(0, y), (width, y)], fill=(229, 57, 53, alpha))
        
    # Header Area
    draw.line([(80, 60), (220, 60)], fill=(229, 57, 53, 255), width=3)
    font_tag = get_font(24, bold=True, mono=True)
    draw.text((80, 85), scene["tag"], font=font_tag, fill=(229, 57, 53, 255))
    
    font_head = get_font(52, bold=True)
    draw.text((80, 130), scene["headline"], font=font_head, fill=(245, 245, 248, 255))
    
    # Compact Code Snippet Card (y: 245 to 455)
    code_w = width - 160
    code_h = 220
    code_x = 80
    code_y = 250
    draw_code_editor_window(draw, code_x, code_y, code_w, code_h, scene["code_file"], scene["code_lines"][:5])
    
    # Virtual Phone Center Stage (y: 495 to 1545)
    phone_w = 640
    phone_h = 1050
    phone_x = (width - phone_w) // 2
    phone_y = 495
    
    active_state = get_current_phone_state(scene, t_local)
    ss_img = cache.get(active_state["img"])
    
    draw.rounded_rectangle([phone_x - 6, phone_y - 6, phone_x + phone_w + 6, phone_y + phone_h + 6], radius=34, fill=(18, 18, 22, 255), outline=(55, 58, 70, 255), width=2)
    if ss_img:
        mask = Image.new("L", (phone_w, phone_h), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, phone_w, phone_h], radius=28, fill=255)
        img.paste(ss_img, (phone_x, phone_y), mask)
    draw.rounded_rectangle([phone_x, phone_y, phone_x + phone_w, phone_y + phone_h], radius=28, outline=(55, 58, 70, 255), width=2)
    
    # Caption pill below phone
    cap_text = active_state.get("caption", "")
    font_cap = get_font(20, bold=True, mono=True)
    draw.rounded_rectangle([phone_x, phone_y + phone_h + 14, phone_x + phone_w, phone_y + phone_h + 54], radius=10, fill=(22, 22, 28, 240), outline=(229, 57, 53, 220), width=1)
    draw.text((phone_x + phone_w // 2, phone_y + phone_h + 34), f"RUNNING DEVICE: {cap_text}", font=font_cap, fill=(255, 255, 255, 255), anchor="mm")
    
    # Subtitle Card at bottom (y: 1610 to 1820)
    sub_y = 1610
    draw.rounded_rectangle([60, sub_y, width - 60, sub_y + 190], radius=18, fill=(18, 20, 26, 240), outline=(42, 46, 60, 255), width=2)
    
    font_sub = get_font(32, bold=False)
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
        draw.text((width // 2, sub_y + 44 + i * 44), line, font=font_sub, fill=(240, 240, 245, 255), anchor="mm")
        
    font_brand = get_font(20, bold=True, mono=True)
    draw.text((width // 2, 1850), "MINIMAL OS  |  TEXT-FIRST VELOCITY  |  GOOGLE PLAY", font=font_brand, fill=(140, 145, 160, 255), anchor="mm")
    
    return img

def render_scenes_to_video(scenes, orientation="vertical"):
    width, height = (1080, 1920) if orientation == "vertical" else (1920, 1080)
    phone_w, phone_h = (640, 1050) if orientation == "vertical" else (460, 920)
    
    print(f"\n=======================================================")
    print(f"Pre-caching screenshots for {orientation} ({phone_w}x{phone_h})...")
    cache = pre_cache_screenshots(phone_w, phone_h)
    print(f"Cached {len(cache)} real screenshots.")
    
    print(f"Rendering {orientation} video ({width}x{height}) via ffmpeg pipe...")
    print(f"=======================================================")
    
    fps = 24
    scene_mp4s = []
    
    for idx, scene in enumerate(scenes):
        duration = scene["duration"]
        total_frames = int(duration * fps)
        scene_mp4 = os.path.join(TEMP_DIR, f"vp_{orientation}_{scene['id']}.mp4")
        
        print(f"Encoding Scene {scene['id']} ({duration:.2f}s, {total_frames} frames)...")
        
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
            if orientation == "vertical":
                frame_img = render_vertical_frame(scene, t_local, duration, cache, width, height)
            else:
                frame_img = render_landscape_frame(scene, t_local, duration, cache, width, height)
                
            proc.stdin.write(frame_img.tobytes())
            
        proc.stdin.close()
        proc.wait()
        
        if proc.returncode != 0:
            raise RuntimeError(f"FFmpeg failed with code {proc.returncode} on scene {scene['id']}")
            
        scene_mp4s.append(scene_mp4)
        print(f"Finished Scene {scene['id']} -> {scene_mp4}")

    concat_txt = os.path.join(TEMP_DIR, f"concat_vp_{orientation}.txt")
    with open(concat_txt, "w") as f:
        for fpath in scene_mp4s:
            f.write(f"file '{fpath}'\n")
            
    output_filename = f"minimal_os_virtual_phone_code_{'vertical_9x16' if orientation == 'vertical' else 'landscape_16x9'}.mp4"
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
    print("\nALL VIRTUAL PHONE SHOWCASE VIDEOS SUCCESSFULLY GENERATED!")
    print(f"Vertical (9:16):   {v_out}")
    print(f"Landscape (16:9):  {l_out}")
