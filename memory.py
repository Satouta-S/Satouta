
import json, os, time
PATH = "/tmp/setouta_memory.json"

def load(user_id: str):
    if not os.path.exists(PATH):
        return {"user_id": user_id, "turns": []}
    try:
        data = json.load(open(PATH, "r", encoding="utf-8"))
    except Exception:
        data = {}
    return data.get(user_id, {"user_id": user_id, "turns": []})

def update(user_id: str, user_text: str, reply: str, intent: str):
    data = {}
    if os.path.exists(PATH):
        try:
            data = json.load(open(PATH, "r", encoding="utf-8"))
        except Exception:
            data = {}
    user_obj = data.get(user_id, {"user_id": user_id, "turns": []})
    user_obj["turns"].append({
        "ts": time.time(),
        "user": user_text,
        "bot": reply,
        "intent": intent
    })
    # keep last 200 turns
    user_obj["turns"] = user_obj["turns"][-200:]
    data[user_id] = user_obj
    with open(PATH, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    return user_obj
