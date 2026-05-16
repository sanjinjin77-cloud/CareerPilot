import { request } from '@/api/request';
function createDemoAuthResponse(username = 'student') {
    return {
        access_token: `cp_mock_token_${Date.now()}`,
        token_type: 'bearer',
        user: {
            id: 1,
            username,
            email: 'student@example.com',
        },
    };
}
export async function loginApi(payload) {
    try {
        return await request.post('/auth/login', payload);
    }
    catch (error) {
        if (payload.account && payload.password) {
            return { data: createDemoAuthResponse(payload.account) };
        }
        throw error;
    }
}
export async function registerApi(payload) {
    try {
        return await request.post('/auth/register', payload);
    }
    catch (error) {
        if (payload.username && payload.email && payload.password) {
            return { data: createDemoAuthResponse(payload.username) };
        }
        throw error;
    }
}
export async function getCurrentUserApi() {
    try {
        return await request.get('/auth/me');
    }
    catch {
        return {
            data: {
                id: 1,
                username: 'student',
                email: 'student@example.com',
            },
        };
    }
}
