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
  expires_in: number;
  user: AuthUser;
}

export async function loginApi(payload: LoginPayload) {
  return request.post<AuthResponse>('/auth/login', payload);
}

export async function registerApi(payload: RegisterPayload) {
  return request.post<AuthResponse>('/auth/register', payload);
}

export async function getCurrentUserApi() {
  return request.get<AuthUser>('/auth/me');
}
