export interface LoginDto {
  email: string;
  password: string;
}

export interface TokenDto {
  user: User;
  refreshToken: string;
  accessToken: string;
}

export interface ResetPasswordDto {
  token: string;
  password: string;
}

export interface RegistrationDto {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

