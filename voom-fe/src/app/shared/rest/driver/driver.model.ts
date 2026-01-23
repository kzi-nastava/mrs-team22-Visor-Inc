export interface DriverDto {
  id?: number;
  firstName: string;
  lastName: string;
  pfpUrl?: string;
  status?: string;
  userStatus?: string;
  email: string;
  phoneNumber: string;
  birthDate: Date;
  address: string;
}

export interface AdminCreateDriverDto {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
  birthDate: string;
  email: string;
  password: string;
  userStatus: string;
  userRoleId: number;
}
