export interface LoginDto {
  email: string;
  password: string;
}

export interface TokenDto {
  user: User;
  refreshToken: string;
  accessList: Access[];
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
}

export interface Authority {
  readonly contextId: number;
  readonly contextName: string;
  readonly mainContext: boolean;
  readonly active: boolean;
  readonly authenticationToken: string;
  readonly roleNames: string[];
  readonly permissions: string[];
}

export interface Access {
  resource: string;
  permissions: string[];
}

