export type RegisterResponse = {
  id: string;
  email: string;
};

export type LoginOrRefreshTokenResponse = {
  accessToken: string;
  refreshToken: string;
};
