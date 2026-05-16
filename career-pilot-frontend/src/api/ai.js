import { javaRequest } from '@/api/request';
function unwrapResult(result) {
    if (result.code !== 200) {
        throw new Error(result.message || '请求失败');
    }
    return result.data;
}
export async function sendAiChatApi(payload) {
    const response = await javaRequest.post('/ai/chat', {
        userId: payload.userId || 1,
        message: payload.message,
    });
    return unwrapResult(response.data);
}
export async function getAiMessagesApi(userId = 1) {
    const response = await javaRequest.get('/ai/messages', {
        params: { userId },
    });
    return unwrapResult(response.data);
}
