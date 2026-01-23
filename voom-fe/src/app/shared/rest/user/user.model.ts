export enum UserStatus {
  INACTIVE,
  ACTIVE,
  SUSPENDED,
  PENDING,
  NOTACTIVATED
}

export interface UserProfileDto {
  id: number;
  firstName: string;
  lastName: string;
  pfpUrl: string | null;
  userStatus: UserStatus;
  email: string;
  phoneNumber: string;
  birthDate: Date;
  address: string;
  userRoleId: number;
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
