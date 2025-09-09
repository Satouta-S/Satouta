
import re

def detect_intent(text: str) -> str:
    t = text or ""
    if any(k in t for k in ["اتصل", "كلمي", "اتصال"]):
        return "CALL_INTENT"
    if any(k in t for k in ["ذكريني", "تذكير", "منبه"]):
        return "REMINDER_INTENT"
    if any(k in t for k in ["الساعة", "وقت"]):
        return "TIME_INTENT"
    return "SMALL_TALK"

def plan(intent: str, text: str, memory) -> dict:
    if intent == "CALL_INTENT":
        return {"tool_proposal": {"tool":"CALL", "args":{"contact":"جاسم"}, "summary":"اتصال بجاسم الآن"}}
    if intent == "REMINDER_INTENT":
        return {"action": "REMINDER", "when": "غدًا 9 صباحًا", "text": "تذكير بدفع DHL"}
    if intent == "TIME_INTENT":
        return {"action": "ANSWER", "reply": "الساعة تقريبًا دلوقتي حسب جهازك."}
    return {"action": "CHAT", "style":"friendly"}
