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
  http = inject(HttpClient);

  public getTasks(): Observable<GetTasksResponse> {
    return this.http.get<GetTasksResponse>(`${environment.BASE_URL}/api/tasks/by-owner`);
  }

  public getTask(id: string): Observable<GetTaskResponse> {
    return this.http.get<GetTaskResponse>(`${environment.BASE_URL}/api/tasks/${id}`);
  }

  public createTask(
    title: string,
    description: string,
    cover: string
  ): Observable<CreateTaskResponse> {
    return this.http.post<CreateTaskResponse>(`${environment.BASE_URL}/api/tasks`, {
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
    return this.http.put<UpdateTaskResponse>(`${environment.BASE_URL}/api/tasks/${id}`, {
      title,
      description,
      cover,
    });
  }

  public deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.BASE_URL}/api/tasks/${id}`);
  }

  public markAsUncompleted(id: string): Observable<CompleteTaskResponse> {
    return this.http.put<CompleteTaskResponse>(
      `${environment.BASE_URL}/api/tasks/mark-as-uncompleted${id}`,
      {}
    );
  }

  public markAsCompleted(id: string): Observable<CompleteTaskResponse> {
    return this.http.put<CompleteTaskResponse>(
      `${environment.BASE_URL}/api/tasks/mark-as-completed${id}`,
      {}
    );
  }
}
