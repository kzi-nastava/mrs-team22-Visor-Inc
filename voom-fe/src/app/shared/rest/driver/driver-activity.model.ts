export interface DriverStateChangeDto {
  userId: number,
  currentState: string,
  performedAt: string,
}

export enum DriverState {
  INACTIVE,
  ACTIVE
}
