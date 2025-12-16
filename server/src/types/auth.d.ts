export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  userId: string;
  accessToken: string;
}

export interface JwtPayload {
  userId: string;
  iat: number;
  exp: number;
}
