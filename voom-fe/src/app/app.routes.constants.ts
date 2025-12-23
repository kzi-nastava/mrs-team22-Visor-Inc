// export const ROUTE_USER_PROFILE = 'profile';
// export const ROUTE_DRIVER_HOME = 'driverHome';
// export const ROUTE_DRIVER_RIDE_HISTORY = 'driver/rideHistory';
// export const ROUTE_HOME = 'home';
// export const ROUTE_LOGIN = 'login';
// export const ROUTE_FORGOT_PASSWORD = 'forgotPassword';
// export const ROUTE_RESET_PASSWORD = 'resetPassword';
// export const ROUTE_REGISTRATION = 'registration';

export const APP_ROUTES = {
  HOME: '',
  AUTH: {
    LOGIN: 'login',
    REGISTRATION: 'registration',
    FORGOT_PASSWORD: 'forgotPassword',
    RESET_PASSWORD: 'resetPassword',
  },
  USER: {
    PROFILE: 'profile',
  },
  DRIVER: {
    HOME: 'driverHome',
    RIDE_HISTORY: 'driver/rideHistory',
  },
} as const;
