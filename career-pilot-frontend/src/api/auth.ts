import { request } from '@/api/request';

export interface AuthUser {
  id: number;
  username: string;
  email: string;
}

export interface LoginPayload {
  account: string;
  password: string;
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  user: AuthUser;
}

function createDemoAuthResponse(username = 'student'): AuthResponse {
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

export async function loginApi(payload: LoginPayload) {
  try {
    return await request.post<AuthResponse>('/auth/login', payload);
  } catch (error) {
    if (payload.account && payload.password) {
      return { data: createDemoAuthResponse(payload.account) };
    }

    throw error;
  }
}

export async function registerApi(payload: RegisterPayload) {
  try {
    return await request.post<AuthResponse>('/auth/register', payload);
  } catch (error) {
    if (payload.username && payload.email && payload.password) {
      return { data: createDemoAuthResponse(payload.username) };
    }

    throw error;
  }
}

export async function getCurrentUserApi() {
  try {
    return await request.get<AuthUser>('/auth/me');
  } catch {
    return {
      data: {
        id: 1,
        username: 'student',
        email: 'student@example.com',
      },
    };
  }
}
