import subprocess
import time
import os

SCREENSHOTS_DIR = "/Users/zaryif/Minimal_os/store_listing/screenshots_real"
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

def adb(cmd):
    if isinstance(cmd, str):
        cmd = cmd.split()
    return subprocess.run(["adb"] + cmd, capture_output=True, text=True)

def tap(x, y):
    adb(["shell", "input", "tap", str(x), str(y)])
    time.sleep(0.4)

def type_text(text):
    escaped = text.replace(" ", "%s")
    adb(["shell", "input", "text", escaped])
    time.sleep(0.4)

def keyevent(code):
    adb(["shell", "input", "keyevent", str(code)])
    time.sleep(0.4)

def capture(filename):
    path = os.path.join(SCREENSHOTS_DIR, filename)
    with open(path, "wb") as f:
        subprocess.run(["adb", "exec-out", "screencap", "-p"], stdout=f, check=True)
    print(f"Captured: {path}")
    return path

def main():
    print("Automating real emulator captures...")
    
    # 1. Capture clean home screen
    keyevent(4) # Back
    keyevent(4) # Back to home
    time.sleep(0.5)
    capture("real_01_home_screen.png")
    
    # 2. Type SMS command: @Zaryif On my way
    tap(498, 2243) # Tap command bar
    time.sleep(0.4)
    type_text("@Zaryif%sOn%smy%sway")
    time.sleep(0.5)
    capture("real_02_typing_message.png")
    
    # Execute SMS
    keyevent(66) # Enter
    time.sleep(0.6)
    capture("real_03_executed_message.png")
    
    # 3. Type Call command: #Zaryif
    tap(498, 2243)
    time.sleep(0.4)
    type_text("#Zaryif")
    time.sleep(0.5)
    capture("real_04_typing_call.png")
    
    # Clear command
    for _ in range(8):
        keyevent(67)
    time.sleep(0.2)
    
    # 4. Type To-Do: - Finish my project
    type_text("-%sFinish%smy%sproject")
    time.sleep(0.5)
    capture("real_05_typing_todo.png")
    keyevent(66) # Execute
    time.sleep(0.6)
    capture("real_06_executed_todo.png")
    
    # 5. Type Note: ! Life is always good when you have Minimal OS as your default
    tap(498, 2243)
    time.sleep(0.4)
    type_text("!%sLife%sis%salways%sgood%swhen%syou%shave%sMinimal%sOS")
    time.sleep(0.5)
    capture("real_07_typing_note.png")
    keyevent(66) # Execute
    time.sleep(0.6)
    capture("real_08_executed_note.png")
    
    # 6. Type Alias: alias yt = https://youtube.com
    tap(498, 2243)
    time.sleep(0.4)
    type_text("alias%syt%s=%shttps://youtube.com")
    time.sleep(0.5)
    capture("real_09_typing_alias.png")
    keyevent(66) # Execute
    time.sleep(0.6)
    capture("real_10_executed_alias.png")
    
    # 7. Type Macro: yt lofi beats
    tap(498, 2243)
    time.sleep(0.4)
    type_text("yt%slofi%sbeats")
    time.sleep(0.5)
    capture("real_11_typing_macro.png")
    for _ in range(15):
        keyevent(67)
    time.sleep(0.2)
    
    # 8. Start Pomodoro timer: +25m
    type_text("+25m")
    time.sleep(0.4)
    capture("real_12_typing_timer.png")
    keyevent(66) # Execute
    time.sleep(0.8)
    capture("real_13_executed_timer.png")
    
    # 9. Real Terminal Buffer & Command History stream
    tap(498, 2243) # Tap command bar while history exists
    time.sleep(0.6)
    capture("real_14_terminal_buffer_stream.png")
    
    # Cycle history with up arrow button or key
    # In CommandBar, up arrow button is around x=850, y=1440 or we send DPAD_UP
    keyevent(19) # DPAD_UP
    time.sleep(0.4)
    capture("real_15_history_cycled.png")
    
    # 10. Open Command Guide Dialog
    # Tap the Help button (question mark / terminal guide icon at x=950, y=2243)
    keyevent(4) # Dismiss keyboard
    time.sleep(0.4)
    tap(950, 2243) # Tap help icon
    time.sleep(0.6)
    capture("real_16_command_guide_dialog.png")
    
    print("All real emulator captures complete!")

if __name__ == "__main__":
    main()
