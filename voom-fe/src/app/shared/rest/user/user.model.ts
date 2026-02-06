export enum UserStatus {
  INACTIVE,
  ACTIVE,
  SUSPENDED,
  PENDING,
  NOTACTIVATED,
  BLOCKED,
}

export interface UserProfileDto {
  id: number;
  firstName: string;
  lastName: string;
  pfpUrl: string | null;
  userStatus: string;
  email: string;
  phoneNumber: string;
  birthDate: string;
  address: string;
  userRoleId: number;
  status?: string;
}

export interface CreateUserDto {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
  birthDate: string; // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
  email: string;
  password: string;
  userStatus: string;
  userRoleId: number;
}
