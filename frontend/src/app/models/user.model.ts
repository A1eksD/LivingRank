export interface User {
  id: string;
  email: string;
  displayName: string;
  authProvider: string;
  emailVerified: boolean;
  profileImageUrl?: string;
  role?: string;
  status?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: User;
}

export interface RegisterRequest {
  email: string;
  password: string;
  displayName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UpdateProfileRequest {
  displayName?: string;
  profileImageUrl?: string;
}

export interface MessageResponse {
  message: string;
}
