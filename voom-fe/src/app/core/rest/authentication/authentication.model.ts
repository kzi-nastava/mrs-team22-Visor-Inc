export interface SignInRequest {
  email: string;
  password: string;
}

export interface SignInResponse {
  user: User;
  refreshToken: string;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  verified: boolean;
}
