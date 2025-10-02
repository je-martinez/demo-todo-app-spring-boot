export type UserJWTDecoded = {
  sub: string;
  type: string;
  email: string;
  iat: number;
  exp: number;
};
