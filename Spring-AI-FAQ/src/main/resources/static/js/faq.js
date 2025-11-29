const chatBox = document.getElementById("chat-box");
const questionInput = document.getElementById("question");
const cidInput = document.getElementById("cid");

// 在聊天框中加入一行文字
function append(text) {
    chatBox.innerText += text + "\n";
    chatBox.scrollTop = chatBox.scrollHeight; // 自動卷到最下面
}

// 呼叫後端 /faq/ask
function ask() {
    const q = questionInput.value.trim();
    if (!q) return;

    append("你：" + q);
    questionInput.value = "";

    const params = new URLSearchParams();
    params.append("q", q);

    const cid = cidInput.value.trim();
    if (cid) {
        params.append("conversationId", cid);
    }

    fetch("/faq/ask?" + params.toString())
        .then(res => res.text())
        .then(text => {
            append("AI：" + text);
            append(""); // 空行分隔
        })
        .catch(err => {
            console.error(err);
            append("AI：呼叫發生錯誤，請稍後再試。\n");
        });
}

// 按 Enter 也可以送出
questionInput.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        ask();
    }
});
