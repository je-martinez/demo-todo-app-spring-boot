import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import {
  CompleteTaskResponse,
  CreateTaskResponse,
  GetTaskResponse,
  GetTasksResponse,
  UpdateTaskResponse,
} from '@app/types';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TasksApi {
  private readonly BASE_URL = `${environment.BASE_URL}/api/todos`;
  http = inject(HttpClient);

  public getTasks(): Observable<GetTasksResponse> {
    return this.http.get<GetTasksResponse>(`${this.BASE_URL}/by-owner`);
  }

  public getTask(id: string): Observable<GetTaskResponse> {
    return this.http.get<GetTaskResponse>(`${this.BASE_URL}/${id}`);
  }

  public createTask(
    title: string,
    description: string,
    cover: string
  ): Observable<CreateTaskResponse> {
    return this.http.post<CreateTaskResponse>(`${this.BASE_URL}`, {
      title,
      description,
      cover,
    });
  }

  public updateTask(
    id: string,
    title: string,
    description: string,
    cover: string
  ): Observable<UpdateTaskResponse> {
    return this.http.put<UpdateTaskResponse>(`${this.BASE_URL}/${id}`, {
      title,
      description,
      cover,
    });
  }

  public deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}/${id}`);
  }

  public markAsUncompleted(id: string): Observable<CompleteTaskResponse> {
    return this.http.patch<CompleteTaskResponse>(`${this.BASE_URL}/mark-as-uncompleted/${id}`, {});
  }

  public markAsCompleted(id: string): Observable<CompleteTaskResponse> {
    return this.http.patch<CompleteTaskResponse>(`${this.BASE_URL}/mark-as-completed/${id}`, {});
  }
}
