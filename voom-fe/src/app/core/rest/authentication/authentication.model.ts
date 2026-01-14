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

export interface ForgotPasswordDto {
  email: string;
}

export interface ResetPasswordDto {
  password: string;
  confirmPassword: string;
}
