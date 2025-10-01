import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginOrRefreshTokenResponse, RegisterResponse } from '@app/types';

@Injectable({
  providedIn: 'root',
})
export class AuthApi {
  constructor(private http: HttpClient) {}

  register(email: string, password: string) {
    return this.http.post<RegisterResponse>('/api/auth/register', { email, password });
  }

  login(email: string, password: string) {
    return this.http.post<LoginOrRefreshTokenResponse>('/api/auth/login', { email, password });
  }

  refreshToken(refreshToken: string) {
    return this.http.post<LoginOrRefreshTokenResponse>('/api/auth/refresh-token', { refreshToken });
  }
}
