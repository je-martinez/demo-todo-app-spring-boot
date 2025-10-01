import { jwtDecode } from 'jwt-decode';

export const decodeJwt = (token: string) => jwtDecode(token);
