import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginOrRefreshTokenResponse, RegisterResponse } from '@app/types';
import { environment } from '@environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthApi {
  constructor(private http: HttpClient) {}

  register(email: string, password: string) {
    return this.http.post<RegisterResponse>(`${environment.BASE_URL}/api/auth/register`, {
      email,
      password,
    });
  }

  login(email: string, password: string) {
    return this.http.post<LoginOrRefreshTokenResponse>(`${environment.BASE_URL}/api/auth/login`, {
      email,
      password,
    });
  }

  refreshToken(refreshToken: string) {
    return this.http.post<LoginOrRefreshTokenResponse>(`${environment.BASE_URL}/api/auth/refresh`, {
      refreshToken,
    });
  }
}
