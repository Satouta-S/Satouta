
import asyncio, time

_pending_confirmed = None

async def execute_safe(plan, user_text, memory):
    global _pending_confirmed
    if plan.get("action") == "REMINDER":
        # Simulate creating a reminder locally (no real calendar yet)
        return f"تمام. سجلت تذكير: {plan['text']} في {plan['when']}."
    if plan.get("action") == "ANSWER":
        return plan.get("reply", "تمام.")
    if plan.get("tool_proposal"):
        _pending_confirmed = plan["tool_proposal"]
        return "عايزني أنفذ ده؟ قول: موافق أو أكد التنفيذ."
    # Small talk default
    return small_talk(user_text, memory)

async def execute_confirmed():
    global _pending_confirmed
    if not _pending_confirmed:
        return "لا يوجد إجراء معلق."
    tool = _pending_confirmed["tool"]
    args = _pending_confirmed["args"]
    _pending_confirmed = None
    # Simulate
    await asyncio.sleep(0.2)
    if tool == "CALL":
        return f"اتصال بـ {args.get('contact','جهة اتصال')} (محاكاة)."
    return "تم."

def small_talk(user_text, memory):
    # Simple empathetic Arabic response using last turn
    if not user_text or "لم أسمعك" in user_text:
        return "ممكن تعيدها بصوت أوضح؟"
    if "عامل ايه" in user_text or "اخبارك" in user_text:
        return "تمام الحمد لله يا محمود. طمني عليك."
    # Mirror back with warmth
    return f"سمعتك بتقول: {user_text}. تحب نحكي شوية ولا تعمل حاجة معينة؟"
