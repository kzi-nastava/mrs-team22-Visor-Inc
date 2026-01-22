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
